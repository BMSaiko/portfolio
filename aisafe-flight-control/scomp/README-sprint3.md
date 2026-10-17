# SCOMP — Sprint 3
## Integrative Project of the 4th Semester — LEI, ISEP 2025/2026

---

## 1. Component Diagram

```
+-----------------------------------------------------------------+
|                       PARENT PROCESS                             |
|                      (simulation.c)                              |
|                                                                   |
|  load_config()  validate_params()  setup_signals()  -- US100     |
|                                                                   |
|  create_shared_memory()  -- US105                                |
|    shm_open / ftruncate / mmap  ->  SimSharedData *shm           |
|                                                                   |
|  pthread_create(detection_thread)  -- US106                      |
|  pthread_create(report_thread)     -- US107 / US111              |
|                                                                   |
|  launch_flights()  -- US105                                       |
|    init shm->flights[i]                                          |
|    create_step_semaphores() x N  -- US108                        |
|    fork() x N                                                     |
|                                                                   |
|  run_simulation()  -- US108                                       |
|    sem_post(step_go) / sem_timedwait(step_done)                  |
|    read(pipe) -> PositionUpdate -- US101                          |
|    cond_signal(detection_thread) / cond_wait(done)  -- US106     |
|                                                                   |
|  destroy_shared_memory()  -- US105                                |
|  fork() -- final report process (US109, legacy)                  |
+----+------------------+------------------+-----------------------+
     |                  |                  |
  THREAD:            THREAD:           SHARED MEMORY
  detection          report            /aisafe_sim
  (US106)            (US107/US111)     SimSharedData {
  check_safety_      ViolationNotifier   FlightSlot flights[N]
  violations()       queue + cond        current_step
                      write_report_       simulation_done
                      file()              violation_pending
     |                                  }
     | notify_violation()
     +------------------>  report thread queue
     |
     v
+----+--------+ +-----------+ +-----------+
| CHILD       | | CHILD     | | CHILD     |
| Voo AA123   | | Voo AA124 | | Voo AAxxx |
| flight_     | | flight_   | | flight_   |
| process_run | | process_  | | process_  |
+-------------+ | run       | | run       |
   |    |       +-----------+ +-----------+
 pipe  2 named semaphores per flight:
 (filho  /aisafe_{pid}_{i}_go    (US108)
 ->pai)  /aisafe_{pid}_{i}_done  (US108)
   |
 SIGUSR1 / SIGTERM  (US102 / US100, por kill() em handle_violation)
```

**Ficheiros produzidos em runtime:**
```
violations.log                              # registo de violacoes (US102)
simulation_report_us111_YYYYMMDD_HHMMSS.txt # relatorio em tempo real (US107/US111, via thread)
simulation_report_YYYYMMDD_HHMMSS.txt       # relatorio final legado (US109, via processo fork)
```

**Ficheiros de configuração:**
```
config/opo_mad.cfg          # 2 voos, rota completa, sem colisoes
config/opo_mad_stress.cfg   # 8 voos, offsets sobrepostos -- violacoes e terminacoes
```

---

## 2. Approach per User Story

### US105 — Initialize hybrid simulation environment with shared memory

Esta US fornece o **andaime** sobre o qual todas as outras US do Sprint 3 assentam. Define `shared_memory.h` (struct `SimSharedData` com um array de `FlightSlot` por voo e estado global da simulação) e implementa, em `simulation.c`:

- `create_shared_memory` / `destroy_shared_memory` — `shm_open(O_CREAT|O_EXCL)` → `ftruncate` → `mmap(MAP_SHARED)` → `close(fd)` imediato (o mapeamento é independente do descritor); cleanup com `munmap` + `shm_unlink`. `shm_unlink` preventivo no início remove segmentos órfãos de execuções anteriores.
- Inicialização do mutex, condition variable e `ViolationNotifier` (US107) usados pelos threads.
- Criação dos dois threads do processo pai (`pthread_create` para o detection thread US106 e o report thread US107/US111), feita **antes** do `fork` dos filhos para que estejam prontos a processar eventos desde o primeiro step.
- `launch_flights` — inicializa o slot `shm->flights[i]` (`flight_id`, `active`, `flight_index`, `violation_count`) **antes** de cada `fork`, garantindo que o filho nunca lê um slot em estado indefinido.
- Orquestração geral do `main`: sequência de arranque (config → shm → threads → fork → loop) e sequência de shutdown (sinalizar fim aos threads → `pthread_join` → `waitpid` dos filhos → cleanup de semáforos e shm).

Decisões técnicas principais:
- Shared memory como mecanismo central de estado partilhado entre processo pai e filhos (substitui o estado que no Sprint 2 vivia apenas em `fleet[]` no pai).
- Threads no processo pai em vez de mais processos com `fork` — permitem acesso direto ao array `fleet[]` sem IPC adicional.
- Ordem de shutdown estrita (threads → filhos → recursos IPC) para evitar use-after-free/unlink.

### US106 — Dedicated safety violation detection thread

O thread `safety_violation_detector` corre em loop bloqueado por condition variables (`g_detect_cond_start` / `g_detect_cond_done`, protegidas por `g_detect_mtx`). Após cada step, `run_simulation` (US108) sinaliza `g_detect_cond_start`; o thread acorda, chama `check_safety_violations` sobre `g_detect_fleet`, e sinaliza `g_detect_cond_done` para o loop principal poder avançar. Termina quando `g_detect_sim_finished` é definido e não há scan pendente.

### US107 — Notify report thread via condition variable upon violation

