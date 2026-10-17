#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <math.h>
#include <fcntl.h>
#include <errno.h>
#include <time.h>
#include <semaphore.h>
#include <pthread.h>
#include "report.h"
#include "simulation.h"
#include "shared_memory.h"
#include "flight_process.h"
#include "report_thread.h"

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

volatile sig_atomic_t g_terminate = 0;
volatile sig_atomic_t g_child_exited = 0;

/* US107: Global violation notifier and report thread */
static ViolationNotifier g_notifier;
static pthread_t g_report_tid;
static ReportThreadArgs g_report_args;

/* US106: Variables for detection thread */
static pthread_mutex_t g_detect_mtx = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t g_detect_cond_start = PTHREAD_COND_INITIALIZER;
static pthread_cond_t g_detect_cond_done = PTHREAD_COND_INITIALIZER;
static int g_detect_scan_requested = 0;
static int g_detect_sim_finished = 0;
static int g_detect_current_step = 0;

static SimParams *g_detect_params = NULL;
static AircraftState *g_detect_fleet = NULL;
static int g_detect_num_flights = 0;

static void handle_SIGTERM(int signo, siginfo_t *sinfo, void *ctx)
{
    (void)signo;
    (void)sinfo;
    (void)ctx;
    g_terminate = 1;
}

static void handle_SIGCHLD(int signo, siginfo_t *sinfo, void *ctx)
{
    (void)signo;
    (void)sinfo;
    (void)ctx;
    g_child_exited = 1;
}

void setup_signals(void)
{
    struct sigaction act;

    memset(&act, 0, sizeof(act));
    sigemptyset(&act.sa_mask);
    sigaddset(&act.sa_mask, SIGCHLD);
    act.sa_sigaction = handle_SIGTERM;
    act.sa_flags = SA_SIGINFO;
    sigaction(SIGTERM, &act, NULL);

    memset(&act, 0, sizeof(act));
    sigemptyset(&act.sa_mask);
    act.sa_sigaction = handle_SIGCHLD;
    act.sa_flags = SA_SIGINFO | SA_RESTART | SA_NOCLDSTOP;
    sigaction(SIGCHLD, &act, NULL);

    memset(&act, 0, sizeof(act));
    sigemptyset(&act.sa_mask);
    act.sa_sigaction = handle_SIGTERM;
    act.sa_flags = SA_SIGINFO;
    sigaction(SIGINT, &act, NULL);
}

int validate_params(const SimParams *params)
{
    if (params == NULL) {
        fprintf(stderr, "[US100][ERROR] Parametros nulos.\n");
        return -1;
    }

    if (params->num_flights <= 0 || params->num_flights > MAX_FLIGHTS) {
        fprintf(stderr, "[US100][ERROR] num_flights invalido: %d\n",
                params->num_flights);
        return -1;
    }

    if (params->lat_min >= params->lat_max ||
        params->lon_min >= params->lon_max) {
        fprintf(stderr, "[US100][ERROR] Limites geograficos invalidos.\n");
        return -1;
    }

    if (params->time_start >= params->time_end) {
        fprintf(stderr, "[US100][ERROR] Intervalo temporal invalido.\n");
        return -1;
    }

    if (params->time_step_secs <= 0) {
        fprintf(stderr, "[US100][ERROR] time_step_secs deve ser maior que zero.\n");
        return -1;
    }

    if (params->safety_threshold <= 0) {
        fprintf(stderr, "[US100][ERROR] safety_threshold deve ser maior que zero.\n");
        return -1;
    }

    if (params->max_altitude <= 0.0 || params->max_altitude > 20000.0) {
        fprintf(stderr, "[US100][ERROR] max_altitude invalida: %.0f (deve ser > 0 e <= 20000).\n",
                params->max_altitude);
        return -1;
    }

    for (int i = 0; i < params->num_flights; i++) {
        if (params->flight_ids[i][0] == '\0') {
            fprintf(stderr, "[US100][ERROR] flight_id vazio na posicao %d.\n", i);
            return -1;
        }
    }

    return 0;
}

