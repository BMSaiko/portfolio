#include <stdio.h>
#include "get_n_element.h"

int main() {
    int buffer[] = {10, 20, 30, 40, 50};
    int length = 5;
    int value = 0;
    int tail = 0; // Não utilizado, mas incluído para compatibilidade
    int head = 2;

    // Teste 1: Índice válido
    if (get_n_element(buffer, length, &tail, &head, &value) == 1) {
        printf("Teste 1: Sucesso! Valor: %d (Esperado: 30)\n", value);
    } else {
        printf("Teste 1: Falhou!\n");
    }

    // Teste 2: Índice inválido (fora do intervalo)
    head = 6;
    if (get_n_element(buffer, length, &tail, &head, &value) == 0) {
        printf("Teste 2: Sucesso! Índice inválido tratado corretamente.\n");
    } else {
        printf("Teste 2: Falhou!\n");
    }

    // Teste 3: Índice negativo
    head = -1;
    if (get_n_element(buffer, length, &tail, &head, &value) == 0) {
        printf("Teste 3: Sucesso! Índice negativo tratado corretamente.\n");
    } else {
        printf("Teste 3: Falhou!\n");
    }

    // Teste 4: Buffer vazio
    int empty_buffer[1];
    length = 0;
    head = 0;
    if (get_n_element(empty_buffer, length, &tail, &head, &value) == 0) {
        printf("Teste 4: Sucesso! Buffer vazio tratado corretamente.\n");
    } else {
        printf("Teste 4: Falhou!\n");
    }

    // Teste 5: Índice no limite superior
    int buffer2[] = {5, 15, 25, 35};
    length = 4;
    head = 3; // Último índice válido
    if (get_n_element(buffer2, length, &tail, &head, &value) == 1) {
        printf("Teste 5: Sucesso! Valor: %d (Esperado: 35)\n", value);
    } else {
        printf("Teste 5: Falhou!\n");
    }

    return 0;
}