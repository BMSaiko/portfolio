#include <stdio.h>
#include "asm.h"


#define BUFFER_SIZE 5
int buffer[BUFFER_SIZE] = {10, 20, 30, 40, 50};
int head = 0;
int tail = BUFFER_SIZE;


extern int dequeue_value(int *head, int *tail, int *buffer, int *result, int size);

int main() {
    int result;
    int success;

    printf("Initial buffer: ");
    for (int i = 0; i < BUFFER_SIZE; i++) {
        printf("%d ", buffer[i]);
    }
    printf("\n");

    // Call dequeue_value
    success = dequeue_value(&head, &tail, buffer, &result, BUFFER_SIZE);

    if (success) {
        printf("Dequeued value: %d\n", result);
    } else {
        printf("Buffer is empty.\n");
    }

    printf("New head index: %d\n", head);
    return 0;
}

