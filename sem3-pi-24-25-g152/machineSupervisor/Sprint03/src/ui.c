#include "../include/supervisor.h"

void display_menu() {
    printf("===========================================\n");
    printf("     Plant-floor Machine Management System\n");
    printf("===========================================\n");
    printf("1. Exibir status das máquinas\n");
    printf("2. Atribuir operação a uma máquina\n");
    printf("3. Gerenciar máquinas (Adicionar/Remover)\n");
    printf("4. Testar comunicação com máquina\n");
    printf("5. Visualizar histórico de operações\n");
    printf("6. Processar Instruções\n");
    printf("7. Sair\n");
    printf("===========================================\n");
    printf("Escolha uma opção: ");
}



void exibir_status_maquinas(PlantFloorManager *plantManager) {
    if (plantManager->machine_count == 0) {
        printf("Não há máquinas no Plant Floor.\n");
        return;
    }

    printf("\n=== Status das Máquinas no Plant Floor ===\n\n");
    
    for (int i = 0; i < plantManager->machine_count; i++) {
        Machine *machine = &plantManager->machines[i];

        check_operation_status(machine);
        
        printf("Máquina: %s (ID: %d)\n", machine->name, machine->identifier);
        printf("Estado: %s\n", machine->state);
        printf("Temperatura Atual: %d°C (Range: %d-%d°C)\n", 
               machine->temperature.current,
               machine->temperature.range.min,
               machine->temperature.range.max);
        printf("Humidade Atual: %d%% (Range: %d-%d%%)\n",
               machine->humidity.current,
               machine->humidity.range.min,
               machine->humidity.range.max);
        
        if (strcmp(machine->state, "OP") == 0) {
            printf("Status: Em operação\n");
            
            char filename[100];
            snprintf(filename, sizeof(filename), "data/machine_%d_operations.csv", machine->identifier);
            
            FILE *file = fopen(filename, "r");
            if (file != NULL) {
                char line[256];
                char last_line[256] = "";
                
                fgets(line, sizeof(line), file);  // Pular cabeçalho
                
                while (fgets(line, sizeof(line), file)) {
                    strcpy(last_line, line);
                }
                
                if (strlen(last_line) > 0) {
                    Operation op;
                    char op_name[50];
                    sscanf(last_line, "%d,%d,%[^,],%[^,],%d", 
                           &op.operation_id,
                           &op.machine_id,
                           op_name,
                           op.start_time,
                           &op.duration);
                    
                    printf("Operação Atual: %s (ID: %d)\n", op_name, op.operation_id);
                    printf("Início: %s\n", op.start_time);
                    printf("Duração: %d segundos\n", op.duration);
                }
                
                fclose(file);
            }
        }
        
        printf("----------------------------------------\n");
    }
}

void atribuir_operacao(Machine *machines, int machine_count, PlantFloorManager *plantManager) {
    if (plantManager->machine_count == 0) {
        printf("Não há máquinas no Plant Floor para atribuir operações.\n");
        return;
    }

    printf("\nMáquinas disponíveis no Plant Floor:\n");
    for (int i = 0; i < plantManager->machine_count; i++) {
        printf("[%d] %s (Estado: %s)\n", 
               plantManager->machines[i].identifier,
               plantManager->machines[i].name,
               plantManager->machines[i].state);
    }

    int machine_id;
    printf("Digite o ID da máquina: ");
    scanf("%d", &machine_id);

    Machine *target_machine = NULL;
    for (int i = 0; i < plantManager->machine_count; i++) {
        if (plantManager->machines[i].identifier == machine_id) {
            target_machine = &plantManager->machines[i];
            break;
        }
    }

    if (target_machine == NULL) {
        printf("Máquina não encontrada no Plant Floor.\n");
        return;
    }

    if (strcmp(target_machine->state, "OFF") == 0) {
        printf("Máquina está desligada. Ligue-a primeiro.\n");
        return;
    }

    if (strcmp(target_machine->state, "OP") == 0) {
        printf("Máquina já está em operação.\n");
        return;
    }

    Operation new_op;
    new_op.machine_id = machine_id;

    int max_op_id = 0;
    char filename[100];
    for (int i = 1; i <= machine_count; i++) {
        snprintf(filename, sizeof(filename), "data/machine_%d_operations.csv", i);
        FILE *file = fopen(filename, "r");
        if (file != NULL) {
            char line[256];
            int op_id;
            // Pular o cabeçalho
            fgets(line, sizeof(line), file);
            while (fgets(line, sizeof(line), file)) {
                sscanf(line, "%d,", &op_id);
                if (op_id > max_op_id) {
                    max_op_id = op_id;
                }
            }
            fclose(file);
        }
    }
    new_op.operation_id = max_op_id + 1;

    printf("Nome da operação: ");
    scanf(" %49[^\n]", new_op.operation_name);

    printf("Duração da operação (em segundos): ");
    scanf("%d", &new_op.duration);

    get_current_time(new_op.start_time, sizeof(new_op.start_time));

    log_operation(new_op, plantManager);
    strcpy(target_machine->state, "OP");

    printf("Operação %d atribuída à máquina %s (ID: %d)\n", 
           new_op.operation_id, target_machine->name, target_machine->identifier);
}

