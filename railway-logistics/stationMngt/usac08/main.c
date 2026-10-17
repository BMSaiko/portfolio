#include <stdio.h>
#include "ac08.h"

#define MAX_SIZE 100 

int sort_array(int* ptr, int num, char order);

int main(void) {
    int vec[MAX_SIZE];
    int length, i;
    int order_int; 

    printf("Enter the number of elements (up to %d): ", MAX_SIZE);
    scanf("%d", &length);

    
    if (length <= 0 || length > MAX_SIZE) {
        printf("Invalid number of elements.\n");
        
    } else {
        printf("Enter %d integers:\n", length);
        for (i = 0; i < length; i++) {
            scanf("%d", &vec[i]);
        }
    }

    printf("Enter sorting order (1 for ascending, 0 for descending): ");
    scanf("%d", &order_int);

    
    sort_array(vec, length, (char)order_int);

    
    printf("\nSorted array:\n");
    for (i = 0; i < length; i++) {
        printf("%d ", vec[i]);
    }
    printf("\n");

    return 0;
}