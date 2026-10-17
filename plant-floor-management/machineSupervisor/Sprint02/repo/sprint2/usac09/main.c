#include "asm.h"
#include <stdio.h>


int main(void){
  // USBD01 DEVELOPMENT TESTES
	int vec[1] = {1};
	int res = sort_array(vec, 1, 1);

	printf("%d\n", res);

for(int i = 0; i < 1; i++){
	printf("%d\n", vec[i]);
}

  return 0;
}