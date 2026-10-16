#include "../include/supervisor.h"
#include "../include/machine.h"

int select_machine(const Machine *machines, int machine_count);


int main(void) {
    int machine_count = 0;
    Machine *machines = NULL;

    PlantFloorManager plantManager;
    plantManager.machines = NULL;
    plantManager.machine_count = 0;

    printf("Carregando configurações das máquinas...\n");
    int res = carregar_configuracao(&machines, &machine_count);
    if (res != 0 || machines == NULL) {
        printf("Falha ao carregar configurações das máquinas.\n");
        return 1;
    }

    for (int i = 0; i < machine_count; i++) {
        initialize_csv(machines[i].identifier);
    }

    while (1) {
        for (int i = 0; i < plantManager.machine_count; i++) {
            check_operation_status(&plantManager.machines[i]);
        }

        display_menu();
        int opcao = get_menu_option(1, 7);

        switch (opcao) {
            case 1:
                exibir_status_maquinas(&plantManager);
            break;
            case 2:
                atribuir_operacao(machines, machine_count, &plantManager);
            break;
            case 3:
                gerenciar_maquinas(machines, machine_count, &plantManager.machines, &plantManager.machine_count);
            break;
            case 4:
                char cmd[13] = "ON,1,1,1,1,1";
                char *response = simulate_machine(cmd);
                printf("Response: %s\n", response);
            break;
            case 5:
                visualizar_historico(&plantManager);
            break;
            case 6:
                carregar_e_processar_instrucoes(&plantManager);
            break;
            case 7:
                printf("Saindo do programa...\n");
                cleanup_machines(machines, machine_count);
                exit(0);
            break;
            default:
                printf("Opção inválida. Tente novamente.\n");
        }

        printf("\n");
    }

    return 0;
}