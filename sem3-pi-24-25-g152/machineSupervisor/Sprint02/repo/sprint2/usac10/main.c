#include <stdio.h>
#include "asm.h"


int main() {
    int vec[] = {-1,-3,-4,-2};
    int length = 4;
    int me;

    int result = median(vec, length, &me);

    for (int i = 0; i < length; i++) {
      printf("%d\n", vec[i]);
    }
printf("\n");

    if (result == 1) {
        printf("Array ordenado com %d elementos.\n", length);
        printf("Mediana: %d\n", me);
    } else {
        printf("Falha: tamanho inválido do array.\n");
    }

    return 0;
}
