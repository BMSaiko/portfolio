/**
 * @file machine_emulator.h
 * @brief Header do emulador de máquina para testes
 */

#ifndef MACHINE_EMULATOR_H
#define MACHINE_EMULATOR_H

/**
 * Simula uma máquina respondendo a comandos
 * @param cmd Comando a ser processado
 * @return Resposta simulada da máquina
 */
char* simulate_machine(const char* cmd);

#endif // MACHINE_EMULATOR_H 