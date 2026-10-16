#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include "report.h"

static int count_violations_from_log(const char *path){
    FILE *f = fopen(path, "r");
    if (f == NULL)
        return 0;

    int count = 0;
    char line[256];
    while (fgets(line, sizeof(line), f) != NULL)
        count++;

    fclose(f);
    return count;
}

static int count_violations_for_flight(const char *path, const char *flight_id){
    FILE *f = fopen(path, "r");
    if (f == NULL)
        return 0;

    int count = 0;
    char line[256];
    while (fgets(line, sizeof(line), f) != NULL) {
        if (strstr(line, flight_id) != NULL)
            count++;
    }

    fclose(f);
    return count;
}

void run_report_process(const ReportData *data){
    time_t now = time(NULL);
    struct tm *t = localtime(&now);

    char filename[64];
    strftime(filename, sizeof(filename),
             "simulation_report_%Y%m%d_%H%M%S.txt", t);

    FILE *f = fopen(filename, "w");
    if (f == NULL) {
        perror("[US109] fopen report");
        return;
    }

    char timestamp[32];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", t);

    int total_violations = count_violations_from_log("violations.log");
    int total_steps = (data->time_end - data->time_start) / data->time_step_secs;

    fprintf(f, "========================================\n");
    fprintf(f, "  SIMULATION REPORT\n");
    fprintf(f, "========================================\n");
    fprintf(f, "Generated  : %s\n", timestamp);
    fprintf(f, "Area       : %s\n", data->area_code);
    fprintf(f, "Time range : %d -- %d s (step %d s, %d steps total)\n",
            data->time_start, data->time_end,
            data->time_step_secs, total_steps);
    fprintf(f, "Threshold  : %d km\n", data->safety_threshold);
    fprintf(f, "Flights    : %d\n", data->num_flights);
    fprintf(f, "----------------------------------------\n\n");

    fprintf(f, "FLIGHT DETAILS\n");
    fprintf(f, "%-16s  %-12s  %-14s  %-10s  %s\n",
            "Flight ID", "Status", "History steps",
            "Violations", "Final position");
    fprintf(f, "%-16s  %-12s  %-14s  %-10s  %s\n",
            "---------", "------", "-------------",
            "----------", "--------------");

    for (int i = 0; i < data->num_flights; i++) {
        const AircraftState *a = &data->fleet[i];
        const char *status = (a->violation_count >= 3)
                     ? "TERMINATED" : "COMPLETED";
        int flight_violations = count_violations_for_flight(
                "violations.log", a->flight_id);

        fprintf(f, "%-16s  %-12s  %-14d  %-10d  lat=%.4f lon=%.4f alt=%.0f\n",
                a->flight_id,
                status,
                a->history_count,
                flight_violations,
                a->current.lat,
                a->current.lon,
                a->current.altitude);
    }

    fprintf(f, "\n----------------------------------------\n");
    fprintf(f, "SAFETY VIOLATIONS: %d total\n\n", total_violations);

    if (total_violations > 0) {
        fprintf(f, "%-10s  %-16s  %-16s  %-12s  %-30s  %-30s\n",
                "Time step", "Flight 1", "Flight 2", "Distance",
                "Velocity 1 (vx,vy,vz)", "Velocity 2 (vx,vy,vz)");
        fprintf(f, "%-10s  %-16s  %-16s  %-12s  %-30s  %-30s\n",
                "---------", "--------", "--------", "--------",
                "----------------------", "----------------------");

        FILE *log = fopen("violations.log", "r");
        if (log != NULL) {
            char line[512];
            while (fgets(line, sizeof(line), log) != NULL) {
                int step = 0;
                char f1[MAX_FLIGHT_ID], f2[MAX_FLIGHT_ID];
                double dist = 0.0;
                double v1x = 0, v1y = 0, v1z = 0;
                double v2x = 0, v2y = 0, v2z = 0;
                if (sscanf(line,
                           "time_step=%d flight1=%15s flight2=%15s distance=%lfkm "
                           "v1=(%lf,%lf,%lf) v2=(%lf,%lf,%lf)",
                           &step, f1, f2, &dist,
                           &v1x, &v1y, &v1z, &v2x, &v2y, &v2z) >= 4) {
                    fprintf(f, "%-10d  %-16s  %-16s  %-12.2f  (%.1f,%.1f,%.1f)       (%.1f,%.1f,%.1f)\n",
                            step, f1, f2, dist,
                            v1x, v1y, v1z, v2x, v2y, v2z);
                }
            }
            fclose(log);
        }
    }

    int terminated = 0;
    for (int i = 0; i < data->num_flights; i++) {
        if (data->fleet[i].violation_count >= 3)
            terminated++;
    }

fprintf(f, "\n----------------------------------------\n");
if (total_violations == 0) {
    fprintf(f, "VALIDATION RESULT: PASS\n");
} else if (terminated > 0) {
    fprintf(f, "VALIDATION RESULT: FAIL\n");
    fprintf(f, "Reason: %d flight(s) terminated due to safety violations.\n",
            terminated);
} else {
    fprintf(f, "VALIDATION RESULT: FAIL\n");
    fprintf(f, "Reason: %d safety violation(s) detected, all flights completed.\n",
            total_violations);
}
fprintf(f, "========================================\n");

    fclose(f);

    printf("[US109] Report saved to %s\n", filename);
}