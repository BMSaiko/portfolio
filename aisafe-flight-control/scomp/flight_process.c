#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include "simulation.h"
#include "flight_process.h"

static volatile sig_atomic_t g_stop = 0;

typedef struct {
    const char *mode;

    double start_lat;
    double start_lon;
    double start_alt;

    double end_lat;
    double end_lon;
    double end_alt;

    double progress_per_step;
} FlightSegment;

static const FlightSegment FLIGHT_PLAN_SEGMENTS[] = {
    {
        "climb",
        41.262891, -8.685220, 69.0,
        42.000000, -8.010000, 9249.0,
        0.08
    },
    {
        "cruise",
        42.000000, -8.010000, 9249.0,
        41.000000, -4.300000, 9249.0,
        0.05
    },
    {
        "descend",
        41.000000, -4.300000, 9249.0,
        40.489500, -3.564300, 610.0,
        0.08
    }
};

static const int FLIGHT_PLAN_SEGMENT_COUNT =
        sizeof(FLIGHT_PLAN_SEGMENTS) / sizeof(FLIGHT_PLAN_SEGMENTS[0]);

static void handle_SIGTERM(int signo, siginfo_t *sinfo, void *ctx)
{
    (void)signo;
    (void)sinfo;
    (void)ctx;

    g_stop = 1;
}

static void handle_SIGUSR1(int signo, siginfo_t *sinfo, void *ctx)
{
    (void)signo;
    (void)sinfo;
    (void)ctx;

    const char msg[] = "[US102] Flight process received SIGUSR1: safety violation detected\n";
    write(STDOUT_FILENO, msg, sizeof(msg) - 1);
}

static void setup_child_signals(void)
{
    struct sigaction act;

    memset(&act, 0, sizeof(act));
    sigemptyset(&act.sa_mask);
    act.sa_sigaction = handle_SIGTERM;
    act.sa_flags = SA_SIGINFO;
    sigaction(SIGTERM, &act, NULL);

    memset(&act, 0, sizeof(act));
    sigfillset(&act.sa_mask);
    sigdelset(&act.sa_mask, SIGTERM);
    act.sa_sigaction = handle_SIGUSR1;
    act.sa_flags = SA_SIGINFO | SA_RESTART;
    sigaction(SIGUSR1, &act, NULL);
}

static double interpolate(double start, double end, double progress)
{
    return start + ((end - start) * progress);
}

static double flight_offset(const char *flight_id)
{
    size_t len = strlen(flight_id);

    if (len == 0) {
        return 0.0;
    }

    return (double)(flight_id[len - 1] % 3) * 0.5;
}

static int wait_for_step_signal(sem_t *step_go_sem)
{
    while (!g_stop) {
        if (sem_wait(step_go_sem) == 0) {
            return 0;
        }

        if (errno != EINTR) {
            return -1;
        }
    }

    return -1;
}

static void update_position_from_flight_plan(PositionUpdate *pos,
                                             int *current_segment_index,
                                             double *segment_progress,
                                             double offset)
{
    if (*current_segment_index >= FLIGHT_PLAN_SEGMENT_COUNT) {
        pos->active = 0;
        return;
    }

    const FlightSegment *segment = &FLIGHT_PLAN_SEGMENTS[*current_segment_index];

    *segment_progress += segment->progress_per_step;

    if (*segment_progress > 1.0) {
        *segment_progress = 1.0;
    }

    pos->lat = interpolate(segment->start_lat, segment->end_lat, *segment_progress) + offset;
    pos->lon = interpolate(segment->start_lon, segment->end_lon, *segment_progress) + offset;
    pos->altitude = interpolate(segment->start_alt, segment->end_alt, *segment_progress);

    pos->vx = segment->end_lat - segment->start_lat;
    pos->vy = segment->end_lon - segment->start_lon;
    pos->vz = segment->end_alt - segment->start_alt;

    if (*segment_progress >= 1.0) {
        (*current_segment_index)++;
        *segment_progress = 0.0;

        if (*current_segment_index >= FLIGHT_PLAN_SEGMENT_COUNT) {
            pos->active = 0;
        }
    }
}

void flight_process_run(const char *flight_id,
                        int write_fd,
                        sem_t *step_go_sem,
                        sem_t *step_done_sem,
                        const SimParams *params)
{
    setup_child_signals();

    PositionUpdate pos;
    memset(&pos, 0, sizeof(pos));

    strncpy(pos.flight_id, flight_id, MAX_FLIGHT_ID - 1);
    pos.flight_id[MAX_FLIGHT_ID - 1] = '\0';

    double offset = flight_offset(flight_id);

    pos.lat = FLIGHT_PLAN_SEGMENTS[0].start_lat + offset;
    pos.lon = FLIGHT_PLAN_SEGMENTS[0].start_lon + offset;
    pos.altitude = FLIGHT_PLAN_SEGMENTS[0].start_alt;
    pos.vx = 0.0;
    pos.vy = 0.0;
    pos.vz = 0.0;
    pos.active = 1;

    int step = params->time_start;
    int current_segment_index = 0;
    double segment_progress = 0.0;

    while (step < params->time_end && !g_stop && pos.active) {
        if (wait_for_step_signal(step_go_sem) != 0) {
            break;
        }

        pos.time_step = step;

        update_position_from_flight_plan(
                &pos,
                &current_segment_index,
                &segment_progress,
                offset
        );

        if (write(write_fd, &pos, sizeof(pos)) < 0) {
            break;
        }

        sem_post(step_done_sem);

        step += params->time_step_secs;
    }

    close(write_fd);
}
