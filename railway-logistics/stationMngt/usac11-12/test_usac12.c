
#include <stdio.h>
#include <string.h>
#include "manager.h"

int main(int argc, char* argv[]) {
    char* config_file = "config.txt";
    char* username = "admin";
    char* output_file = "user_actions_report.txt";

    if (argc > 1) config_file = argv[1];
    if (argc > 2) username = argv[2];
    if (argc > 3) output_file = argv[3];
    
    printf("=== Test USAC12 - Export User Actions ===\n");
    printf("Configuration file: %s\n", config_file);
    printf("User: %s\n", username);
    printf("Output file: %s\n\n", output_file);
    
    // Load config
    if (!load_config(config_file)) {
        printf("Failed to load configuration!\n");
        return 1;
    }
    
    // Show status to verify data
    printf("Loaded data:\n");
    printf("- Users: %d\n", user_count);
    printf("- Logs: %d\n\n", log_count);

    if (export_user_actions(username, output_file)) {
        printf("\nReport generated successfully!\n");
        

        printf("\n=== Generated file content ===\n");
        FILE* f = fopen(output_file, "r");
        if (f) {
            char line[256];
            while (fgets(line, sizeof(line), f)) {
                printf("%s", line);
            }
            fclose(f);
        }
    } else {
        printf("\nFailed to generate report!\n");
    }
    

    free_manager_data();
    
    return 0;
}
