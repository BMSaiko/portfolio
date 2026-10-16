#define _XOPEN_SOURCE 700

#ifndef SUPERVISOR_H
#define SUPERVISOR_H

// Standard C libraries
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <dirent.h>

// Project headers
#include "asm_functions.h"
#include "structs.h"
#include "machmanager.h"
#include "ui.h"
#include "utils.h"
#include "machine_emulator.h"

// Time utility functions
void get_current_time(char *buffer, size_t size);
void add_seconds_to_time(const char *start_time, char *end_time, int duration_in_seconds);

#endif // SUPERVISOR_H 