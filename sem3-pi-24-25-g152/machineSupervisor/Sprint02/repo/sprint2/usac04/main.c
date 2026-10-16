#include <stdio.h>

int format_command(char* op, int n, char *cmd);

int main(void) {
    int value = 26;
    char str[] = "ON";
    char cmd[50];
    int res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); // Expected output: 1: ON,11010

    char str2[] = "aaa";
    res = format_command(str2, value, cmd);
    printf("%d: %s\n", res, cmd); // Expected output: 0:

    return 0;
}
