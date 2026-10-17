#include "ac07.h"
#include <stdio.h>

int main(void) {
    int buffer1[] = {10, 20, 30, 40, 50};
    int length1 = 5;
    int nelem1 = 5;
    int tail1 = 0;
    int head1 = 0;
    int n1 = 3;
    int array1[3];

    int res1 = move_n_to_array(buffer1, length1, &nelem1, &tail1, &head1, n1, array1);
    printf("Result: %d\n", res1);
    printf("Array: ");
    for (int i = 0; i < n1; i++) {
        printf("%d ", array1[i]);
    }
    printf("\n");

    int buffer2[] = {100, 200};
    int length2 = 2;
    int nelem2 = 2;
    int tail2 = 0;
    int head2 = 0;
    int n2 = 3;
    int array2[3];

    int res2 = move_n_to_array(buffer2, length2, &nelem2, &tail2, &head2, n2, array2);
    printf("Result: %d\n", res2);
    printf("Array: ");
    for (int i = 0; i < (res2 > 0 ? n2 : 0); i++) {
        printf("%d ", array2[i]);
    }
    printf("\n");

    return 0;
}
