
#ifndef UTILS_H
#define UTILS_H
#include "../include/supervisor.h"

void get_current_time(char *buffer, size_t size);

void add_seconds_to_time(const char *start_time, char *end_time, int duration_in_seconds);

/**
 * Solicita ao usuário uma opção dentro de um intervalo definido.
 *
 * @param min O valor mínimo permitido.
 * @param max O valor máximo permitido.
 * @return A opção válida escolhida pelo usuário.
 */
int get_menu_option(int min, int max);

void cleanup_machines(Machine *machines, int count);
#endif
