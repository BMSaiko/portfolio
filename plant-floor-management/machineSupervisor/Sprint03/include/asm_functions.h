#ifndef ASM_FUNCTIONS_H
#define ASM_FUNCTIONS_H

// USAC01 - Extract data and string manipulation functions
// Returns: 1 if successful, 0 if failed
// Parameters: input string, token to find, buffer for unit, pointer to store value
int extract_data(char* str, char* token, char* unit, int* value);
int tokenCheck(char* str, char* token);
int extractUnit(char* str, char* unit);
int extractValue(char* str, int* value);
int verifyToken(char* token);

// USAC02 - Convert number to binary array
// Returns: 1 if successful, 0 if failed
// Parameters: number to convert (0-31), array to store binary digits
int get_number_binary(int value, char* binary_array);

// USAC03 - Get number from string
// Returns: 1 if successful, 0 if failed
// Parameters: input string, pointer to store result
int get_number(char* str, int* result);

// USAC04 - Format command and string manipulation functions
// Returns: 1 if successful, 0 if failed
// Parameters: input string, number to convert, output buffer
int format_command(char* input, int number, char* output);
int TrimAndCapitalize(char* dest, char* src);
int CopyString(char* dest, char* src);
int ValidateCommand(char* cmd);
int WriteBinaryString(char* dest, char* src);

// USAC05 - Enqueue value to circular buffer
// Returns: 1 if successful, 0 if failed
// Parameters: buffer, length, tail pointer, head pointer, value to enqueue
int enqueue_value(int* buffer, int length, int* tail, int* head, int value);

// USAC06 - Dequeue value from circular buffer
// Returns: 1 if successful, 0 if failed
// Parameters: buffer, length, tail pointer, head pointer, pointer to store value
int dequeue_value(int* buffer, int length, int* tail, int* head, int* value);

// USAC07 - Get number of elements in circular buffer
// Returns: number of elements in buffer, 0 if error
// Parameters: buffer, length, tail pointer, head pointer
int get_n_element(int* buffer, int length, int* tail, int* head);

// USAC08 - Move n elements from buffer to array
// Returns: 1 if successful, 0 if failed
// Parameters: buffer, length, tail pointer, head pointer, n elements, destination array
int move_n_to_array(int* buffer, int length, int* tail, int* head, int n, int* array);

// USAC09 - Sort array
// Returns: 1 if successful, 0 if failed
// Parameters: array to sort, length, order (1 for ascending, 0 for descending)
int sort_array(int* array, int length, char order);

// USAC10 - Calculate median
// Returns: 1 if successful, 0 if failed
// Parameters: array, length, pointer to store median
int median(int* array, int length, int* result);

#endif // ASM_FUNCTIONS_H 