int load_config(const char *filepath, SimParams *params)
{
    FILE *f = fopen(filepath, "r");
    if (f == NULL) {
        fprintf(stderr, "[US100][ERROR] Nao foi possivel abrir config: %s\n",
                filepath);
        return -1;
    }

    char line[256];

    while (fgets(line, sizeof(line), f) != NULL) {
        if (line[0] == '#' || line[0] == '\n' || line[0] == '\r')
            continue;

        line[strcspn(line, "\r\n")] = '\0';

        char key[64];
        char value[192];

        if (sscanf(line, "%63[^=]=%191s", key, value) != 2)
            continue;

        if (strcmp(key, "area_code") == 0) {
            strncpy(params->area_code, value, sizeof(params->area_code) - 1);
            params->area_code[sizeof(params->area_code) - 1] = '\0';
        } else if (strcmp(key, "lat_min") == 0) {
            params->lat_min = atof(value);
        } else if (strcmp(key, "lat_max") == 0) {
            params->lat_max = atof(value);
        } else if (strcmp(key, "lon_min") == 0) {
            params->lon_min = atof(value);
        } else if (strcmp(key, "lon_max") == 0) {
            params->lon_max = atof(value);
        } else if (strcmp(key, "max_altitude") == 0) {
            params->max_altitude = atof(value);
        } else if (strcmp(key, "time_start") == 0) {
            params->time_start = atoi(value);
        } else if (strcmp(key, "time_end") == 0) {
            params->time_end = atoi(value);
        } else if (strcmp(key, "time_step_secs") == 0) {
            params->time_step_secs = atoi(value);
        } else if (strcmp(key, "safety_threshold") == 0) {
            params->safety_threshold = atoi(value);
        } else if (strcmp(key, "num_flights") == 0) {
            params->num_flights = atoi(value);
        } else if (strcmp(key, "flights") == 0) {
            int idx = 0;
            char *token = strtok(value, ",");
            while (token != NULL && idx < MAX_FLIGHTS) {
                strncpy(params->flight_ids[idx], token, MAX_FLIGHT_ID - 1);
                params->flight_ids[idx][MAX_FLIGHT_ID - 1] = '\0';
                idx++;
                token = strtok(NULL, ",");
            }
        }
    }

    fclose(f);
    return 0;
}

static void store_position_update(AircraftState *aircraft, const PositionUpdate *update)
{
    aircraft->current = *update;

    if (aircraft->history_count < MAX_HISTORY_STEPS) {
        aircraft->history[aircraft->history_count] = *update;
        aircraft->history_count++;
        return;
    }

    for (int i = 1; i < MAX_HISTORY_STEPS; i++) {
        aircraft->history[i - 1] = aircraft->history[i];
    }

    aircraft->history[MAX_HISTORY_STEPS - 1] = *update;
}

static int predict_next_position(const AircraftState *aircraft, PositionUpdate *prediction)
{
    if (aircraft->history_count < 2)
        return 0;

    const PositionUpdate *previous =
            &aircraft->history[aircraft->history_count - 2];
    const PositionUpdate *current =
            &aircraft->history[aircraft->history_count - 1];

    *prediction = *current;
    prediction->lat      = current->lat      + (current->lat      - previous->lat);
    prediction->lon      = current->lon      + (current->lon      - previous->lon);
    prediction->altitude = current->altitude + (current->altitude - previous->altitude);
    prediction->time_step = current->time_step;

    return 1;
}

static int predicted_position_outside_area(const SimParams *params,
                                           const PositionUpdate *prediction)
{
    return prediction->lat < params->lat_min ||
           prediction->lat > params->lat_max ||
           prediction->lon < params->lon_min ||
           prediction->lon > params->lon_max;
}

static void process_position_update(const SimParams *params,
                                    AircraftState *aircraft,
                                    const PositionUpdate *update)
{
    PositionUpdate prediction;

    store_position_update(aircraft, update);

    printf("[US101] %s position stored: lat=%.4f lon=%.4f alt=%.0f history=%d\n",
           update->flight_id, update->lat, update->lon,
           update->altitude, aircraft->history_count);

    if (predict_next_position(aircraft, &prediction)) {
        printf("[US101] %s predicted next position: lat=%.4f lon=%.4f alt=%.0f\n",
               update->flight_id, prediction.lat, prediction.lon, prediction.altitude);

        if (predicted_position_outside_area(params, &prediction)) {
            printf("[US101] %s predicted position is outside the simulated area.\n",
                   update->flight_id);
        }
    }
}

