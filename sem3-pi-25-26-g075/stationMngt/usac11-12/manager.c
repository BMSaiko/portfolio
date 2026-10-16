
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "manager.h"
#include "asm.h"

// Global variables
User* users = NULL;
Track* tracks = NULL;
Train* trains = NULL;
Log* logs = NULL;
int user_count = 0, track_count = 0, train_count = 0, log_count = 0;
SensorConfig temp_config = {10, 5};
SensorConfig hum_config = {10, 5};

char* trim(char* str) {
    char* end;
    while(isspace((unsigned char)*str)) str++;
    if(*str == 0) return str;
    end = str + strlen(str) - 1;
    while(end > str && isspace((unsigned char)*end)) end--;
    end[1] = '\0';
    return str;
}

TrackState string_to_trackstate(const char* str) {
    if (strcasecmp(str, "FREE") == 0) return TRACK_FREE;
    if (strcasecmp(str, "BUSY") == 0) return TRACK_BUSY;
    if (strcasecmp(str, "ASSIGNED") == 0) return TRACK_ASSIGNED;
    if (strcasecmp(str, "INOPERATIVE") == 0) return TRACK_INOPERATIVE;
    return TRACK_FREE;
}

int load_config(const char* filename) {
    FILE* file = fopen(filename, "r");
    if (!file) {
        printf("Error: Unable to open file %s\n", filename);
        return 0;
    }
    
    char line[256];
    char current_section[50] = "";
    
    // Static temporary arrays (no malloc to avoid leaks)
    User temp_users[10];
    Track temp_tracks[100];
    Train temp_trains[50];
    Log temp_logs[100];
    int temp_user_count = 0, temp_track_count = 0, temp_train_count = 0, temp_log_count = 0;
    
    memset(temp_users, 0, sizeof(temp_users));
    memset(temp_tracks, 0, sizeof(temp_tracks));
    memset(temp_trains, 0, sizeof(temp_trains));
    memset(temp_logs, 0, sizeof(temp_logs));
    
    // Variables for current user
    User current_user = {0};
    int user_fields_set = 0;
    
    while (fgets(line, sizeof(line), file)) {
        line[strcspn(line, "\n")] = '\0';
        char* trimmed = trim(line);
        
        if (trimmed[0] == '\0' || trimmed[0] == '#') continue;

        if (trimmed[0] == '[' && strchr(trimmed, ']')) {

            if (strcmp(current_section, "USERS") == 0 && user_fields_set == 4) {
                temp_users[temp_user_count++] = current_user;
                memset(&current_user, 0, sizeof(User));
                user_fields_set = 0;
            }
            
            sscanf(trimmed, "[%49[^]]", current_section);
            continue;
        }

        if (strcmp(current_section, "USERS") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* key = trim(trimmed);
            char* value = trim(equal + 1);
            
            if (strcmp(key, "name") == 0) {
                strcpy(current_user.name, value);
                user_fields_set |= 1;
            } else if (strcmp(key, "username") == 0) {
                strcpy(current_user.username, value);
                user_fields_set |= 2;
            } else if (strcmp(key, "password") == 0) {
                strcpy(current_user.password, value);
                user_fields_set |= 4;
            } else if (strcmp(key, "caesar_key") == 0) {
                current_user.caesar_key = atoi(value);
                user_fields_set |= 8;
                
                // Encrypt password
                char encrypted[50];
                if (encrypt_data(current_user.password, current_user.caesar_key, encrypted)) {
                    strcpy(current_user.encrypted_password, encrypted);
                } else {
                    printf("Warning: Failed to encrypt password for %s\n", current_user.username);
                    strcpy(current_user.encrypted_password, current_user.password);
                }
                
                // Add user
                temp_users[temp_user_count++] = current_user;
                memset(&current_user, 0, sizeof(User));
                user_fields_set = 0;
            }
        }
        else if (strcmp(current_section, "SENSOR_TEMP") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* key = trim(trimmed);
            char* value = trim(equal + 1);
            
            if (strcmp(key, "buffer_size") == 0) temp_config.buffer_size = atoi(value);
            else if (strcmp(key, "window_size") == 0) temp_config.window_size = atoi(value);
        }
        else if (strcmp(current_section, "SENSOR_HUM") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* key = trim(trimmed);
            char* value = trim(equal + 1);
            
            if (strcmp(key, "buffer_size") == 0) hum_config.buffer_size = atoi(value);
            else if (strcmp(key, "window_size") == 0) hum_config.window_size = atoi(value);
        }
        else if (strcmp(current_section, "TRACKS") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* track_id_str = trim(trimmed);
            char* track_info = trim(equal + 1);
            
            int track_id = atoi(track_id_str);
            if (track_id <= 0) continue;
            
            Track track = {0};
            track.id = track_id;
            track.train_id = -1;

            char* colon = strchr(track_info, ':');
            if (colon) {
                *colon = '\0';
                char* state_str = trim(track_info);
                char* train_id_str = trim(colon + 1);
                
                track.state = string_to_trackstate(state_str);
                track.train_id = atoi(train_id_str);
            } else {
                track.state = string_to_trackstate(track_info);
            }
            
            temp_tracks[temp_track_count++] = track;
        }
        else if (strcmp(current_section, "TRAINS") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* train_id_str = trim(trimmed);
            char* train_name = trim(equal + 1);
            
            int train_id = atoi(train_id_str);
            if (train_id <= 0) continue;
            
            Train train = {0};
            train.id = train_id;
            strncpy(train.name, train_name, sizeof(train.name) - 1);
            train.current_station = -1;
            
            temp_trains[temp_train_count++] = train;
        }
        else if (strcmp(current_section, "LOGS") == 0) {
            char* equal = strchr(trimmed, '=');
            if (!equal) continue;
            *equal = '\0';
            char* log_id_str = trim(trimmed);
            char* log_info = trim(equal + 1);
            
            int log_id = atoi(log_id_str);
            if (log_id <= 0) continue;

            char* colon1 = strchr(log_info, ':');
            if (!colon1) continue;
            *colon1 = '\0';
            char* username = trim(log_info);
            char* rest = trim(colon1 + 1);
            
            char* colon2 = strchr(rest, ':');
            if (!colon2) continue;
            *colon2 = '\0';
            char* action = trim(rest);
            char* timestamp_str = trim(colon2 + 1);
            
            Log log = {0};
            log.id = log_id;
            strncpy(log.username, username, sizeof(log.username) - 1);
            strncpy(log.action, action, sizeof(log.action) - 1);
            log.timestamp = atol(timestamp_str);
            
            temp_logs[temp_log_count++] = log;
        }
    }
    
    fclose(file);

    if (strcmp(current_section, "USERS") == 0 && user_fields_set == 4) {
        temp_users[temp_user_count++] = current_user;
    }
    

    free_manager_data();
    
    // Allocate new arrays
    user_count = temp_user_count;
    track_count = temp_track_count;
    train_count = temp_train_count;
    log_count = temp_log_count;
    
    if (user_count > 0) {
        users = malloc(sizeof(User) * user_count);
        memcpy(users, temp_users, sizeof(User) * user_count);
    }
    
    if (track_count > 0) {
        tracks = malloc(sizeof(Track) * track_count);
        memcpy(tracks, temp_tracks, sizeof(Track) * track_count);
    }
    
    if (train_count > 0) {
        trains = malloc(sizeof(Train) * train_count);
        memcpy(trains, temp_trains, sizeof(Train) * train_count);
    }
    
    if (log_count > 0) {
        logs = malloc(sizeof(Log) * log_count);
        memcpy(logs, temp_logs, sizeof(Log) * log_count);
    }
    
    printf("Configuration loaded successfully!\n");
    printf("- Users: %d\n", user_count);
    printf("- Tracks: %d\n", track_count);
    printf("- Trains: %d\n", train_count);
    printf("- Logs: %d\n", log_count);
    
    return 1;
}

