#include <stdio.h>

extern int move_n_to_array(int* buffer, int length, int* tail, int* head, int n, int* array);

int main() {
    int buffer[6] = {1, 2, 3, 5, 7, 8};  // Buffer circular com 5 posições
    int array[6] = {0};                    // Array para receber os elementos removidos
    int length = 6;                        // Tamanho do buffer
    int tail = 4;                // Ponteiro para o início do buffer
    int head = 2;                // Ponteiro para a posição atual no buffer


    //printf("%d\n",move_n_to_array(buffer, length, tail, head, 3, array));


    // Cenário 1: Movendo 2 elementos do buffer para o array
    printf("Teste 1: Movendo 2 elementos\n");
    int n = 4;
    int result = move_n_to_array(buffer, length, &tail, &head, n, array);

    if (result) {
        printf("Sucesso: Array = ");
        for (int i = 0; i < n; i++) {
            printf("%d ", array[i]);
        }
        printf("\n");
    } else {
        printf("Falha: Não foi possível mover %d elementos.\n", n);
    }


    return 0;
}