double calculate_haversine_distance(double lat1, double lon1, double lat2, double lon2)
{
    const double R      = 6371.0;
    double phi1         = lat1 * M_PI / 180.0;
    double phi2         = lat2 * M_PI / 180.0;
    double delta_phi    = (lat2 - lat1) * M_PI / 180.0;
    double delta_lambda = (lon2 - lon1) * M_PI / 180.0;

    double a = sin(delta_phi / 2) * sin(delta_phi / 2) +
               cos(phi1) * cos(phi2) *
               sin(delta_lambda / 2) * sin(delta_lambda / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));
    return R * c;
}

void log_violation(const ViolationRecord *violation)
{
    int fd = open("violations.log", O_WRONLY | O_CREAT | O_APPEND, 0644);
    if (fd == -1) return;

    char buffer[512];
    int len = snprintf(buffer, sizeof(buffer),
                       "time_step=%d flight1=%s flight2=%s distance=%.2fkm "
                       "v1=(%.1f,%.1f,%.1f) v2=(%.1f,%.1f,%.1f)\n",
                       violation->time_step, violation->flight1_id,
                       violation->flight2_id, violation->distance_km,
                       violation->flight1_position.vx,
                       violation->flight1_position.vy,
                       violation->flight1_position.vz,
                       violation->flight2_position.vx,
                       violation->flight2_position.vy,
                       violation->flight2_position.vz);

    if (len > 0) write(fd, buffer, len);
    close(fd);
}

void check_safety_violations(SimParams *params, AircraftState fleet[], int n, int current_time_step)
{
    for (int i = 0; i < n; i++) {
        if (!fleet[i].active) continue;

        for (int j = i + 1; j < n; j++) {
            if (!fleet[j].active) continue;

            double distance = calculate_haversine_distance(
                    fleet[i].current.lat, fleet[i].current.lon,
                    fleet[j].current.lat, fleet[j].current.lon);

            if (distance < params->safety_threshold) {
                handle_violation(&fleet[i], &fleet[j], distance,
                                 current_time_step, params->safety_threshold, i, j);
            }
        }
    }
}

void handle_violation(AircraftState *aircraft1, AircraftState *aircraft2,
                      double distance, int time_step, int safety_threshold,
                      int flight1_index, int flight2_index)
{
    (void)safety_threshold;

    kill(aircraft1->pid, SIGUSR1);
    kill(aircraft2->pid, SIGUSR1);

    ViolationRecord violation;
    violation.time_step = time_step;
    strncpy(violation.flight1_id, aircraft1->flight_id, MAX_FLIGHT_ID - 1);
    violation.flight1_id[MAX_FLIGHT_ID - 1] = '\0';
    strncpy(violation.flight2_id, aircraft2->flight_id, MAX_FLIGHT_ID - 1);
    violation.flight2_id[MAX_FLIGHT_ID - 1] = '\0';
    violation.distance_km = distance;
    violation.flight1_position = aircraft1->current;
    violation.flight2_position = aircraft2->current;
    violation.resolved = 0;

    log_violation(&violation);

    /* US107: Notify report thread via condition variable */
    notify_violation(&g_notifier, aircraft1, aircraft2, distance, time_step,
                     flight1_index, flight2_index);

    aircraft1->violation_count++;
    aircraft2->violation_count++;

    if (aircraft1->violation_count >= 3) {
        kill(aircraft1->pid, SIGTERM);
        aircraft1->active = 0;
    }

    if (aircraft2->violation_count >= 3) {
        kill(aircraft2->pid, SIGTERM);
        aircraft2->active = 0;
    }
}


/* ------------------------------------------------------------------ */
/* US107 -- ViolationNotifier API                                      */
/* ------------------------------------------------------------------ */

void notifier_init(ViolationNotifier *notifier)
{
    memset(notifier, 0, sizeof(*notifier));
    if (pthread_mutex_init(&notifier->mtx, NULL) != 0) {
        perror("[US107] pthread_mutex_init");
        exit(EXIT_FAILURE);
    }
    if (pthread_cond_init(&notifier->cond, NULL) != 0) {
        perror("[US107] pthread_cond_init");
        exit(EXIT_FAILURE);
    }
    notifier->sim_running = 1;
}

