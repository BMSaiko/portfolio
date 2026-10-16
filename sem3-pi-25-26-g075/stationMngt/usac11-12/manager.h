
#ifndef MANAGER_H
#define MANAGER_H

#include <time.h>
#include "asm.h"

// Data structures for Manager
typedef struct {
    char name[50];
    char username[50];
    char password[50];        // Original password (for encryption)
    char encrypted_password[50]; // Encrypted password
    int caesar_key;
} User;

typedef struct {
    int buffer_size;
    int window_size;
} SensorConfig;

typedef enum {
    TRACK_FREE,
    TRACK_BUSY,
    TRACK_ASSIGNED,
    TRACK_INOPERATIVE
} TrackState;

typedef struct {
    int id;
    TrackState state;
    int train_id;  // -1 if no train
} Track;

typedef struct {
    int id;
    char name[50];
    int current_station; // Current station
} Train;

typedef struct {
    int id;
    char username[50];
    char action[100];
    unsigned long timestamp;
} Log;

// Manager global variables
extern User* users;
extern Track* tracks;
extern Train* trains;
extern Log* logs;
extern int user_count, track_count, train_count, log_count;
extern SensorConfig temp_config, hum_config;

// Manager functions
// usac11
int load_config(const char* filename);
void free_manager_data(void);
void print_manager_status(void);
// usac12
int export_user_actions(const char* username, const char* filename);

#endif
