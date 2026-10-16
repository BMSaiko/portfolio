#include "asm.h"
#include <stdio.h>


int main(void){
  // USBD01 DEVELOPMENT TESTES
  char* str= "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
  char* token = "TEMP";
  char unit[100];
  int value;

  int res = extract_data(str, token, unit, &value);
  printf("%d\n", res);
  printf("%s\n", unit);
  printf("%d\n", value);

  return 0;
}