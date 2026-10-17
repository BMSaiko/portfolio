#ifndef SIMULATION_H
#define SIMULATION_H

#include <sys/types.h>
#include <signal.h>
#include <semaphore.h>
#include <pthread.h>

#define MAX_FLIGHTS 32
#define MAX_FLIGHT_ID 16
#define MAX_HISTORY_STEPS 256
#define MAX_EXECUTION_STATUS 64
#define MAX_VIOLATION_RECORDS 1024
#define SAFETY_THRESHOLD_KM 10.0

#define VIOLATION_QUEUE_SIZE 2048

typedef struct {
    char area_code[16];
    double lat_min;
    double lat_max;
    double lon_min;
    double lon_max;
    double max_altitude;
    int time_start;
    int time_end;
    int time_step_secs;
    int safety_threshold;
    int num_flights;
    char flight_ids[MAX_FLIGHTS][MAX_FLIGHT_ID];
} SimParams;

typedef struct {
    char flight_id[MAX_FLIGHT_ID];
    double lat;
    double lon;
    double altitude;
    double vx;
    double vy;
    double vz;
    int time_step;
    int active;
} PositionUpdate;

typedef struct {
    int time_step;
    char flight1_id[MAX_FLIGHT_ID];
    char flight2_id[MAX_FLIGHT_ID];
    double distance_km;
    PositionUpdate flight1_position;
    PositionUpdate flight2_position;
    int resolved;
} ViolationRecord;

/* US107: Violation event for condition variable communication */
typedef struct {
    int time_step;
    int flight1_index;
    int flight2_index;
    double distance_km;
    PositionUpdate flight1_position;
    PositionUpdate flight2_position;
} ViolationEvent;

/* US107: Thread-safe bounded queue for violation notification */
typedef struct {
    pthread_mutex_t mtx;
    pthread_cond_t  cond;
    ViolationEvent  queue[VIOLATION_QUEUE_SIZE];
    int             queue_head;
    int             queue_tail;
    int             queue_count;
    volatile int    sim_running;
} ViolationNotifier;

typedef struct {
    char flight_id[MAX_FLIGHT_ID];
    pid_t pid;
    int pipe_read_fd;
    int pipe_write_fd;
    sem_t *step_go_sem;
    sem_t *step_done_sem;
    char step_go_name[64];
    char step_done_name[64];
    PositionUpdate current;
    PositionUpdate history[MAX_HISTORY_STEPS];
    int history_count;
    int active;
    int violation_count;
} AircraftState;

typedef struct {
    const SimParams *params;
    const AircraftState *fleet;
    int flight_count;
    const ViolationRecord *violations;
    int violation_count;
    const char *report_path;
    int interrupted;
} FinalReportContext;

extern volatile sig_atomic_t g_terminate;
extern volatile sig_atomic_t g_child_exited;

int  validate_params(const SimParams *params);
void setup_signals(void);
void run_simulation(SimParams *params, AircraftState fleet[], int n);

int  load_config(const char *filepath, SimParams *params);

double calculate_haversine_distance(double lat1, double lon1, double lat2, double lon2);
void   check_safety_violations(SimParams *params, AircraftState fleet[], int n, int current_time_step);
void   log_violation(const ViolationRecord *violation);
void   handle_violation(AircraftState *aircraft1, AircraftState *aircraft2, double distance, int time_step, int safety_threshold, int flight1_index, int flight2_index);
void  *generate_final_report_thread(void *arg);

/* US107: ViolationNotifier API */
void notifier_init(ViolationNotifier *notifier);
void notifier_destroy(ViolationNotifier *notifier);
void notifier_enqueue(ViolationNotifier *notifier, const ViolationEvent *event);
int  notifier_dequeue(ViolationNotifier *notifier, ViolationEvent *event);
void notifier_signal_finished(ViolationNotifier *notifier);

/* US107: Bridge function from detection to report thread */
void notify_violation(ViolationNotifier *notifier, AircraftState *aircraft1,
                      AircraftState *aircraft2, double distance, int time_step,
                      int flight1_index, int flight2_index);

#endif
