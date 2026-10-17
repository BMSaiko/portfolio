#ifndef REPORT_H
#define REPORT_H

#include "simulation.h"

#define MAX_REPORT_VIOLATIONS 512

typedef struct {
    char   area_code[16];
    int    num_flights;
    int    time_start;
    int    time_end;
    int    time_step_secs;
    int    safety_threshold;
    AircraftState fleet[MAX_FLIGHTS];
} ReportData;

void run_report_process(const ReportData *data);

#endif