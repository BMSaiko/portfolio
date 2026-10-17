#include <stdio.h>
#include <string.h>
#include "asm.h"

#define MAX_LENGTH 100
#define BUFFER_SIZE 5

void print_buffer(int* buffer, int length, int nelem, int tail, int head) {
    printf("Buffer: [");
    for (int i = 0; i < length; i++) {
        if (i == tail && i == head && nelem > 0) {
            printf(" T/H:%d", buffer[i]);
        } else if (i == tail && nelem > 0) {
            printf(" T:%d", buffer[i]);
        } else if (i == head && nelem > 0) {
            printf(" H:%d", buffer[i]);
        } else {
            printf(" %d", buffer[i]);
        }
        if (i < length - 1) printf(",");
    }
    printf(" ] | nelem=%d, tail=%d, head=%d\n", nelem, tail, head);
}

int main(void) {
    printf("=== Testing USAC05 - Circular Buffer ===\n\n");
    
    int buffer[BUFFER_SIZE] = {0};
    int length = BUFFER_SIZE;
    int nelem = 0;
    int tail = 0;
    int head = 0;
    int value, result;
    
    printf("Initial state:\n");
    print_buffer(buffer, length, nelem, tail, head);
    
    // Test 1: Enqueue values until buffer is full
    printf("\n--- Test 1: Enqueue values ---\n");
    for (int i = 1; i <= 6; i++) {
        result = enqueue_value(buffer, length, &nelem, &tail, &head, i * 10);
        printf("Enqueue %d: result=%d (1=full, 0=not full)\n", i * 10, result);
        print_buffer(buffer, length, nelem, tail, head);
    }
    
    // Test 3: Enqueue more values (wraparound)
    printf("\n--- Test 3: Enqueue with wraparound ---\n");
    for (int i = 7; i <= 9; i++) {
        result = enqueue_value(buffer, length, &nelem, &tail, &head, i * 10);
        printf("Enqueue %d: result=%d (1=full, 0=not full)\n", i * 10, result);
        print_buffer(buffer, length, nelem, tail, head);
    }
    
    // Test 6: Enqueue after being empty
    printf("\n--- Test 6: Enqueue after empty ---\n");
    result = enqueue_value(buffer, length, &nelem, &tail, &head, 100);
    printf("Enqueue 100: result=%d\n", result);
    print_buffer(buffer, length, nelem, tail, head);
    
    printf("\n=== Circular Buffer Tests Completed ===\n");
    
    return 0;
}