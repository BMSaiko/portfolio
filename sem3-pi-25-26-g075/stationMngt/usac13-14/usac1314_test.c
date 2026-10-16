

#include <stdio.h>
#include "manager.h"
#include <unistd.h>

int main() {

    //  USAC13 initialization
    if (!init_sensor_filters()) {
        free_manager_data();
        return 1;
    }
    if (!init_serial_port()) {
        free_manager_data();
        return 1;
    }
    printf("\n");

    //USAC14 
    printf("--- USAC14: Demonstrating track light control ---\n");
    
    
    printf("Setting Track 1 to ASSIGNED (Yellow)...\n");
    control_track_light(1, TRACK_ASSIGNED);
    sleep(2); 

    printf("Setting Track 2 to BUSY (Red)...\n");
    control_track_light(2, TRACK_BUSY);
    sleep(2);

    printf("Setting Track 1 to FREE (Green)...\n");
    control_track_light(1, TRACK_FREE);
    sleep(2);

    printf("Setting Track 3 to INOPERATIVE (Red Blinking)...\n");
    control_track_light(3, TRACK_INOPERATIVE);
    printf("\n");

    printf("--- USAC13: Now starting to listen for sensor data... ---\n");
    printf("(Press Ctrl+C to stop)\n\n");

    if (!init_serial_port()) {
        free_manager_data();
        return 1;
    }

    printf("\n--- Waiting for sensor data... (Press Ctrl+C to stop) ---\n\n");
    char sensor_string[256];

    while (1) {
        if (read_line_from_sensor(sensor_string, sizeof(sensor_string))) {
            printf(">> Received: %s\n", sensor_string);
            get_and_process_sensor_data(sensor_string);
            printf("   Current Temp: Filtered=%.2f\n", temp_sensor_data.last_filtered_value);
            printf("   Current Hum:  Filtered=%.2f\n\n", hum_sensor_data.last_filtered_value);
        }

    }

    close_serial_port();
    free_manager_data();
    return 0;
    
    free_manager_data();
    return 0;
}