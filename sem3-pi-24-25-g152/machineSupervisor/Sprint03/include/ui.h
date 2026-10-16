#ifndef UI_H
#define UI_H
#include "structs.h"

int carregar_configuracao(Machine **machines, int *machine_count);
void display_menu();
void exibir_status_maquinas(PlantFloorManager *plantManager);
void atribuir_operacao(Machine *machines, int machine_count, PlantFloorManager *plantManager);
void gerenciar_maquinas(Machine *existing_machines, int existing_count, Machine **plant_floor_machines, int *plant_count);
void visualizar_historico(PlantFloorManager *plantManager);
int select_machine(const Machine *machines, int machine_count);

#endif //UI_H