void notifier_destroy(ViolationNotifier *notifier)
{
    pthread_mutex_destroy(&notifier->mtx);
    pthread_cond_destroy(&notifier->cond);
    memset(notifier, 0, sizeof(*notifier));
}

void notifier_enqueue(ViolationNotifier *notifier, const ViolationEvent *event)
{
    if (notifier->queue_count >= VIOLATION_QUEUE_SIZE) {
        fprintf(stderr, "[US107][WARN] Violation queue full, dropping event.\n");
        return;
    }
    notifier->queue[notifier->queue_tail] = *event;
    notifier->queue_tail = (notifier->queue_tail + 1) % VIOLATION_QUEUE_SIZE;
    notifier->queue_count++;
}

int notifier_dequeue(ViolationNotifier *notifier, ViolationEvent *event)
{
    if (notifier->queue_count <= 0)
        return -1;
    *event = notifier->queue[notifier->queue_head];
    notifier->queue_head = (notifier->queue_head + 1) % VIOLATION_QUEUE_SIZE;
    notifier->queue_count--;
    return 0;
}

void notifier_signal_finished(ViolationNotifier *notifier)
{
    pthread_mutex_lock(&notifier->mtx);
    notifier->sim_running = 0;
    pthread_mutex_unlock(&notifier->mtx);
    pthread_cond_broadcast(&notifier->cond);
}

void notify_violation(ViolationNotifier *notifier, AircraftState *aircraft1,
                      AircraftState *aircraft2, double distance, int time_step,
                      int flight1_index, int flight2_index)
{
    ViolationEvent evt;
    memset(&evt, 0, sizeof(evt));
    evt.time_step = time_step;
    evt.distance_km = distance;
    evt.flight1_position = aircraft1->current;
    evt.flight2_position = aircraft2->current;
    evt.flight1_index = flight1_index;
    evt.flight2_index = flight2_index;
    pthread_mutex_lock(&notifier->mtx);
    notifier_enqueue(notifier, &evt);
    pthread_cond_signal(&notifier->cond);
    pthread_mutex_unlock(&notifier->mtx);
}

static void cleanup_flight_semaphores(AircraftState fleet[], int n)
{
    for (int i = 0; i < n; i++) {
        if (fleet[i].step_go_sem != NULL && fleet[i].step_go_sem != SEM_FAILED) {
            sem_close(fleet[i].step_go_sem);
            sem_unlink(fleet[i].step_go_name);
            fleet[i].step_go_sem = NULL;
        }

        if (fleet[i].step_done_sem != NULL && fleet[i].step_done_sem != SEM_FAILED) {
            sem_close(fleet[i].step_done_sem);
            sem_unlink(fleet[i].step_done_name);
            fleet[i].step_done_sem = NULL;
        }
    }
}

static int create_step_semaphores(AircraftState *aircraft, int index)
{
    snprintf(aircraft->step_go_name, sizeof(aircraft->step_go_name),
             "/aisafe_%d_%d_go", getpid(), index);

    snprintf(aircraft->step_done_name, sizeof(aircraft->step_done_name),
             "/aisafe_%d_%d_done", getpid(), index);

    sem_unlink(aircraft->step_go_name);
    sem_unlink(aircraft->step_done_name);

    aircraft->step_go_sem = sem_open(aircraft->step_go_name, O_CREAT | O_EXCL, 0600, 0);
    if (aircraft->step_go_sem == SEM_FAILED) {
        perror("sem_open step_go");
        return -1;
    }

    aircraft->step_done_sem = sem_open(aircraft->step_done_name, O_CREAT | O_EXCL, 0600, 0);
    if (aircraft->step_done_sem == SEM_FAILED) {
        perror("sem_open step_done");
        sem_close(aircraft->step_go_sem);
        sem_unlink(aircraft->step_go_name);
        aircraft->step_go_sem = NULL;
        return -1;
    }

    return 0;
}

