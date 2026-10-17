#ifndef FLIGHT_PROCESS_H
#define FLIGHT_PROCESS_H

#include <semaphore.h>
#include "simulation.h"

void flight_process_run(const char *flight_id,
                        int write_fd,
                        sem_t *step_go_sem,
                        sem_t *step_done_sem,
                        const SimParams *params);

#endif