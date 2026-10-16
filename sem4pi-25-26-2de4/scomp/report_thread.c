#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include "report_thread.h"

#define MAX_REPORT_VIOLATIONS 512

static void write_report_file(const char *filename, const ReportThreadArgs *args,
                               const ViolationEvent *violations, int violation_count)
{
    FILE *f = fopen(filename, "w");
    if (f == NULL) {
        perror("[US111][ERROR] fopen report");
        return;
    }

    time_t now = time(NULL);
    struct tm *t = localtime(&now);
    char timestamp[32];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", t);

    int total_steps = (args->params.time_end - args->params.time_start)
                      / args->params.time_step_secs;

    fprintf(f, "========================================\n");
    fprintf(f, "  SIMULATION REPORT (US111 - Thread)\n");
    fprintf(f, "========================================\n");
    fprintf(f, "Generated  : %s\n", timestamp);
    fprintf(f, "Area       : %s\n", args->params.area_code);
    fprintf(f, "Time range : %d -- %d s (step %d s, %d steps total)\n",
            args->params.time_start, args->params.time_end,
            args->params.time_step_secs, total_steps);
    fprintf(f, "Threshold  : %d km\n", args->params.safety_threshold);
    fprintf(f, "Flights    : %d\n", args->num_flights);
    fprintf(f, "----------------------------------------\n\n");

    fprintf(f, "FLIGHT DETAILS\n");
    fprintf(f, "%-16s  %-12s  %-14s  %-10s  %s\n",
            "Flight ID", "Status", "History steps",
            "Violations", "Final position");
    fprintf(f, "%-16s  %-12s  %-14s  %-10s  %s\n",
            "---------", "------", "-------------",
            "----------", "--------------");

    int terminated = 0;
    for (int i = 0; i < args->num_flights; i++) {
        const AircraftState *a = &args->fleet[i];
        const char *status = (a->violation_count >= 3) ? "TERMINATED" : "COMPLETED";
        if (a->violation_count >= 3) terminated++;
        int flight_viols = 0;
        for (int v = 0; v < violation_count; v++) {
            if (violations[v].flight1_index == i || violations[v].flight2_index == i)
                flight_viols++;
        }
        fprintf(f, "%-16s  %-12s  %-14d  %-10d  lat=%.4f lon=%.4f alt=%.0f\n",
                a->flight_id, status, a->history_count, flight_viols,
                a->current.lat, a->current.lon, a->current.altitude);
    }

    fprintf(f, "\n----------------------------------------\n");
    fprintf(f, "SAFETY VIOLATIONS: %d total\n", violation_count);

    if (violation_count > 0) {
        fprintf(f, "\n");
        fprintf(f, "%-10s  %-16s  %-16s  %-10s  %-24s  %-24s\n",
                "Time step", "Flight 1 idx", "Flight 2 idx",
                "Distance", "Velocity 1 (vx,vy,vz)", "Velocity 2 (vx,vy,vz)");
        fprintf(f, "%-10s  %-16s  %-16s  %-10s  %-24s  %-24s\n",
                "---------", "------------", "------------",
                "--------", "----------------------", "----------------------");
        for (int i = 0; i < violation_count; i++) {
            const ViolationEvent *ev = &violations[i];
            fprintf(f, "%-10d  %-16d  %-16d  %-10.2f  (%.1f,%.1f,%.1f)    (%.1f,%.1f,%.1f)\n",
                    ev->time_step, ev->flight1_index, ev->flight2_index,
                    ev->distance_km,
                    ev->flight1_position.vx, ev->flight1_position.vy,
                    ev->flight1_position.vz,
                    ev->flight2_position.vx, ev->flight2_position.vy,
                    ev->flight2_position.vz);
        }
    }

    fprintf(f, "\n----------------------------------------\n");
    if (violation_count == 0) {
        fprintf(f, "VALIDATION RESULT: PASS\n");
    } else if (terminated > 0) {
        fprintf(f, "VALIDATION RESULT: FAIL\n");
        fprintf(f, "Reason: %d flight(s) terminated due to safety violations.\n", terminated);
    } else {
        fprintf(f, "VALIDATION RESULT: FAIL\n");
        fprintf(f, "Reason: %d safety violation(s) detected, all flights completed.\n",
                violation_count);
    }
    fprintf(f, "========================================\n");

    fclose(f);
    printf("[US111] Report saved to %s\n", filename);
}

void *report_thread_main(void *arg)
{
    ReportThreadArgs *args = (ReportThreadArgs *)arg;
    ViolationEvent *accumulated = NULL;
    int acc_count = 0;
    int acc_cap = 0;

    printf("[US106] Report generation thread started\n");

    while (1) {
        if (pthread_mutex_lock(&args->notifier->mtx) != 0) {
            perror("[US111] pthread_mutex_lock");
            break;
        }

        while (args->notifier->queue_count == 0 && args->notifier->sim_running) {
            if (pthread_cond_wait(&args->notifier->cond, &args->notifier->mtx) != 0) {
                perror("[US111] pthread_cond_wait");
                pthread_mutex_unlock(&args->notifier->mtx);
                goto cleanup;
            }
        }

        if (!args->notifier->sim_running && args->notifier->queue_count == 0) {
            pthread_mutex_unlock(&args->notifier->mtx);
            break;
        }

        if (args->notifier->queue_count > 0) {
            ViolationEvent evt;
            if (notifier_dequeue(args->notifier, &evt) == 0) {
                if (acc_count >= acc_cap) {
                    acc_cap = (acc_cap == 0) ? 64 : acc_cap * 2;
                    accumulated = realloc(accumulated, acc_cap * sizeof(ViolationEvent));
                    if (accumulated == NULL) {
                        perror("[US111] realloc");
                        pthread_mutex_unlock(&args->notifier->mtx);
                        goto cleanup;
                    }
                }
                accumulated[acc_count++] = evt;
                printf("[US107] Violation: idx=%d <-> idx=%d (%.2f km) -> report notified\n",
                       evt.flight1_index, evt.flight2_index, evt.distance_km);
            }
        }

        pthread_mutex_unlock(&args->notifier->mtx);
    }

cleanup:
    {
        time_t now = time(NULL);
        struct tm *tm_info = localtime(&now);
        char filename[64];
        strftime(filename, sizeof(filename),
                 "simulation_report_us111_%Y%m%d_%H%M%S.txt", tm_info);
        write_report_file(filename, args, accumulated, acc_count);
    }

    free(accumulated);
    printf("[US106] Report generation thread exiting\n");
    return NULL;
}
