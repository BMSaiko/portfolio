#include "ac09.h"
#include <stdio.h>

int main(void) {
    int vec[] = {1, 3, 5, 7, 9};
    int length = 5;
    int me = 0;

    int result = median(vec, length, &me);
    printf("Median: %d, result: %d\n", me, result);
    return 0;
}