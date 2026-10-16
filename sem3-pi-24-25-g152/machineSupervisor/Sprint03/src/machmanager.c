#include "../include/supervisor.h"
#include <time.h>
#include <unistd.h>

int carregar_configuracao(Machine **machines, int *machine_count) {
    printf("Carregando configuração das máquinas...\n");
    *machines = load_machine_setup_dynamic("data/machine_setup.csv", machine_count);

    if (machines == NULL) {
        printf("Failed to load machine setup.\n");
        return 1;
    }

    printf("Loaded %d machines:\n", *machine_count);
    for (int i = 0; i < *machine_count; i++) {
        printf("Machine ID: %d, Name: %s\n",
               (*machines)[i].identifier,
               (*machines)[i].name);
        printf("Temperature Range: %d-%d\n",
               (*machines)[i].temperature.range.min,
               (*machines)[i].temperature.range.max);
        printf("Humidity Range: %d-%d\n",
               (*machines)[i].humidity.range.min,
               (*machines)[i].humidity.range.max);
        printf("Buffer Sizes - Temperature: %d, Humidity: %d\n",
               (*machines)[i].temperature.buffer.size,
               (*machines)[i].humidity.buffer.size);
        printf("Median Window: %d\n",
               (*machines)[i].moving_median_window_length);
        printf("-------------------\n");
    }
    return 0;
}

void initialize_machine(Machine *machine, int id, const char *name, int buffer_size) {
    machine->identifier = id;
    strncpy(machine->name, name, sizeof(machine->name) - 1);
    machine->name[sizeof(machine->name) - 1] = '\0';

    machine->temperature.current = 0;
    machine->temperature.buffer.data = malloc(buffer_size * sizeof(int));
    machine->temperature.buffer.size = buffer_size;
    machine->temperature.buffer.head = 0;
    machine->temperature.buffer.tail = 0;

    machine->humidity.current = 0;
    machine->humidity.buffer.data = malloc(buffer_size * sizeof(int));
    machine->humidity.buffer.size = buffer_size;
    machine->humidity.buffer.head = 0;
    machine->humidity.buffer.tail = 0;

    strcpy(machine->state, "OFF");
    machine->operations = NULL;
    machine->op_count = 0;
}

/**
 * Função que carrega a configuração das máquinas de um arquivo CSV de forma dinâmica.
 *
 * @param filename Nome do arquivo CSV contendo as configurações.
 * @param count Ponteiro para o número de máquinas carregadas.
 * @return Ponteiro para o array dinâmico de máquinas, ou NULL em caso de erro.
 *
 * @note A função utiliza `strtok` para tokenizar cada linha do arquivo e `atoi`
 *       para converter valores numéricos de string para inteiros.
 */
Machine* load_machine_setup_dynamic(const char *filename, int *count) {
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        perror("Error opening file");
        return NULL;
    }

    char line[256];
    int capacity = 10;
    *count = 0;
    Machine *machines = malloc(capacity * sizeof(Machine));
    if (machines == NULL) {
        perror("Memory allocation failed");
        fclose(file);
        return NULL;
    }

    while (fgets(line, sizeof(line), file)) {
        if (*count >= capacity) {
            capacity *= 2;
            Machine *temp = realloc(machines, capacity * sizeof(Machine));
            if (temp == NULL) {
                perror("Memory reallocation failed");
                free(machines);
                fclose(file);
                return NULL;
            }
            machines = temp;
        }

        line[strcspn(line, "\n")] = '\0';

        char *token = strtok(line, ",");
        if (token == NULL) continue;
        machines[*count].identifier = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        strncpy(machines[*count].name, token, sizeof(machines[*count].name) - 1);
        machines[*count].name[sizeof(machines[*count].name) - 1] = '\0';

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        machines[*count].temperature.range.min = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        machines[*count].temperature.range.max = atoi(token);
        machines[*count].temperature.current = 0;

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        machines[*count].humidity.range.min = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        machines[*count].humidity.range.max = atoi(token);
        machines[*count].humidity.current = 0;

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        int buffer_size = atoi(token);

        machines[*count].temperature.buffer.size = buffer_size;
        machines[*count].humidity.buffer.size = buffer_size;

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        machines[*count].moving_median_window_length = atoi(token);

        machines[*count].operations = NULL;
        machines[*count].op_count = 0;

        machines[*count].temperature.buffer.data = malloc(buffer_size * sizeof(int));
        if (machines[*count].temperature.buffer.data == NULL) {
            perror("Error allocating temperature buffer for machine");
            free(machines);
            fclose(file);
            return NULL;
        }
        machines[*count].temperature.buffer.head = 0;
        machines[*count].temperature.buffer.tail = 0;

        machines[*count].humidity.buffer.data = malloc(buffer_size * sizeof(int));
        if (machines[*count].humidity.buffer.data == NULL) {
            perror("Error allocating humidity buffer for machine");
            free(machines[*count].temperature.buffer.data);
            free(machines);
            fclose(file);
            return NULL;
        }
        machines[*count].humidity.buffer.head = 0;
        machines[*count].humidity.buffer.tail = 0;

        strcpy(machines[*count].state, "OFF");

        (*count)++;
    }

    fclose(file);
    return machines;
}