void free_manager_data(void) {
    if (users) { free(users); users = NULL; }
    if (tracks) { free(tracks); tracks = NULL; }
    if (trains) { free(trains); trains = NULL; }
    if (logs) { free(logs); logs = NULL; }
    user_count = track_count = train_count = log_count = 0;
}

int export_user_actions(const char* username, const char* filename) {
    if (!username || !filename) {
        printf("Error: Invalid user or filename\n");
        return 0;
    }
    
    if (log_count == 0) {
        printf("Error: No logs available\n");
        return 0;
    }

    int user_log_count = 0;
    for (int i = 0; i < log_count; i++) {
        if (strcmp(logs[i].username, username) == 0) {
            user_log_count++;
        }
    }
    
    if (user_log_count == 0) {
        printf("Error: No actions found for user '%s'\n", username);
        return 0;
    }

    FILE* file = fopen(filename, "w");
    if (!file) {
        printf("Error: Unable to create file %s\n", filename);
        return 0;
    }
    
    // File header
    fprintf(file, "=============================================\n");
    fprintf(file, "USER ACTION REPORT: %s\n", username);
    fprintf(file, "Generation date: %s", ctime(&(time_t){time(NULL)}));
    fprintf(file, "Total actions: %d\n", user_log_count);
    fprintf(file, "=============================================\n\n");
    
    // Write each action
    int written_count = 0;
    for (int i = 0; i < log_count; i++) {
        if (strcmp(logs[i].username, username) == 0) {
            time_t timestamp = (time_t)logs[i].timestamp;
            struct tm* timeinfo = localtime(&timestamp);
            char time_str[50];
            strftime(time_str, sizeof(time_str), "%d/%m/%Y %H:%M:%S", timeinfo);

            fprintf(file, "ACTION #%d\n", written_count + 1);
            fprintf(file, "Log ID: %d\n", logs[i].id);
            fprintf(file, "Date/Time: %s\n", time_str);
            fprintf(file, "Timestamp: %lu\n", logs[i].timestamp);
            fprintf(file, "Action: %s\n", logs[i].action);
            fprintf(file, "Status: RECORDED\n");
            fprintf(file, "---------------------------------------------\n");
            
            written_count++;
        }
    }
    
    fclose(file);
    
    printf("Report exported successfully!\n");
    printf("File: %s\n", filename);
    printf("Actions recorded: %d\n", written_count);
    
    return 1;
}

