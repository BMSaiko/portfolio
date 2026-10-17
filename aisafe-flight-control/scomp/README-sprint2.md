# SCOMP — Sprint 2
## Integrative Project of the 4th Semester — LEI, ISEP 2025/2026

---

## 1. Component Diagram

```
+----------------------------------------------------------+
|                    PARENT PROCESS                        |
|                   (simulation.c)                         |
|                                                          |
|  load_config()  validate_params()  setup_signals()       |
|                                                          |
|  launch_flights()                                        |
|    fork() x N                                            |
|                                                          |
|  run_simulation()                                        |
|    select() -- US103                                     |
|    process_position_update() -- US101                    |
|    check_safety_violations() -- US102                    |
|    waitpid(WNOHANG)                                      |
|                                                          |
|  fork() -- report process (US109)                        |
|    run_report_process()                                  |
|    waitpid(report_pid)                                   |
+--------+-----------+-----------+------------------------+
         |           |           |
    pipe_w      pipe_w      pipe_w
    pipe_r      pipe_r      pipe_r
         |           |           |
+--------+--+ +------+----+ +----+------+
| CHILD     | | CHILD     | | CHILD     |
| Voo AA123 | | Voo AA124 | | Voo AA125 |
| (flight_  | | (flight_  | | (flight_  |
|  process) | |  process) | |  process) |
+-----------+ +-----------+ +-----------+
    |               |               |
 SIGUSR1         SIGUSR1         SIGUSR1   (US102 -- violacao)
 SIGTERM         SIGTERM         SIGTERM   (US100 -- terminacao)
```

**Ficheiros produzidos em runtime:**
```
violations.log                          # registo de violacoes (US102)
simulation_report_YYYYMMDD_HHMMSS.txt  # relatorio final (US109)
```

**Ficheiros de configuracao:**
```
config/opo_mad.cfg                # 2 voos, rota completa, sem colisoes
config/single_flight.cfg          # 1 voo isolado
config/invalid_params.cfg         # parametros invalidos -- testa validacao
config/collisions.cfg             # colisoes imediatas -- demonstra US102
config/progressive_collisions.cfg # 3 voos, colisoes progressivas
```

---

## 2. Approach per User Story

### US100 — Simulate flights in a given area

O processo pai valida os parâmetros lidos de um ficheiro de configuração (`.cfg`),
instala os handlers de sinais com `sigaction()` e cria um processo filho por cada
voo com `fork()`. Cada filho recebe dois pipes anónimos — um para enviar
`PositionUpdate` ao pai e outro para receber o sinal de avanço de step (GO).
O pai mantém o estado de todos os voos no array `fleet[]` (posição actual e
histórico). A terminação é limpa — filhos activos recebem `SIGTERM`, o pai
aguarda com `wait(NULL)`.

Decisões técnicas principais:
- Dois pipes por voo (um por direcção) para evitar condições de corrida
- `sigaction()` com `SA_SIGINFO | SA_RESTART` em todos os handlers
- `volatile sig_atomic_t` para todas as flags de sinais
- Handlers apenas fazem `flag = 1` ou `write()` — padrão async-signal-safe
- Parâmetros lidos de ficheiro `.cfg` (chave=valor) para múltiplos cenários sem recompilar
- `max_altitude` configurável por exigência do PO

### US101 — Capture and process flight movements

Cada filho envia uma `PositionUpdate` por pipe a cada time step. O pai lê,
guarda em `fleet[i].current` e acumula em `fleet[i].history[]` (até
`MAX_HISTORY_STEPS = 256`). Se o buffer encher, descarta a posição mais
antiga. Calcula também uma previsão linear da próxima posição com base nos
dois últimos pontos e verifica se a posição prevista sai da área simulada.

### US102 — Detect aircraft safety violations in real time

Após processar todos os updates de um time step, o pai verifica todos os pares
de voos activos calculando a distância Haversine entre as posições actuais. Se
a distância for inferior ao `safety_threshold` (configurável no `.cfg`), envia
`SIGUSR1` a ambos os processos, regista a violação em `violations.log` e
incrementa `violation_count`. Ao atingir 3 violações, envia `SIGTERM` ao voo
em questão. O filho ao receber `SIGUSR1` bloqueia todos os sinais excepto
`SIGTERM` durante o handler (padrão TP4 — `sigfillset` + `sigdelset`).