void initialize_csv(int machine_id) {
    struct stat st = {0};
    if (stat("data", &st) == -1) {
        #ifdef _WIN32
            _mkdir("data");
        #else
            mkdir("data", 0755);
        #endif
    }

    char filename[150];
    snprintf(filename, sizeof(filename), "data/machine_%d_operations.csv", machine_id);

    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        perror("Error creating CSV file");
        return;
    }

    fprintf(file, "OperationID,MachineID,OperationName,StartTime,Duration\n");
    fclose(file);
}

void log_operation(Operation op, PlantFloorManager *plantManager) {
    char filename[100];
    snprintf(filename, sizeof(filename), "data/machine_%d_operations.csv", op.machine_id);

    struct stat st = {0};
    if (stat("data", &st) == -1) {
        #ifdef _WIN32
            _mkdir("data");
        #else
            mkdir("data", 0755);
        #endif
    }

    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Error opening CSV file for appending");
        return;
    }

    fprintf(file, "%d,%d,%s,%s,%d\n",
            op.operation_id,
            op.machine_id,
            op.operation_name,
            op.start_time,
            op.duration);

    fclose(file);

    char cmd[50];

    if(!format_command("OP", op.machine_id, cmd)) {
      perror("Error formatting command");
    }

    char *response = simulate_machine(cmd);
    printf("Machine response: %s\n", response);

    for (int i = 0; i < plantManager->machine_count; i++) {
        if (plantManager->machines[i].identifier == op.machine_id) {
            process_machine_response(&plantManager->machines[i], response);
            break;
        }
    }
}

void add_machine_to_plantfloor_from_existing(
    Machine *existing_machines, int existing_count,
    Machine **plant_floor_machines, int *plant_count, int machine_id
) {
    for (int i = 0; i < existing_count; i++) {
        if (existing_machines[i].identifier == machine_id) {
            *plant_floor_machines = realloc(*plant_floor_machines, (*plant_count + 1) * sizeof(Machine));
            if (*plant_floor_machines == NULL) {
                perror("Erro ao realocar memória para PlantFloorManager");
                exit(EXIT_FAILURE);
            }

            (*plant_floor_machines)[*plant_count] = existing_machines[i];
            strcpy((*plant_floor_machines)[*plant_count].state, "ON");
            (*plant_count)++;

            printf("Máquina '%s' (ID: %d) adicionada ao PlantFloorManager e ligada.\n",
                   existing_machines[i].name, machine_id);
            return;
        }
    }
    printf("Máquina com ID %d não encontrada na lista de máquinas existentes.\n", machine_id);
}

void remove_machine_from_plantfloor(
    Machine **plant_floor_machines,
    int *plant_count,
    int machine_id
) {
    for (int i = 0; i < *plant_count; i++) {
        if ((*plant_floor_machines)[i].identifier == machine_id) {

            if (strcmp((*plant_floor_machines)[i].state, "OP") == 0) {
                printf("Máquina com ID %d está em uso (state = \"OP\"). "
                       "Não é possível removê-la.\n", machine_id);
                return;
            }

            strcpy((*plant_floor_machines)[i].state, "OFF");

            for (int j = i; j < *plant_count - 1; j++) {
                (*plant_floor_machines)[j] = (*plant_floor_machines)[j + 1];
            }

            (*plant_count)--;
            *plant_floor_machines = realloc(*plant_floor_machines, (*plant_count) * sizeof(Machine));
            if (*plant_floor_machines == NULL && *plant_count > 0) {
                perror("Erro ao realocar memória após remoção");
                exit(EXIT_FAILURE);
            }

            printf("Máquina com ID %d desligada e removida do PlantFloorManager.\n", machine_id);
            return;
        }
    }
    printf("Máquina com ID %d não encontrada no PlantFloorManager.\n", machine_id);
}