void print_manager_status(void) {
    printf("\n=== MANAGER STATUS ===\n");
    
    printf("\nUsers (%d):\n", user_count);
    for (int i = 0; i < user_count; i++) {
        printf("  %s (%s) - Key: %d\n", 
               users[i].name, users[i].username, users[i].caesar_key);
        printf("    Password: %s -> %s\n", 
               users[i].password, users[i].encrypted_password);
    }
    
    printf("\nTracks (%d):\n", track_count);
    const char* state_names[] = {"FREE", "BUSY", "ASSIGNED", "INOPERATIVE"};
    for (int i = 0; i < track_count; i++) {
        printf("  Track %d: %s", tracks[i].id, state_names[tracks[i].state]);
        if (tracks[i].train_id != -1) {
            printf(" (Train %d)", tracks[i].train_id);
        }
        printf("\n");
    }
    
    printf("\nTrains (%d):\n", train_count);
    for (int i = 0; i < train_count; i++) {
        printf("  Train %d: %s\n", trains[i].id, trains[i].name);
    }
    
    printf("\nLogs (%d, showing first 5):\n", log_count);
    int max_logs = (log_count < 5) ? log_count : 5;
    for (int i = 0; i < max_logs; i++) {
        printf("  Log %d: %s - %s (timestamp: %lu)\n",
               logs[i].id, logs[i].username, logs[i].action, logs[i].timestamp);
    }
    
    printf("\nSensor Configuration:\n");
    printf("  Temperature: buffer=%d, window=%d\n", 
           temp_config.buffer_size, temp_config.window_size);
    printf("  Humidity: buffer=%d, window=%d\n", 
           hum_config.buffer_size, hum_config.window_size);
}
