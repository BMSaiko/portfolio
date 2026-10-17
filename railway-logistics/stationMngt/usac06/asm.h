#ifndef ASM_H
#define ASM_H

int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value);

#endif