void visualizar_historico(PlantFloorManager *plantManager) {
    if (plantManager->machine_count == 0) {
        printf("Não há máquinas no Plant Floor.\n");
        return;
    }

    printf("\n=== Histórico de Operações do Plant Floor ===\n\n");

    for (int i = 0; i < plantManager->machine_count; i++) {
        Machine *machine = &plantManager->machines[i];
        
        char filepath[256];
        snprintf(filepath, sizeof(filepath), "data/machine_%d_operations.csv", machine->identifier);
        
        FILE *file = fopen(filepath, "r");
        if (file == NULL) {
            printf("Sem histórico para máquina %s (ID: %d)\n", 
                   machine->name, machine->identifier);
            continue;
        }

        printf("Máquina: %s (ID: %d)\n", machine->name, machine->identifier);
        printf("Estado Atual: %s\n", machine->state);
        printf("----------------------------------------\n");

        char line[256];
        int is_header = 1;
        
        while (fgets(line, sizeof(line), file)) {
            if (is_header) {
                is_header = 0;
                continue;
            }

            Operation op;
            char op_name[50];
            char start_time[20];
            int duration;
            
            sscanf(line, "%d,%d,%[^,],%[^,],%d", 
                   &op.operation_id,
                   &op.machine_id,
                   op_name,
                   start_time,
                   &duration);
            
            printf("Operação ID: %d\n", op.operation_id);
            printf("Nome: %s\n", op_name);
            printf("Início: %s\n", start_time);
            printf("Duração: %d segundos\n", duration);
            printf("----------------------------------------\n");
        }

        fclose(file);
        printf("\n");
    }
}

/**
 * Exibe as máquinas disponíveis e solicita que o usuário escolha uma delas.
 *
 * @param machines Array de máquinas disponíveis.
 * @param machine_count Número de máquinas no array.
 * @return O identificador da máquina escolhida, ou -1 em caso de erro.
 */
int select_machine(const Machine *machines, int machine_count) {
    if (machines == NULL || machine_count == 0) {
        printf("Nenhuma máquina disponível.\n");
        return -1;
    }

    printf("Máquinas disponíveis:\n");
    for (int i = 0; i < machine_count; i++) {
        printf("[%d] %s (ID: %d)\n", i + 1, machines[i].name, machines[i].identifier);
    }

    int choice = -1;
    while (1) {
        printf("Escolha uma máquina (1-%d): ", machine_count);
        if (scanf("%d", &choice) != 1) {
            printf("Entrada inválida. Por favor, tente novamente.\n");
            while (getchar() != '\n');
        } else if (choice < 1 || choice > machine_count) {
            printf("Opção fora do intervalo. Tente novamente.\n");
        } else {
            break;
        }
    }

    return machines[choice - 1].identifier;
}

void gerenciar_maquinas(Machine *existing_machines, int existing_count, Machine **plant_floor_machines, int *plant_count) {
    int choice;
    printf("\nGerenciar Máquinas no PlantFloorManager:\n");
    printf("1. Adicionar Máquina ao PlantFloorManager\n");
    printf("2. Remover Máquina do PlantFloorManager\n");
    printf("Escolha uma opção: ");
    scanf("%d", &choice);

    if (choice == 1) {
        printf("\nMáquinas Disponíveis para Adicionar:\n");
        int has_available = 0;
        for (int i = 0; i < existing_count; i++) {
            int already_added = 0;
            for (int j = 0; j < *plant_count; j++) {
                if (existing_machines[i].identifier == (*plant_floor_machines)[j].identifier) {
                    already_added = 1;
                    break;
                }
            }

            if (!already_added) {
                has_available = 1;
                printf("ID: %d, Nome: %s\n", existing_machines[i].identifier, existing_machines[i].name);
            }
        }

        if (!has_available) {
            printf("Nenhuma máquina disponível para adicionar.\n");
            return;
        }

        int machine_id;
        printf("Digite o ID da máquina que deseja adicionar: ");
        scanf("%d", &machine_id);

        add_machine_to_plantfloor_from_existing(
            existing_machines, existing_count,
            plant_floor_machines, plant_count, machine_id
        );

    } else if (choice == 2) {
        printf("\nMáquinas no PlantFloorManager para Remover:\n");
        if (*plant_count == 0) {
            printf("Nenhuma máquina no PlantFloorManager.\n");
            return;
        }

        for (int i = 0; i < *plant_count; i++) {
            printf("ID: %d, Nome: %s\n", (*plant_floor_machines)[i].identifier, (*plant_floor_machines)[i].name);
        }

        int machine_id;
        printf("Digite o ID da máquina que deseja remover: ");
        scanf("%d", &machine_id);

        remove_machine_from_plantfloor(plant_floor_machines, plant_count, machine_id);

    } else {
        printf("Opção inválida.\n");
    }
}

