#include <stdio.h>
#include <string.h>
#include "ac03.h"

int main(void) {
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char token[5] = "TEMP";
    char token2[5] = "HUM";
    char unit[20];
    int value;
    int res = extract_data(str, token, unit, &value);
    if (res == 1) {
        printf("Unit: %s\n", unit);
        printf("Value: %d\n", value);
    } else {
        printf("Dados não encontrados.\n");
    }
    int res2 = extract_data(str, token2, unit, &value);
    if (res2 == 1) {
        printf("Unit: %s\n", unit);
        printf("Value: %d\n", value);
    } else {
        printf("Dados não encontrados.\n");
    }

    return 0;
}