#ifndef REPORT_THREAD_H
#define REPORT_THREAD_H

#include "simulation.h"

/* US111: Arguments passed to report thread */
typedef struct {
    ViolationNotifier *notifier;
    AircraftState     *fleet;
    int                num_flights;
    SimParams          params;
} ReportThreadArgs;

/* US111: Report thread main function */
void *report_thread_main(void *arg);

#endif