static int launch_flights(SimParams *params, AircraftState fleet[], SimSharedData *shm)
{
    for (int i = 0; i < params->num_flights; i++) {
        int pipe_child_to_parent[2];

        if (pipe(pipe_child_to_parent) < 0) {
            perror("pipe child to parent");
            return -1;
        }

        if (create_step_semaphores(&fleet[i], i) != 0) {
            close(pipe_child_to_parent[0]);
            close(pipe_child_to_parent[1]);
            return -1;
        }

        strncpy(shm->flights[i].flight_id, params->flight_ids[i], MAX_FLIGHT_ID - 1);
        shm->flights[i].flight_id[MAX_FLIGHT_ID - 1] = '\0';
        shm->flights[i].active       = 1;
        shm->flights[i].flight_index = i;
        shm->flights[i].violation_count = 0;

        pid_t pid = fork();

        if (pid < 0) {
            perror("fork");
            close(pipe_child_to_parent[0]);
            close(pipe_child_to_parent[1]);
            return -1;
        }

        if (pid == 0) {
            close(pipe_child_to_parent[0]);

            flight_process_run(
                    params->flight_ids[i],
                    pipe_child_to_parent[1],
                    fleet[i].step_go_sem,
                    fleet[i].step_done_sem,
                    params);

            exit(EXIT_SUCCESS);
        }

        close(pipe_child_to_parent[1]);

        strncpy(fleet[i].flight_id, params->flight_ids[i], MAX_FLIGHT_ID - 1);
        fleet[i].flight_id[MAX_FLIGHT_ID - 1] = '\0';
        fleet[i].pid          = pid;
        fleet[i].pipe_read_fd = pipe_child_to_parent[0];
        fleet[i].pipe_write_fd = -1;
        fleet[i].active        = 1;
        fleet[i].history_count = 0;
        fleet[i].violation_count = 0;

        printf("[US105] Voo %s iniciado -- PID %d\n", fleet[i].flight_id, pid);
    }

    return 0;
}

static int wait_for_step_done(AircraftState *aircraft, int step)
{
    struct timespec deadline;

    if (clock_gettime(CLOCK_REALTIME, &deadline) == -1) {
        perror("clock_gettime");
        return -1;
    }

    deadline.tv_sec += 10;

    while (sem_timedwait(aircraft->step_done_sem, &deadline) == -1) {
        if (errno == EINTR)
            continue;

        if (errno == ETIMEDOUT) {
            fprintf(stderr,
                    "[US108][ERROR] Timeout waiting for flight %s at step %d.\n",
                    aircraft->flight_id, step);
            return -1;
        }

        perror("sem_timedwait");
        return -1;
    }

    return 0;
}

void run_simulation(SimParams *params, AircraftState fleet[], int n)
{
    int active_count = n;

    FILE *vlog = fopen("violations.log", "w");
    if (vlog != NULL) fclose(vlog);

    for (int step = params->time_start;
         step < params->time_end && !g_terminate && active_count > 0;
         step += params->time_step_secs) {

        active_count = 0;
        for (int i = 0; i < n; i++) {
            if (fleet[i].active) active_count++;
        }

        if (active_count == 0) break;

        printf("[US108] === Time step %d ===\n", step);

        for (int i = 0; i < n; i++) {
            if (fleet[i].active) {
                printf("[US108] Parent released flight %s for step %d\n",
                       fleet[i].flight_id, step);
                sem_post(fleet[i].step_go_sem);
            }
        }

        for (int i = 0; i < n; i++) {
            if (!fleet[i].active)
                continue;

            if (wait_for_step_done(&fleet[i], step) != 0) {
                kill(fleet[i].pid, SIGTERM);
                fleet[i].active = 0;
                active_count--;
                continue;
            }

            PositionUpdate update;
            ssize_t read_result = read(fleet[i].pipe_read_fd, &update, sizeof(update));

            if (read_result <= 0) {
                fleet[i].active = 0;
                active_count--;
                continue;
            }

            process_position_update(params, &fleet[i], &update);

            printf("[US108] %s completed step %d -> lat=%.4f lon=%.4f alt=%.0f\n",
                   update.flight_id, step, update.lat, update.lon, update.altitude);

            if (!update.active) {
                fleet[i].active = 0;
                active_count--;
            }
        }

        printf("[US108] All active flights completed step %d\n", step);

        /* US106: Wakes up detection thread and waits */
        pthread_mutex_lock(&g_detect_mtx);
        g_detect_current_step = step;
        g_detect_scan_requested = 1;
        pthread_cond_signal(&g_detect_cond_start);

        while (g_detect_scan_requested == 1) {
            pthread_cond_wait(&g_detect_cond_done, &g_detect_mtx);
        }

        pthread_mutex_unlock(&g_detect_mtx);

        sleep(1);

        if (g_child_exited) {
            int status;
            pid_t p;

            while ((p = waitpid(-1, &status, WNOHANG)) > 0) {
                for (int i = 0; i < n; i++) {
                    if (fleet[i].pid == p) {
                        printf("[US108] Voo %s terminou execucao -- PID %d\n",
                               fleet[i].flight_id, p);
                        if (fleet[i].active) {
                            fleet[i].active = 0;
                            active_count--;
                        }
                    }
                }
            }

            g_child_exited = 0;
        }
    }

    if (g_terminate) {
        for (int i = 0; i < n; i++) {
            if (fleet[i].active)
                kill(fleet[i].pid, SIGTERM);
        }
    }

    while (waitpid(-1, NULL, 0) != -1 || errno == EINTR);

    cleanup_flight_semaphores(fleet, n);
}

