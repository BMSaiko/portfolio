/**
 * @file main.c
 * @brief Circular buffer implementation demonstration
 *
 * This program demonstrates the usage of a circular buffer implementation
 * using a fixed-size array. It shows enqueuing operations and buffer state.
 */

#include <stdio.h>
#include "asm.h"

/**
 * @brief Main function that demonstrates circular buffer operations
 * @return Returns 0 on successful execution
 */
int main() {
    int buffer[5] = {0};    /* Circular buffer array */
    int length = 5;         /* Size of the buffer */
    int head = 0, tail = 0; /* Buffer pointers */

    printf("Inserting values into buffer:\n");
    /* Attempt to insert 7 values into a buffer of size 5 */
    for (int i = 1; i <= 7; i++) {
        int result = enqueue_value(buffer, length, &tail, &head, i);
        printf("Inserted %d | Head: %d | Tail: %d | Full: %d\n", i, head, tail, result);
    }

    printf("Final buffer state:\n");
    /* Display final contents of the buffer */
    for (int i = 0; i < length; i++) {
        printf("buffer[%d] = %d\n", i, buffer[i]);
    }
    return 0;
}