void process_machine_response(Machine *machine, const char *response) {
    char unit[20];
    int value;
    char *response_copy = strdup(response);

    if (extract_data(response_copy, "TEMP", unit, &value)) {
        enqueue_value(machine->temperature.buffer.data,
                     machine->temperature.buffer.size,
                     &machine->temperature.buffer.tail,
                     &machine->temperature.buffer.head,
                     value);
        machine->temperature.current = value;

        int n_elements = get_n_element(machine->temperature.buffer.data,
                                     machine->temperature.buffer.size,
                                     &machine->temperature.buffer.tail,
                                     &machine->temperature.buffer.head);

        if (n_elements >= machine->moving_median_window_length) {
            int *temp_values = malloc(machine->moving_median_window_length * sizeof(int));
            if (move_n_to_array(machine->temperature.buffer.data,
                              machine->temperature.buffer.size,
                              &machine->temperature.buffer.tail,
                              &machine->temperature.buffer.head,
                              machine->moving_median_window_length,
                              temp_values)) {

                int median_temp;
                sort_array(temp_values, machine->moving_median_window_length, 1);
                if (median(temp_values, machine->moving_median_window_length, &median_temp)) {
                    if (median_temp > machine->temperature.range.max) {
                        printf("ALERTA: Máquina %d - Temperatura muito alta (mediana: %d°C, máximo: %d°C)\n",
                               machine->identifier, median_temp, machine->temperature.range.max);
                    }
                    else if (median_temp < machine->temperature.range.min) {
                        printf("ALERTA: Máquina %d - Temperatura muito baixa (mediana: %d°C, mínimo: %d°C)\n",
                               machine->identifier, median_temp, machine->temperature.range.min);
                    }
                }
            }
            free(temp_values);
        }
    }

    if (extract_data(response_copy, "HUM", unit, &value)) {
        enqueue_value(machine->humidity.buffer.data,
                     machine->humidity.buffer.size,
                     &machine->humidity.buffer.tail,
                     &machine->humidity.buffer.head,
                     value);
        machine->humidity.current = value;

        int n_elements = get_n_element(machine->humidity.buffer.data,
                                     machine->humidity.buffer.size,
                                     &machine->humidity.buffer.tail,
                                     &machine->humidity.buffer.head);

        if (n_elements >= machine->moving_median_window_length) {
            int *hum_values = malloc(machine->moving_median_window_length * sizeof(int));
            if (move_n_to_array(machine->humidity.buffer.data,
                              machine->humidity.buffer.size,
                              &machine->humidity.buffer.tail,
                              &machine->humidity.buffer.head,
                              machine->moving_median_window_length,
                              hum_values)) {

                int median_hum;
                sort_array(hum_values, machine->moving_median_window_length, 1);
                if (median(hum_values, machine->moving_median_window_length, &median_hum)) {
                    if (median_hum > machine->humidity.range.max) {
                        printf("ALERTA: Máquina %d - Humidade muito alta (mediana: %d%%, máximo: %d%%)\n",
                               machine->identifier, median_hum, machine->humidity.range.max);
                    }
                    else if (median_hum < machine->humidity.range.min) {
                        printf("ALERTA: Máquina %d - Humidade muito baixa (mediana: %d%%, mínimo: %d%%)\n",
                               machine->identifier, median_hum, machine->humidity.range.min);
                    }
                }
            }
            free(hum_values);
        }
    }

    free(response_copy);
}


void check_operation_status(Machine *machine) {
    char filepath[256];
    snprintf(filepath, sizeof(filepath), "data/machine_%d_operations.csv", machine->identifier);

    FILE *file = fopen(filepath, "r");
    if (file == NULL) {
        return;
    }

    char line[256];
    char last_line[256] = "";
    int is_header = 1;

    while (fgets(line, sizeof(line), file)) {
        if (is_header) {
            is_header = 0;
            continue;
        }
        strcpy(last_line, line);
    }

    fclose(file);

    if (strlen(last_line) > 0) {
        Operation op;
        sscanf(last_line, "%d,%d,%[^,],%[^,],%d",
               &op.operation_id,
               &op.machine_id,
               op.operation_name,
               op.start_time,
               &op.duration);

        char current_time[20];
        get_current_time(current_time, sizeof(current_time));

        time_t start_time_t, current_time_t;
        struct tm start_tm = {0}, current_tm = {0};
        strptime(op.start_time, "%Y-%m-%d %H:%M:%S", &start_tm);
        strptime(current_time, "%Y-%m-%d %H:%M:%S", &current_tm);
        start_time_t = mktime(&start_tm);
        current_time_t = mktime(&current_tm);

        double elapsed_seconds = difftime(current_time_t, start_time_t);

        if (elapsed_seconds >= op.duration && strcmp(machine->state, "OP") == 0) {
            strcpy(machine->state, "ON");
            printf("Operação %d concluída na máquina %d. Estado alterado para ON.\n",
                   op.operation_id, machine->identifier);
        }
    }
}


