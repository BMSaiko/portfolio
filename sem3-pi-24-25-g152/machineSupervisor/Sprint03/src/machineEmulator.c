#include "../include/supervisor.h"
#include <time.h>

char* simulate_machine(const char* cmd) {
    static char response[100];

    srand(time(NULL));
    int temp = 20 + (rand() % 11);  // Random temperature between 20-30
    int hum = 40 + (rand() % 31);   // Random humidity between 40-70

    if (strncmp(cmd, "OFF", 3) == 0) {
        strcpy(response, "Machine is OFF");
    } else if (strncmp(cmd, "ON", 2) == 0 || strncmp(cmd, "OP", 2) == 0) {
        snprintf(response, sizeof(response), 
                "TEMP&unit:celsius&value:%d#HUM&unit:percentage&value:%d",
                temp, hum);
    } else {
        strcpy(response, "Invalid command");
    }

    return response;
}