/* ------------------------------------------------------------------ */
/* US105 -- shared memory                                              */
/* ------------------------------------------------------------------ */

static SimSharedData *create_shared_memory(int num_flights)
{
    shm_unlink(SHM_NAME);

    int fd = shm_open(SHM_NAME, O_CREAT | O_EXCL | O_RDWR, S_IRUSR | S_IWUSR);
    if (fd < 0) {
        perror("[US105][ERROR] shm_open");
        return NULL;
    }

    if (ftruncate(fd, sizeof(SimSharedData)) < 0) {
        perror("[US105][ERROR] ftruncate");
        close(fd);
        shm_unlink(SHM_NAME);
        return NULL;
    }

    SimSharedData *shm = mmap(NULL, sizeof(SimSharedData),
                              PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    close(fd);

    if (shm == MAP_FAILED) {
        perror("[US105][ERROR] mmap");
        shm_unlink(SHM_NAME);
        return NULL;
    }

    memset(shm, 0, sizeof(SimSharedData));
    shm->num_flights       = num_flights;
    shm->current_step      = 0;
    shm->simulation_done   = 0;
    shm->violation_pending = 0;
    shm->total_violations  = 0;

    return shm;
}

static void destroy_shared_memory(SimSharedData *shm)
{
    munmap(shm, sizeof(SimSharedData));
    shm_unlink(SHM_NAME);
}

/* ------------------------------------------------------------------ */
/* US106 -- Safety Violation Detection Thread                         */
/* ------------------------------------------------------------------ */

static void *safety_violation_detector(void *arg)
{
    (void)arg;
    printf("[US106] Safety violation detection thread started\n");

    while (1) {
        pthread_mutex_lock(&g_detect_mtx);

        /* Stays asleep until the main asks for a scan or the simulation ends */
        while (g_detect_scan_requested == 0 && g_detect_sim_finished == 0) {
            pthread_cond_wait(&g_detect_cond_start, &g_detect_mtx);
        }

        /* If simulation is over, leaves the loop and ends the thread */
        if (g_detect_sim_finished == 1 && g_detect_scan_requested == 0) {
            pthread_mutex_unlock(&g_detect_mtx);
            break;
        }

        /* US108: scan only after all active flights completed this step. */
        check_safety_violations(g_detect_params, g_detect_fleet, g_detect_num_flights, g_detect_current_step);

        /* Warns main the scan is over */
        g_detect_scan_requested = 0;
        pthread_cond_signal(&g_detect_cond_done);

        pthread_mutex_unlock(&g_detect_mtx);
    }

    printf("[US106] Safety violation detection thread exiting\n");
    return NULL;
}



/* ------------------------------------------------------------------ */
/* US105 -- main                                                       */
/* ------------------------------------------------------------------ */

int main(int argc, char *argv[])
{
    if (argc != 2) {
        fprintf(stderr, "Uso: %s <ficheiro_config>\n", argv[0]);
        fprintf(stderr, "Exemplo: %s config/opo_mad.cfg\n", argv[0]);
        return EXIT_FAILURE;
    }

    SimParams params;
    memset(&params, 0, sizeof(params));

    if (load_config(argv[1], &params) != 0)
        return EXIT_FAILURE;

    if (validate_params(&params) != 0)
        return EXIT_FAILURE;

    setup_signals();

    SimSharedData *shm = create_shared_memory(params.num_flights);
    if (shm == NULL)
        return EXIT_FAILURE;

    pthread_mutex_t mutex;
    pthread_cond_t  cond_violation;
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&cond_violation, NULL);

    /* US107: Initialize notifier */
    notifier_init(&g_notifier);

    AircraftState fleet[MAX_FLIGHTS];
    memset(fleet, 0, sizeof(fleet));

    /* US106: Pointers*/
    g_detect_params = &params;
    g_detect_fleet = fleet;
    g_detect_num_flights = params.num_flights;

    /* US106: Detection thread creation */
    pthread_t tid_detection;
    pthread_create(&tid_detection, NULL, safety_violation_detector, NULL);

    /* US111: Prepare report thread args */
    memset(&g_report_args, 0, sizeof(g_report_args));
    g_report_args.notifier = &g_notifier;
    g_report_args.num_flights = params.num_flights;
    g_report_args.params = params;
    g_report_args.fleet = fleet;

    /* US111: Create report thread */
    if (pthread_create(&g_report_tid, NULL, report_thread_main, &g_report_args) != 0) {
        perror("[US111] pthread_create report_thread");
        pthread_mutex_lock(&g_detect_mtx);
        g_detect_sim_finished = 1;
        pthread_cond_broadcast(&g_detect_cond_start);
        pthread_mutex_unlock(&g_detect_mtx);

        notifier_signal_finished(&g_notifier);
        notifier_destroy(&g_notifier);
        pthread_join(tid_detection, NULL);
        destroy_shared_memory(shm);
        return EXIT_FAILURE;
    }

    if (launch_flights(&params, fleet, shm) != 0) {
        shm->simulation_done = 1;
        pthread_mutex_lock(&g_detect_mtx);
        g_detect_sim_finished = 1;
        pthread_cond_broadcast(&g_detect_cond_start);
        pthread_mutex_unlock(&g_detect_mtx);

        notifier_signal_finished(&g_notifier);
        pthread_cond_broadcast(&cond_violation);
        pthread_join(tid_detection, NULL);
        pthread_join(g_report_tid, NULL);
        pthread_cond_destroy(&cond_violation);
        pthread_mutex_destroy(&mutex);
        notifier_destroy(&g_notifier);
        cleanup_flight_semaphores(fleet, params.num_flights);
        destroy_shared_memory(shm);
        return EXIT_FAILURE;
    }

    run_simulation(&params, fleet, params.num_flights);

    /* US106: Detection thread shutdown */
    pthread_mutex_lock(&g_detect_mtx);
    g_detect_sim_finished = 1;
    pthread_cond_broadcast(&g_detect_cond_start);
    pthread_mutex_unlock(&g_detect_mtx);

    pthread_mutex_lock(&mutex);
    shm->simulation_done = 1;
    pthread_cond_broadcast(&cond_violation);
    pthread_mutex_unlock(&mutex);

    pthread_join(tid_detection, NULL);

    /* US107: Signal report thread that simulation is finished */
    notifier_signal_finished(&g_notifier);

    /* US111: Wait for report thread to finish */
    pthread_join(g_report_tid, NULL);

    pthread_cond_destroy(&cond_violation);
    pthread_mutex_destroy(&mutex);

    /* US107: Destroy notifier */
    notifier_destroy(&g_notifier);

    ReportData report_data;
    memset(&report_data, 0, sizeof(report_data));
    strncpy(report_data.area_code, params.area_code, 15);
    report_data.num_flights    = params.num_flights;
    report_data.time_start     = params.time_start;
    report_data.time_end       = params.time_end;
    report_data.time_step_secs = params.time_step_secs;
    report_data.safety_threshold = params.safety_threshold;
    memcpy(report_data.fleet, fleet, params.num_flights * sizeof(AircraftState));

    pid_t report_pid = fork();
    if (report_pid == 0) {
        run_report_process(&report_data);
        exit(EXIT_SUCCESS);
    }

    if (report_pid > 0)
        waitpid(report_pid, NULL, 0);

    destroy_shared_memory(shm);

    printf("[US105] Simulacao concluida.\n");
    return EXIT_SUCCESS;
}