int load_instructions(Operation **operations) {
    const char *filename = "data/instructions.csv";
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        perror("Erro ao abrir o arquivo CSV de instruções");
        return 0;
    }

    char line[256];
    int count = 0;
    int capacity = 10;

    *operations = malloc(capacity * sizeof(Operation));
    if (*operations == NULL) {
        perror("Erro ao alocar memória para operações");
        fclose(file);
        return 0;
    }

    fgets(line, sizeof(line), file);

    while (fgets(line, sizeof(line), file)) {
        if (count >= capacity) {
            capacity *= 2;
            Operation *temp = realloc(*operations, capacity * sizeof(Operation));
            if (temp == NULL) {
                perror("Erro ao realocar memória para operações");
                free(*operations);
                fclose(file);
                return 0;
            }
            *operations = temp;
        }

        line[strcspn(line, "\n")] = '\0';

        char *token = strtok(line, ",");
        if (token == NULL) continue;
        (*operations)[count].operation_id = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        (*operations)[count].machine_id = atoi(token);

        token = strtok(NULL, ",");
        if (token == NULL) continue;
        strncpy((*operations)[count].operation_name, token, sizeof((*operations)[count].operation_name) - 1);
        (*operations)[count].operation_name[sizeof((*operations)[count].operation_name) - 1] = '\0';

        token = strtok(NULL, ",");
        if (token != NULL) {
            (*operations)[count].duration = atoi(token);
        } else {
            (*operations)[count].duration = 0;
        }

        count++;
    }

    fclose(file);
    return count;
}


void carregar_e_processar_instrucoes(PlantFloorManager *plantManager) {
    Operation *operations = NULL;
    int operation_count = load_instructions(&operations);

    if (operation_count == 0) {
        printf("Nenhuma instrução foi carregada.\n");
        return;
    }

    printf("Processando %d instruções...\n", operation_count);

    for (int i = 0; i < operation_count - 1; i++) {
        for (int j = 0; j < operation_count - i - 1; j++) {
            if (operations[j].operation_id > operations[j + 1].operation_id) {
                Operation temp = operations[j];
                operations[j] = operations[j + 1];
                operations[j + 1] = temp;
            }
        }
    }

    for (int i = 0; i < operation_count; i++) {
        int machine_id = operations[i].machine_id;
        Machine *target_machine = NULL;

        for (int j = 0; j < plantManager->machine_count; j++) {
            if (plantManager->machines[j].identifier == machine_id) {
                target_machine = &plantManager->machines[j];
                break;
            }
        }

        if (target_machine == NULL) {
            printf("Máquina %d não encontrada. Operação %d ignorada.\n",
                   machine_id, operations[i].operation_id);
            continue;
        }

        if (strcmp(target_machine->state, "OP") == 0) {
            printf("Máquina %d está ocupada. Operação %d ignorada.\n",
                   machine_id, operations[i].operation_id);
            continue;
        }

        strcpy(target_machine->state, "OP");

        char current_time[20];
        get_current_time(current_time, sizeof(current_time));
        strncpy(operations[i].start_time, current_time, sizeof(operations[i].start_time) - 1);
        operations[i].start_time[sizeof(operations[i].start_time) - 1] = '\0';

        log_operation(operations[i], plantManager);

        printf("Operação %d iniciada na máquina %d às %s\n",
               operations[i].operation_id, machine_id, current_time);

        sleep(operations[i].duration);

        get_current_time(current_time, sizeof(current_time));
        printf("Operação %d concluída na máquina %d às %s. Estado alterado para ON.\n",
               operations[i].operation_id, machine_id, current_time);

        strcpy(target_machine->state, "ON");
    }

    free(operations);
}






