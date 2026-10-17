#ifndef MACHMANAGER_H
#define MACHMANAGER_H
#include "structs.h"

int carregar_configuracao(Machine **machines, int *machine_count);
Machine* load_machine_setup_dynamic(const char *filename, int *count);
void initialize_csv(int machine_id);

void log_operation(Operation op, PlantFloorManager *plantManager);
void process_machine_response(Machine *machine, const char *response);

void add_machine_to_plantfloor_from_existing(
    Machine *existing_machines, int existing_count,
    Machine **plant_floor_machines, int *plant_count, int machine_id
);
void remove_machine_from_plantfloor(
    Machine **plant_floor_machines, int *plant_count, int machine_id
);

void check_operation_status(Machine *machine);

void carregar_e_processar_instrucoes(PlantFloorManager *plantManager);
int load_instructions(Operation **operations);
void process_instruction(const Operation *operation, PlantFloorManager *plantManager);


#endif //MACHMANAGER_H