`ViolationNotifier` é uma fila circular (`VIOLATION_QUEUE_SIZE`) protegida por mutex + condition variable próprios. Quando `handle_violation` (chamado dentro do US106) deteta uma violação, chama `notify_violation`, que enfileira um `ViolationEvent` (posições, velocidades, índices dos voos, distância) e faz `pthread_cond_signal`. O report thread está bloqueado em `pthread_cond_wait` num `while (queue_count == 0 && sim_running)` — padrão correto contra spurious wakeups.

### US108 — Step-by-step synchronization with per-flight semaphores

Cada voo tem dois semáforos POSIX named, criados em `create_step_semaphores` (US105) com nomes dinâmicos `/aisafe_{pid}_{i}_go` e `/aisafe_{pid}_{i}_done`, ambos com valor inicial 0:

- `step_go` — o pai faz `sem_post` para autorizar o filho a avançar; o filho faz `sem_wait` em `wait_for_step_signal`.
- `step_done` — o filho faz `sem_post` ao terminar o step; o pai faz `sem_timedwait` (timeout 10s) em `wait_for_step_done`.

Semáforos individuais por voo (em vez de uma barreira global) permitem que o pai simplesmente ignore voos terminados (`active=0`) sem afetar a contagem de outros voos. O pipe (herdado do Sprint 2) mantém-se para transportar o `PositionUpdate` — os semáforos sincronizam o *quando*, o pipe transporta o *quê*.

### US109 — Generate and store final simulation report (legado, processo)

Mantido do Sprint 2: após `run_simulation`, `main` faz `fork` de um processo dedicado que chama `run_report_process`, agregando `fleet[]` e `violations.log` num ficheiro `simulation_report_YYYYMMDD_HHMMSS.txt`.

### US111 — Real-time report thread

`report_thread_main`, executado pelo thread criado na US105, consome a fila do `ViolationNotifier` em tempo real (sem polling — bloqueado em `pthread_cond_wait`), acumula os eventos num array dinâmico (`realloc`), e no fim (`notifier_signal_finished`) escreve `simulation_report_us111_YYYYMMDD_HHMMSS.txt` com o detalhe de cada violação (posições, velocidades, distância) e o resultado de validação (PASS/FAIL).

Nota: o Sprint 3 produz **dois** relatórios — o legado por processo (US109) e o novo por thread (US111) — ambos coexistem.

---

## 3. What was and was not successfully implemented

| US | Description | Status | Notes |
|---|---|---|---|
| US105 | Initialize hybrid simulation environment with shared memory | Done | shm + semáforos + threads + mutex/cond inicializados e limpos corretamente |
| US106 | Dedicated safety violation detection thread | Done | Sincronizado com o loop principal via condition variables |
| US107 | Notify report thread via condition variable | Done | `ViolationNotifier` com fila circular, padrão `while` contra spurious wakeups |
| US108 | Step-by-step synchronization with semaphores | Done | Semáforos individuais por voo, `sem_timedwait` com timeout de 10s |
| US109 | Final simulation report (processo) | Done | Mantido do Sprint 2, sem alterações funcionais |
| US111 | Real-time report thread | Done | Relatório separado, com detalhe de velocidades por violação |

**Limitações conhecidas:**
- Dois relatórios são gerados (US109 por processo e US111 por thread) em vez de um único relatório consolidado — redundância aceite por resultar da evolução incremental das duas abordagens.
- A física de voo mantém-se por interpolação linear entre waypoints, sem as fórmulas da secção 3.3 do enunciado.

---

## 4. How to build and run

```bash
# Compilar
make clean && make

# Executar com ficheiro de configuracao
./air_sim config/opo_mad.cfg                # 2 voos, sem violacoes
./air_sim config/opo_mad_stress.cfg         # 8 voos, violacoes e terminacoes

# Verificar shared memory e semaforos durante a execucao (segundo terminal)
ls /dev/shm | grep aisafe

# Verificar ausencia de zombies
ps aux | grep air_sim

# Consultar relatorios gerados
cat simulation_report_*.txt
cat simulation_report_us111_*.txt
```

**Output esperado (excerto):**
```
[US105] Voo AA100 iniciado -- PID 8004
[US108] === Time step 0 ===
[US108] Parent released flight AA100 for step 0
[US101] AA100 position stored: lat=41.3219 lon=-8.6312 alt=803 history=1
[US108] AA100 completed step 0 -> lat=41.3219 lon=-8.6312 alt=803
[US106] Safety violation detection thread started
[US102] Flight process received SIGUSR1: safety violation detected
[US107] Violation: idx=0 <-> idx=3 (0.00 km) -> report notified
[US108] Voo AA100 terminou execucao -- PID 8004
[US111] Report saved to simulation_report_us111_20260606_151439.txt
[US109] Report saved to simulation_report_20260606_151439.txt
[US105] Simulacao concluida.
```

---

## 5. Self-assessment

| Student                 | US                | Contribution (%) |
|---|-------------------|---|
| 1240895 - Diogo Azevedo | US105             | 23.3% |
| 1231635 - João Amorim   | US106      | 23.3% |
| 1221514 - Bruno Silva   | US107, US109/US111 | 30% |
| 1220708 - David Ribeiro | US108             | 23.3% |

**Notes:**
- US105 serve de base para todas as restantes US do Sprint 3 — a shared memory, os threads e os semáforos criados nesta US são reutilizados por US106, US107 e US108.
- US108 evoluiu a sincronização do Sprint 2 (pipes + `select()`) para semáforos POSIX named individuais por voo, integrando-se com a shared memory introduzida na US105.
- US106 e US107 introduzem o modelo de threads no processo pai, substituindo a deteção de violações sequencial do Sprint 2 por um thread dedicado notificado via condition variable.
- US111 é uma extensão da US109 (relatório), implementada como thread em tempo real conforme indicado pelo docente para o Sprint 3.