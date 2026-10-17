#include <stdio.h>
#include <string.h>
#include "asm.h"

#define MAX_LENGTH 100

int main(void) {
    char input[MAX_LENGTH];
    char encrypted[MAX_LENGTH];
    int key, res_encrypt;

    // Test USAC01 - encrypt_data
    printf("=== Caesar Cipher Encryption ===\n");
    
    printf("Enter string (UPPERCASE A-Z only): ");
    scanf("%s", input);
    
    printf("Enter key (1-26): ");
    scanf("%d", &key);
    
    // Test encryption
    res_encrypt = encrypt_data(input, key, encrypted);
    if (res_encrypt) {
        printf("Encryption successful!\n");
        printf("Original: %s\n", input);
        printf("Encrypted: %s\n", encrypted);
    } else {
        printf("Encryption failed! Invalid input or key.\n");
        printf("Output set to: '%s'\n", encrypted);
    }
    
    
    // Additional test cases
    printf("\n=== Additional Test Cases ===\n");
    
    // Test case 1: Valid input
    char test1[] = "HELLO";
    char enc1[100];
    int key1 = 3;
    
    printf("\nTest 1 - 'HELLO' with key=3:\n");
    encrypt_data(test1, key1, enc1);
    printf("Original: %s\n", test1);
    printf("Encrypted: %s\n", enc1);
    
    // Test case 2: Invalid key
    char test2[] = "TEST";
    char enc2[100];
    int key2 = 30;  // Invalid key
    
    printf("\nTest 2 - Invalid key (30):\n");
    int res2 = encrypt_data(test2, key2, enc2);
    printf("Result: %d (0=failure, 1=success)\n", res2);
    printf("Output: '%s'\n", enc2);
    
    return 0;
}