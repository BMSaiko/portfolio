

#ifndef ASM_H
#define ASM_H


// USAC01
int encrypt_data(char* in, int key, char* out);

// USAC03
int get_value_by_key(char* str, char* key, char* value_buffer);

// USAC05
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);

// USAC09
int median(int* ptr, int num, int* me);



int decrypt_data(char* in, int key, char* out); // USAC02
int format_command(char* op, int n, char *cmd); // USAC04
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int* value); // USAC06
int move_n_to_array(int* buffer, int length, int* n_elem, int* rd, int* wr, int num, int* vec); // USAC07
int sort_array(int* ptr, int num, char order); // USAC08


#endif // ASM_H