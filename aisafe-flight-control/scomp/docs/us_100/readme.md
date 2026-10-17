# US100 - Simulate Flights in an Area

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the SCOMP course unit.

The goal is to implement a flight simulation system where each flight is modelled as an
independent OS process, communicating with the parent process through anonymous pipes and
reacting to events via POSIX signals.

This implementation serves as the foundation for the remaining SCOMP user stories in
Sprint 2 (US101, US102, US103), which extend the simulation with position tracking, safety
violation detection, and time-step synchronisation.

---

## 1.1 List of Issues

- **Analysis:** Understand the simulation requirements, including geographic area
  constraints, temporal range, flight plan structure, and safety thresholds.
- **Design:** Define a multi-process architecture using `fork()`, anonymous pipes, and
  POSIX signals in C.
- **Implement:** Implement parameter validation, process creation per flight,
  inter-process communication via pipes, and signal handling for termination.
- **Test:** Validate process creation, pipe communication, signal delivery, zombie-free
  termination, and parameter error handling.

---

## 2. Requirements

**US100** As a Flight Control Operator, I want to simulate the flights occurring in a
given area, during a given period of time.

### Acceptance Criteria

- **US100.1:** The system must accept a geographic area (latitude/longitude bounds), a
  temporal range, and a list of flights as input parameters.
- **US100.2:** All input parameters must be validated before any process is created.
- **US100.3:** Each flight in the simulation must run as an independent child process
  created via `fork()`.
- **US100.4:** The parent process must maintain the state (current position, history) of
  all active flights.
- **US100.5:** The simulation must advance in discrete time steps, with each step defined
  by `time_step_secs`.
- **US100.6:** The parent process must cleanly terminate all child processes and avoid
  zombie processes upon simulation end.
- **US100.7:** The implementation must be in C, following POSIX standards, using
  `sigaction()` for all signal handling.

### Dependencies / References

- This US is a prerequisite for US101, US102, and US103.
- NFR01 states the SCOMP component must be implemented in C using POSIX APIs.
- Related US: US101 (position tracking), US102 (safety violations), US103 (time-step
  synchronisation).

---

## 3. Analysis

### 3.1 Understanding

The simulation represents a controlled airspace area defined by geographic bounds and a
time window. Each flight in the flight plan is assigned an independent OS process. The
parent process acts as the Flight Control Operator, orchestrating all flights and
maintaining their state.

Communication between the parent and each child is done exclusively through anonymous
pipes:

- **Child to Parent pipe:** the child sends a `PositionUpdate` struct at each time step.
- **Parent to Child pipe:** the parent sends a `GO` signal (integer) to authorise the
  child to advance to the next step.

Signal handling follows the safe pattern taught in SCOMP lectures (TP3/TP4):

- Handlers only set `volatile sig_atomic_t` flags or call `write()`.
- All substantial work is deferred to the main execution loop.
- `sigaction()` is used exclusively -- `signal()` is never used.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| One process per flight | Directly models independent flight execution; required by the project specification. |
| Two pipes per child | One for each direction of communication; avoids read/write conflicts. |
| `sigaction()` with `SA_SIGINFO` | Full POSIX compliance; allows access to `siginfo_t` for sender PID if needed. |
| `volatile sig_atomic_t` flags | Guarantees atomic reads/writes from signal handlers; prevents compiler optimisation issues. |
| `write()` only inside handlers | `printf()` is not async-signal-safe; `write()` is listed in `signal-safety(7)`. |
| `sigfillset` + `sigdelset(SIGTERM)` in child | Blocks all signals during `SIGUSR1` handler execution except `SIGTERM`, preventing nested handler issues. |
| `waitpid(-1, WNOHANG)` in `SIGCHLD` handler | Reaps children without blocking the parent simulation loop. |
| Blocking `wait(NULL)` at simulation end | Guarantees all children are reaped before the parent exits; prevents zombies. |
| Parameter validation before `fork()` | Avoids creating processes with invalid state; fails fast and cleanly. |
| Placeholder comments for US101, US102, US103 | Clearly marks the integration points for the responsible colleagues without coupling implementations. |

