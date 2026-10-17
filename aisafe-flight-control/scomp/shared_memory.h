#ifndef SHARED_MEMORY_H
#define SHARED_MEMORY_H

#include "simulation.h"
#include <semaphore.h>
#include <pthread.h>

#define SHM_NAME        "/aisafe_sim"
#define SEM_STEP_GO     "/aisafe_step_go"
#define SEM_STEP_DONE   "/aisafe_step_done"
#define SEM_SHM_MUTEX   "/aisafe_shm_mutex"

typedef struct {
    char   flight_id[MAX_FLIGHT_ID];
    double lat;
    double lon;
    double altitude;
    double vx;
    double vy;
    double vz;
    int    time_step;
    int    active;
    int    violation_count;
    int    flight_index;
} FlightSlot;

typedef struct {
    FlightSlot      flights[MAX_FLIGHTS];
    int             num_flights;
    int             current_step;
    int             simulation_done;
    int             violation_pending;
    int             total_violations;
} SimSharedData;

#endif