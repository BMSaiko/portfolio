#include "../include/supervisor.h"

void get_current_time(char *buffer, size_t size) {
    time_t now = time(NULL);
    struct tm *t = localtime(&now);
    strftime(buffer, size, "%Y-%m-%d %H:%M:%S", t);
}

void add_seconds_to_time(const char *start_time, char *end_time, int duration_in_seconds) {
    struct tm t;
    strptime(start_time, "%Y-%m-%d %H:%M:%S", &t);
    time_t start = mktime(&t);
    time_t end = start + duration_in_seconds;
    struct tm *result = localtime(&end);
    strftime(end_time, 20, "%Y-%m-%d %H:%M:%S", result);
}

/**
 * Solicita ao usuário uma opção dentro de um intervalo definido.
 *
 * @param min O valor mínimo permitido.
 * @param max O valor máximo permitido.
 * @return A opção válida escolhida pelo usuário.
 */
int get_menu_option(int min, int max) {
    int option;

    while (1) {
        printf("Escolha uma opção (%d-%d): ", min, max);
        if (scanf("%d", &option) != 1) {
            printf("Entrada inválida. Por favor, tente novamente.\n");
            while (getchar() != '\n');
        } else if (option < min || option > max) {
            printf("Opção fora do intervalo (%d-%d). Tente novamente.\n", min, max);
        } else {
            return option;
        }
    }
}

void cleanup_machines(Machine *machines, int count) {
    if (machines != NULL) {
        for (int i = 0; i < count; i++) {
            if (machines[i].temperature.buffer.data != NULL) {
                free(machines[i].temperature.buffer.data);
            }

            if (machines[i].humidity.buffer.data != NULL) {
                free(machines[i].humidity.buffer.data);
            }

            if (machines[i].operations != NULL) {
                free(machines[i].operations);
            }
        }
        free(machines);
    }
}