---

## 4. Design

### 4.1 Realization

The parent process (`simulation.c`) validates parameters, sets up signal handlers, and
calls `launch_flights()` to create one child process per flight. Each child runs
`flight_process_run()` from `flight_process.c`.

The simulation loop in `run_simulation()` advances one time step at a time:

1. Send `GO` to all active children via their write pipes *(integration point for US103)*.
2. Read one `PositionUpdate` from each active child via their read pipes *(integration point for US101)*.
3. Update the `AircraftState` array and position history in the parent.
4. Check for safety violations *(placeholder for US102)*.
5. Reap any terminated children using `waitpid()` with `WNOHANG`.

### 4.2 Process Architecture

```
              +----------------------------------+
              |         PARENT PROCESS           |
              |  simulation.c -- main()          |
              |                                  |
              |  validate_params()               |
              |  setup_signals()                 |
              |  launch_flights()                |
              |  run_simulation()                |
              +----+----------+----------+-------+
                   | fork()   | fork()   | fork()
                   v          v          v
            [TP001]      [TP002]      [TPxxx]
          child process  child process  child process
          flight_process_run()
```

Each child communicates with the parent through two dedicated anonymous pipes:

```
  CHILD                              PARENT
    |                                   |
    |-- PositionUpdate ---------------->|  pipe_child_to_parent
    |                                   |
    |<------------------- GO (int) -----|  pipe_parent_to_child
```

### 4.3 Signal Handling

| Signal | Handler | Process | Action |
|---|---|---|---|
| `SIGTERM` | `handle_SIGTERM` | Parent | Sets `g_terminate = 1` |
| `SIGINT` | `handle_SIGTERM` | Parent | Sets `g_terminate = 1` (reuses handler) |
| `SIGCHLD` | `handle_SIGCHLD` | Parent | Sets `g_child_exited = 1` |
| `SIGUSR1` | `handle_SIGUSR1` | Child | Sets `g_violation = 1`; writes alert to stdout |
| `SIGTERM` | `handle_SIGTERM` | Child | Sets `g_stop = 1` |

### 4.4 Termination Flow

```
  g_terminate = 1
        |
        v
  kill(fleet[i].pid, SIGTERM)  <-- for all active children
        |
        v
  wait(NULL)  <-- blocks until all children exit
        |
        v
  "[SIM] Simulacao concluida."
```

---

## 5. Implementation

### 5.1 File Structure

```
scomp/us100/
+-- simulation.h          # Shared types, structs, and constants
+-- simulation.c          # Parent process: main(), setup, simulation loop
+-- flight_process.h      # flight_process_run() interface
+-- flight_process.c      # Child process: position update, signal handling
+-- Makefile              # Build configuration
```

### 5.2 Key Types

#### `SimParams` -- Simulation input parameters

```c
typedef struct {
    char   area_code[16];
    double lat_min, lat_max;
    double lon_min, lon_max;
    int    time_start;
    int    time_end;
    int    time_step_secs;
    int    max_violations;
    int    num_flights;
    char   flight_ids[MAX_FLIGHTS][MAX_FLIGHT_ID];
} SimParams;
```

#### `PositionUpdate` -- Sent from child to parent via pipe at each time step

```c
typedef struct {
    char   flight_id[MAX_FLIGHT_ID];
    double lat, lon, altitude;
    double vx, vy, vz;
    int    time_step;
    int    active;              /* 0 = flight has ended */
} PositionUpdate;
```

#### `AircraftState` -- Maintained by the parent for each active flight

```c
typedef struct {
    char           flight_id[MAX_FLIGHT_ID];
    pid_t          pid;
    int            pipe_read_fd;
    int            pipe_write_fd;
    PositionUpdate current;
    PositionUpdate history[MAX_HISTORY_STEPS];
    int            history_count;
    int            active;
} AircraftState;
```

### 5.3 Signal Setup Pattern

All signal handlers follow the pattern taught in SCOMP lectures (TP3/TP4):

