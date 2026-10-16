#ifndef STRUCTS_H
#define STRUCTS_H

// Struct for min/max levels
typedef struct {
    int min;
    int max;
} Levels;

// Struct for circular buffer management
typedef struct {
    int *data;    // pointer (8 bytes)
    int tail;     // int (4 bytes)
    int head;     // int (4 bytes)
    int size;     // int (4 bytes)
} CircularBuffer;

// Struct for temperature settings
typedef struct {
    Levels range;  // struct (8 bytes)
    int current;   // int (4 bytes)
    CircularBuffer buffer;  // Buffer específico para temperatura
} Temperature;

// Struct for humidity settings
typedef struct {
    Levels range;  // struct (8 bytes)
    int current;   // int (4 bytes)
    CircularBuffer buffer;  // Buffer específico para humidade
} Humidity;

typedef struct {
    int operation_id;          // int (4 bytes)
    int machine_id;           // int (4 bytes)
    char operation_name[50];  // char array
    char start_time[20];      // char array
    int duration;             // int (4 bytes) - duração em segundos
} Operation;

typedef struct {
    Operation *operations;              
    Temperature temperature;           
    Humidity humidity;                 
    char name[50];                     
    int identifier;                    
    int moving_median_window_length;   
    int op_count;                      
    char state[4];                     
} Machine;

typedef struct {
    Machine *machines;    // pointer (8 bytes)
    int machine_count;    // int (4 bytes)
} PlantFloorManager;

#endif //STRUCTS_H
