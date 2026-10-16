#include "us.h"
#include <stdio.h>

void usage(char* y, char* x, char* str) {
  printf("string: %s\n", str);
}

int main(void){
  /* USBD01 TESTE

  char str []= " TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
  char token [] = "TEMP";
  char unit [20];
  int value;
  int res =  extract_data(str, token, unit, &value);
  printf ( "%d:%s ,%d\n", res , unit , value ) ; // 1: celsius ,20

  //char token2 [] = " AAA " ;
  //res = extract_data(str, token2, unit, &value);
  //printf ( "%d:%s, %d\n ", res, unit, value ) ; // 0: ,0
*/

  int vec[5] = {3, 6, 1, 7, 1};
  int len = sizeof(vec) / sizeof(vec[0]);
  int res = sort_array(vec, len, '0');

  printf("%d:\n ", res);
  for(int i = 0; i < len; i++){
    printf("%d ", vec[i]);
  }

  return 0;
}