```c
struct sigaction act;
memset(&act, 0, sizeof(act));
sigemptyset(&act.sa_mask);
sigaddset(&act.sa_mask, SIGCHLD);
act.sa_sigaction = handle_SIGTERM;
act.sa_flags     = SA_SIGINFO | SA_RESTART;
sigaction(SIGTERM, &act, NULL);
```

### 5.4 Integration Points for US101, US102, US103

The simulation loop in `run_simulation()` contains three clearly marked placeholder
comments, one per dependent US. Each comment identifies the exact location, the variables
available, and what the responsible colleague is expected to implement.

- **US101** -- Marked at step 2 of the loop (position read block). The colleague can
  extend the history processing logic. Variables available: `fleet[i].current`,
  `fleet[i].history[]`, `fleet[i].history_count`.

- **US102** -- Marked at step 3 of the loop. The colleague implements proximity
  detection, `SIGUSR1` delivery, and threshold-based `SIGTERM`. Variables available:
  `fleet[]`, `n`, `params->max_violations`. The `SIGUSR1` handler in the child is
  already implemented in `flight_process.c`.

- **US103** -- Marked at step 1 of the loop (GO signal block). The colleague can
  extend the synchronisation mechanism, for example adding `select()` with timeout.
  Variables available: `fleet[]`, `n`, `params->time_step_secs`.

---

## 6. Demonstration

Compile and run from the `scomp/us100/` directory:

```bash
make clean && make
./air_sim
```

Expected output (2 flights, 60 time steps of 60 seconds each):

```
[SIM] Voo TP001 iniciado -- PID 96234
[SIM] Voo TP002 iniciado -- PID 96235
[SIM] === Time step 0 ===
[SIM] TP001 -> lat=37.0100 lon=-8.4900 alt=10000
[SIM] TP002 -> lat=37.0100 lon=-8.4900 alt=10000
[SIM] === Time step 60 ===
[SIM] TP001 -> lat=37.0200 lon=-8.4800 alt=10000
[SIM] TP002 -> lat=37.0200 lon=-8.4800 alt=10000
...
[SIM] Voo TP001 terminou (PID 96234)
[SIM] Voo TP002 terminou (PID 96235)
[SIM] Simulacao concluida.
```

To test signal delivery manually (requires a second terminal):

```bash
# Send SIGUSR1 to a child process
kill -SIGUSR1 <PID_do_filho>

# Terminate the simulation early
kill -SIGTERM <PID_do_pai>
```

To verify no zombie processes exist during execution:

```bash
ps aux | grep air_sim
# All processes must show state S+ (sleeping), never Z (zombie)
```

---

## 7. Tests

All tests were executed on the target Linux virtual machine (antiX, gcc with
`-Wall -Wextra -pedantic`).

| Test | Method | Expected Result | Result |
|---|---|---|---|
| Clean compilation | `make clean && make` | Zero errors, zero warnings | Passed |
| Normal execution | `./air_sim` | 60 steps, both flights active, clean exit | Passed |
| Correct process count | `ps aux` during run | 3 processes (1 parent + 2 children), all state `S+` | Passed |
| No zombie processes | `ps aux` during run | No process with state `Z` | Passed |
| SIGINT clean termination | Ctrl+C during run | Children terminated, `Simulacao concluida.` printed | Passed |
| Child pipe communication | Inspect output | Each child sends one `PositionUpdate` per step | Passed |
| History accumulation | Code inspection | `fleet[i].history[]` grows by 1 per step per flight | Passed |
| Parameter validation | Edit `main()`, set `num_flights=0` | `[ERROR] num_flights invalido: 0`, `EXIT_FAILURE` | Passed |

---

## 8. Observations

The current implementation uses hardcoded parameters in `main()` for demonstration
purposes. In the final version, these will be read from a configuration file or
command-line arguments, aligned with the flight plan DSL produced by LPROG.

The `update_position()` function in `flight_process.c` currently applies a fixed delta to
latitude and longitude. In a more complete implementation this function will receive the
actual flight plan waypoints and apply the physics formulas from section 3.3 of the
project specification (lift, drag, thrust, fuel consumption).