### US103 — Synchronize flight execution with a time step

O pai usa `select()` com timeout de 2 segundos para aguardar as respostas de
todos os filhos activos antes de avançar ao step seguinte. Se um filho não
responder dentro do timeout, recebe `SIGTERM` e é removido da simulação. Isto
garante que todos os processos avançam em lockstep e que um filho bloqueado
não paralisa a simulação inteira.

### US109 — Generate and store final simulation report

Após a conclusão da simulação, o processo pai cria um processo filho dedicado
via `fork()` para gerar o relatório. O processo de relatório agrega os dados
do array `fleet[]` (estado final de cada voo, histórico de steps, contagem de
violações, posição final) e o conteúdo de `violations.log`, determina o
resultado de validação (PASS se zero violações, FAIL caso contrário) e escreve
um ficheiro `simulation_report_YYYYMMDD_HHMMSS.txt`. O processo pai aguarda
a conclusão com `waitpid()`.

Nota: por indicação do docente, no Sprint 2 a US109 é implementada com um
processo em vez de uma thread — a versão com threads POSIX será desenvolvida
no Sprint 3.

---

## 3. What was and was not successfully implemented

| US | Description | Status | Notes |
|---|---|---|---|
| US100 | Simulate flights in a given area | Done | Processos, pipes, sinais, validação, config file |
| US101 | Capture and process flight movements | Done | Histórico, previsão, detecção fora de área |
| US102 | Detect aircraft safety violations | Done | Haversine, SIGUSR1, threshold, violations.log |
| US103 | Synchronize flight execution with time step | Done | select() com timeout de 2s |
| US109 | Generate and store final simulation report | Done | Processo fork(), relatório com timestamp |

**Limitações conhecidas:**
- O plano de voo está embutido em C com valores do JSON de exemplo do cliente. A integração com o DSL do LPROG (US081) está prevista para sprint futuro.
- A física de voo usa interpolação linear entre waypoints em vez das fórmulas da secção 3.3 do enunciado.
- O `active_count` pode divergir do estado real quando `handle_violation()` marca um voo como inactivo sem decrementar o contador -- corrigido com a verificação `if (fleet[i].active)` no bloco SIGCHLD.

---

## 4. How to build and run

```bash
# Compilar
make clean && make

# Executar com ficheiro de configuracao
./air_sim config/opo_mad.cfg

# Cenarios disponiveis
./air_sim config/single_flight.cfg           # 1 voo, rota completa OPO->MAD
./air_sim config/opo_mad.cfg                 # 2 voos paralelos, sem colisoes
./air_sim config/collisions.cfg              # colisoes imediatas, SIGUSR1
./air_sim config/progressive_collisions.cfg  # 3 voos, colisoes progressivas
./air_sim config/invalid_params.cfg          # validacao de parametros invalidos

# Verificar ausencia de zombies (em segundo terminal durante execucao)
ps aux | grep air_sim

# Consultar relatorio gerado
cat simulation_report_*.txt
```

**Output esperado:**
```
[US100] Voo AA123 iniciado -- PID 12345
[US103] === Time step 0 ===
[US101] AA123 position stored: lat=41.3219 lon=-8.6312 alt=803 history=1
[US103] AA123 -> lat=41.3219 lon=-8.6312 alt=803
...
[US109] Report saved to simulation_report_20260517_160000.txt
[US100] Simulacao concluida.
```

---

## 5. Self-assessment

| Student                 | US           | Contribution (%) |
|---|--------------|---|
| 1240895 - Diogo Azevedo | US100, US109 | 25% |
| 1231635 - João Amorim   | US101, US109 | 25% |
| 1221514 - Bruno Silva   | US102, US109 | 25% |
| 1220708 - David Ribeiro | US103, US109 | 25% |

**Notes:**
- US100 serves as the foundation for all other SCOMP user stories in this sprint.
  The fork/pipe/signal infrastructure and the config file loader were developed
  as part of US100 and reused by all colleagues.
- US109 was implemented using a dedicated child process (fork) per instructor
  guidance. Thread-based implementation is planned for Sprint 3.