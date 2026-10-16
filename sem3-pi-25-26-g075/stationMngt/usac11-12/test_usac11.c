
#include <stdio.h>
#include <string.h>
#include "manager.h"
#include "asm.h"

int main(int argc, char* argv[]) {
    char* config_file = "config.txt";
    
    if (argc > 1) {
        config_file = argv[1];
    }
    
    printf("=== Test USAC11 - Configuration Loading ===\n");
    printf("Configuration file: %s\n\n", config_file);
    
    // Load config
    if (load_config(config_file)) {
        printf("\nConfiguration loaded successfully!\n");
        

        print_manager_status();
        

        printf("\n=== Encryption Test ===\n");
        if (user_count > 0) {
            printf("Testing encryption for user '%s':\n", users[0].username);
            printf("Original password: %s\n", users[0].password);
            printf("Encrypted password (stored): %s\n", users[0].encrypted_password);
            

            char test_encrypted[50];
            encrypt_data(users[0].password, users[0].caesar_key, test_encrypted);
            printf("New encryption: %s\n", test_encrypted);
            
            if (strcmp(users[0].encrypted_password, test_encrypted) == 0) {
                printf("✓ Encryption consistent!\n");
            } else {
                printf("✗ Error: Encryption inconsistent!\n");
            }
        }

        free_manager_data();
        printf("\nMemory freed.\n");
    } else {
        printf("Failed to load configuration!\n");
    }
    
    return 0;
}
