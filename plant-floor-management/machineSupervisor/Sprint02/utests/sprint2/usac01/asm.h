#ifndef ASM_H 
#define ASM_H 
int extract_data(char* str, char* token, char* unit, int* value);
int verifyToken(char* token);
void extractValue(char* str, int* value);
void extractUnit(char* str, char* unit);
char* Tokencheck(char* str, char* token);

#endif 

