
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

typedef struct {
    int* buffer;     
    int length;      
    int nelem;        
    int head;        
    int tail;         
} MovingMedianFilter;


typedef struct {
    MovingMedianFilter* filter; 
    float last_raw_value;       
    float last_filtered_value;  
} SensorData;


typedef struct {
    int id;
    char type[50];
    float value;
    char unit[30];
    unsigned long time;
} ParsedSensorData;

// Manager global variables
extern User* users;
extern Track* tracks;
extern Train* trains;
extern Log* logs;
extern int user_count, track_count, train_count, log_count, serial_port_fd;
extern SensorConfig temp_config, hum_config;
extern SensorData temp_sensor_data;
extern SensorData hum_sensor_data;

// Manager functions
// usac11
int load_config(const char* filename);
void free_manager_data(void);
void print_manager_status(void);
// usac12
int export_user_actions(const char* username, const char* filename);

// usac13
int read_line_from_sensor(char* buffer, int buffer_size);
int init_sensor_filters(void);
void free_sensor_filters(void);
void get_and_process_sensor_data(const char* sensor_string);
int get_value_by_key(char* str, char* key, char* value_buffer);
void close_serial_port();
int init_serial_port();
// USAC14
void control_track_light(int track_id, TrackState state);
int send_command_to_lightsign(const char* command);

#endif