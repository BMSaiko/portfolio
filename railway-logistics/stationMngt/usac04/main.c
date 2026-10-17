#include "ac04.h"
#include <stdio.h>
#include <string.h>


int format_command(char* op, int n, char *cmd);

int main(void) {
    int value = 5;

    char str[20] = "rB";
    char cmd[20];

    int res = format_command(str, value, cmd);
    printf("%d : %s\n", res, cmd); // 1: RB,05

    strcpy(str, "Ye");
    value = 25;
    res = format_command(str, value, cmd);
    printf("%d : %s\n", res, cmd); // 1: YE,25

    strcpy(str, "Ye");
    value = 125;
    res = format_command(str, value, cmd);
    printf("%d : %s\n", res, cmd); // 0:

    strcpy(str, "aaa");
    value = 25;
    res = format_command(str, value, cmd);
    printf("%d : %s\n", res, cmd); // 0:

    strcpy(str, "gTh");

    res = format_command(str, value, cmd);
    printf("%d : %s\n", res, cmd); // 1: GTH

    return 0;
}