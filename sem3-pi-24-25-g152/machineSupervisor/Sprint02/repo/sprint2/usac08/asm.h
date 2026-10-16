#ifndef US_H
#define US_H

int extract_data(char* str, char* token, char* unit, int* value);
int verifyToken(char* token);
void extractValue(char* str, int* value);
void extractUnit(char* str, char* unit);
char* Tokencheck(char* str, char* token);

int get_number_binary(int n, char* bits);
int get_number(char* str, int * n);
int format_command(char* op, int n, char *cmd);
int enqueue_value(int* buffer, int length, int* tail, int* head, int value);
int dequeue_value(int* buffer, int length, int* tail, int* head, int *value);
int get_n_element(int* buffer, int length, int* tail, int* head);
int move_n_to_array(int* buffer, int length, int *tail, int *head, int n, int* array);
int sort_array(int* vec, int length, char order);
int median(int* vec, int length, int *me);

#endif