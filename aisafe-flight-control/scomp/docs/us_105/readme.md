# US105 - Initialize Hybrid Simulation Environment with Shared Memory

## 1. Context

This user story belongs to Sprint 3 and is a mandatory requirement for the SCOMP course unit.

The goal is to evolve the simulation architecture from Sprint 2 (pipes-based, US100–US103)
into a hybrid model where the parent process coordinates child flight processes through a
shared memory segment, and exposes the synchronisation infrastructure required by the
remaining Sprint 3 user stories (US106, US107, US108, US109).

---

## 1.1 List of Issues

- **Analysis:** Understand the architectural shift from pipes to shared memory, identify
  the data contract between the parent threads and child processes, and define the
  synchronisation primitives required by dependent user stories.
- **Design:** Define the shared memory layout (`SimSharedData`), the POSIX named semaphore
  set, the mutex/condition variable pair, and the thread creation interface.
- **Implement:** Implement shared memory allocation and initialisation, semaphore creation,
  mutex and condition variable initialisation, thread creation with placeholder functions,
  and child process launch with `fork()`.
- **Test:** Validate shared memory creation and cleanup, semaphore initialisation,
  correct thread creation, child process launch, and resource-leak-free termination.

---

## 2. Requirements

**US105** As a Flight Control Operator, I want to start the simulation with a
multi-threaded parent process and multiple child flight processes communicating through a
shared memory area, so that the system efficiently coordinates simulation data across
processes.

### Acceptance Criteria

- **US105.1:** The parent process spawns dedicated threads for its functionalities.
- **US105.2:** Each flight is launched as an independent process via `fork()`.
- **US105.3:** A shared memory segment is allocated and properly initialised for
  inter-process communication.
- **US105.4:** Flight processes are configured to use semaphores for synchronisation.
- **US105.5:** This component must be implemented in C and must utilise threads, mutexes,
  condition variables, and signals.

### Dependencies / References

- Depends on US100 (process architecture and parameter validation) from Sprint 2.
- This US is a prerequisite for US106 (dedicated threads), US107 (condition variable
  notification), US108 (step synchronisation), and US109 (final report).
- NFR01 states the SCOMP component must be implemented in C using POSIX APIs.

---

## 3. Analysis

### 3.1 Understanding

Sprint 2 used anonymous pipes as the IPC mechanism between the parent and each child
flight process. This required two file descriptors per child and a `select()`-based loop
in the parent. The parent was single-threaded and performed all work sequentially:
send GO, read updates, check violations.

Sprint 3 introduces a hybrid model:

- **Shared memory** replaces pipes as the data exchange mechanism. Each child writes its
  position into a dedicated slot in a shared `SimSharedData` structure. The parent reads
  all slots without any pipe overhead.
- **POSIX named semaphores** replace the GO/response pipe protocol. Two semaphores
  (`SEM_STEP_GO` and `SEM_STEP_DONE`) implement a step barrier; a third
  (`SEM_SHM_MUTEX`) protects concurrent writes to the shared memory.
- **POSIX threads** replace sequential processing in the parent. Two dedicated threads
  (safety violation detection and report generation) run concurrently with the simulation
  loop. A `pthread_mutex_t` and a `pthread_cond_t` coordinate them internally.

The US105 responsibility is strictly the initialisation of this environment: allocating
the shared memory, opening the semaphores, initialising the mutex and condition variable,
creating the threads, and launching the child processes. The behaviour of the threads
themselves is defined by US106 and US107.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| POSIX named semaphores | Must be visible across unrelated processes (parent and forked children); named semaphores survive `fork()` whereas unnamed ones in non-shared memory do not. |
| `shm_unlink` before `shm_open` | Prevents stale segments from a previous crashed execution from blocking initialisation. |
| `sem_unlink` before `sem_open` | Same rationale as above; ensures a clean state at startup. |
| `close(fd)` immediately after `mmap` | The file descriptor is no longer needed once the mapping is established; keeping it open wastes resources. |
| `memset` on shared memory after `mmap` | Guarantees all fields are zero-initialised regardless of prior content. |
| Mutex and condition variable initialised in `main` | Both are owned by the parent and passed by pointer to the threads; lifetime is guaranteed to exceed the threads. |
| Placeholder thread functions | Allow the US105 code to compile and run independently while colleagues implement US106/US107. |
| `pthread_cond_broadcast` on `simulation_done` | Ensures both threads wake up regardless of which one is waiting when the simulation ends. |
| `destroy_shared_memory` called after `wait(NULL)` | All child processes must have detached before the segment is unlinked; avoids use-after-unlink in children. |

---

## 4. Design

### 4.1 Realization

The parent process (`simulation.c`) validates parameters, sets up signal handlers,
allocates the shared memory segment, opens the three POSIX named semaphores, initialises
the mutex and condition variable, creates two threads (detection and report), and calls
`launch_flights()` to fork one child process per flight.

The simulation loop in `run_simulation()` advances one time step at a time, updating
`shm->current_step` under mutex protection, posting to `SEM_STEP_GO` to unblock all
active children, and collecting `SEM_STEP_DONE` posts before advancing. When the loop
ends, `shm->simulation_done` is set to 1 and `pthread_cond_broadcast` is issued so that
the threads can exit cleanly.

### 4.2 Architecture

```
                    +---------------------------------------------+
                    |              PARENT PROCESS                  |
                    |  simulation.c -- main()                      |
                    |                                              |
                    |  create_shared_memory()                      |
                    |  open_semaphores()                           |
                    |  pthread_mutex_init / pthread_cond_init      |
                    |  pthread_create(detection_thread)  [US106]   |
                    |  pthread_create(report_thread)     [US106]   |
                    |  launch_flights()                            |
                    |  run_simulation()                            |
                    |  pthread_join x2                             |
                    |  close_semaphores()                          |
                    |  destroy_shared_memory()                     |
                    +----+----------+----------+-------------------+
                         | fork()   | fork()   | fork()
                         v          v          v
                    [TP001]      [TP002]      [TPxxx]
                  child process  child process  child process
                  flight_process_run()
```

### 4.3 Shared Memory Layout

The shared memory segment contains one `FlightSlot` per flight, plus global simulation
state fields. The layout is defined in `shared_memory.h` and shared between the parent
and all child processes.

```
SimSharedData
+------------------+
| flights[0]       |  FlightSlot: flight_id, lat, lon, altitude,
| flights[1]       |              vx, vy, vz, time_step,
| ...              |              active, violation_count, flight_index
| flights[N-1]     |
+------------------+
| num_flights      |
| current_step     |
| simulation_done  |
| violation_pending|
| total_violations |
+------------------+
```

### 4.4 Synchronisation Infrastructure

```
  PARENT (run_simulation)              CHILD (flight_process_run)
        |                                        |
        | sem_post(SEM_STEP_GO) x active         |
        |--------------------------------------->| sem_wait(SEM_STEP_GO)
        |                                        | [update position in shm]
        | sem_wait(SEM_STEP_DONE) x active       |
        |<---------------------------------------| sem_post(SEM_STEP_DONE)
        |                                        |
```

```
  detection_thread                    report_thread
        |                                   |
        | pthread_mutex_lock                |
        | [scan shm for violations]         | pthread_mutex_lock
        | pthread_cond_signal               | while(!violation_pending)
        | pthread_mutex_unlock              |   pthread_cond_wait
        |                                   | [process violation]
        |                                   | pthread_mutex_unlock
```

### 4.5 Signal Handling

Unchanged from US100. All handlers set `volatile sig_atomic_t` flags only.

| Signal | Handler | Process | Action |
|---|---|---|---|
| `SIGTERM` | `handle_SIGTERM` | Parent | Sets `g_terminate = 1` |
| `SIGINT`  | `handle_SIGTERM` | Parent | Sets `g_terminate = 1` |
| `SIGCHLD` | `handle_SIGCHLD` | Parent | Sets `g_child_exited = 1` |

### 4.6 Termination Flow

```
  run_simulation() ends
        |
        v
  shm->simulation_done = 1
  pthread_cond_broadcast(&cond_violation)
        |
        v
  pthread_join(tid_detection)
  pthread_join(tid_report)
        |
        v
  pthread_cond_destroy / pthread_mutex_destroy
        |
        v
  wait(NULL)  <-- reaps all child processes
        |
        v
  close_semaphores()
  destroy_shared_memory()
        |
        v
  "[US105] Simulacao concluida."
```

---

## 5. Implementation

### 5.1 File Structure

```
scomp/
+-- simulation.h          # Shared types, structs, and constants (unchanged from US100)
+-- shared_memory.h       # SimSharedData, FlightSlot, semaphore name constants (new)
+-- simulation.c          # Parent process: updated main(), bootstrap, simulation loop
+-- flight_process.h      # Updated interface: pipe fds replaced by flight_index
+-- flight_process.c      # Updated child process: uses shared memory (US106 colleague)
+-- detection.h           # DetectionArgs, ReportArgs, thread function declarations (US106 colleague)
+-- detection.c           # detection_thread and report_thread implementations (US106 colleague)
+-- report.h              # Unchanged interface
+-- report.c              # Updated to use shared memory data (US106/US109 colleague)
+-- Makefile              # Updated: added -pthread, shared_memory.h dependencies
```

### 5.2 Key Types

#### `FlightSlot` -- One slot per flight in shared memory

```c
typedef struct {
    char   flight_id[MAX_FLIGHT_ID];
    double lat, lon, altitude;
    double vx, vy, vz;
    int    time_step;
    int    active;
    int    violation_count;
    int    flight_index;
} FlightSlot;
```

#### `SimSharedData` -- The full shared memory segment

```c
typedef struct {
    FlightSlot flights[MAX_FLIGHTS];
    int        num_flights;
    int        current_step;
    int        simulation_done;
    int        violation_pending;
    int        total_violations;
} SimSharedData;
```

#### Semaphore names

```c
#define SHM_NAME      "/aisafe_sim"
#define SEM_STEP_GO   "/aisafe_step_go"
#define SEM_STEP_DONE "/aisafe_step_done"
#define SEM_SHM_MUTEX "/aisafe_shm_mutex"
```

### 5.3 Thread Creation Pattern

Both threads are created in `main()` before `launch_flights()`, ensuring they are active
before any child process writes to the shared memory:

```c
pthread_mutex_init(&mutex, NULL);
pthread_cond_init(&cond_violation, NULL);

pthread_create(&tid_detection, NULL, detection_thread_placeholder, shm);
pthread_create(&tid_report,    NULL, report_thread_placeholder,    shm);
```

Placeholder functions are used until the colleagues implement US106. They loop on
`shm->simulation_done` with a 100ms sleep, consuming no significant CPU.

### 5.4 Integration Points for US106, US107, US108

- **US106** -- `detection_thread_placeholder` and `report_thread_placeholder` in
  `simulation.c` are replaced by `detection_thread` and `report_thread` in `detection.c`.
  The `DetectionArgs` and `ReportArgs` structs (defined in `detection.h`) carry the shared
  memory pointer, simulation parameters, and the mutex/condition variable pointers.

- **US107** -- The `pthread_cond_t cond_violation` and `pthread_mutex_t mutex`
  initialised in `main()` are passed into both thread argument structs. The detection
  thread calls `pthread_cond_signal`; the report thread calls `pthread_cond_wait` inside
  a `while` loop.

- **US108** -- `SEM_STEP_GO` and `SEM_STEP_DONE` are created here with initial value 0.
  The step barrier logic (post N times / wait N times) in `run_simulation()` is the
  integration point; the child-side `sem_wait`/`sem_post` calls are implemented by the
  colleague updating `flight_process.c`.

---

## 6. Demonstration

Compile and run from the `scomp/` directory:

```bash
make clean && make
./air_sim config/opo_mad.cfg
```

Expected output with placeholder threads:

```
[US105] Voo TP001 iniciado -- PID 12301
[US105] Voo TP002 iniciado -- PID 12302
[US105] === Time step 0 ===
[US105] === Time step 60 ===
...
[US105] Simulacao concluida.
```

To verify shared memory was created and cleaned up:

```bash
# During execution
ls /dev/shm | grep aisafe

# After execution (must return nothing)
ls /dev/shm | grep aisafe
```

To verify semaphores were created and cleaned up:

```bash
# During execution
ls /dev/shm | grep aisafe_step

# After execution (must return nothing)
ls /dev/shm | grep aisafe_step
```

To verify no zombie processes:

```bash
ps aux | grep air_sim
# All processes must show state S+, never Z
```

---

## 7. Tests

| Test | Method | Expected Result | Result |
|---|---|---|---|
| Clean compilation | `make clean && make` | Zero errors, zero warnings | Passed |
| Shared memory created | `ls /dev/shm` during run | `/aisafe_sim` visible | Passed |
| Shared memory removed | `ls /dev/shm` after run | `/aisafe_sim` absent | Passed |
| Semaphores created | `ls /dev/shm` during run | Three `/aisafe_*` entries visible | Passed |
| Semaphores removed | `ls /dev/shm` after run | No `/aisafe_*` entries | Passed |
| Threads created | `ps -eLf` during run | Parent PID shows 3 threads (main + detection + report) | Passed |
| Child processes launched | `ps aux` during run | N+1 processes active (1 parent + N flights) | Passed |
| No zombie processes | `ps aux` during run | No process with state `Z` | Passed |
| Stale shm recovery | Run twice without clean exit | Second run initialises cleanly | Passed |
| SIGINT clean termination | Ctrl+C during run | Shared memory and semaphores removed, no leaks | Passed |

---

## 8. Observations

The placeholder thread functions (`detection_thread_placeholder`,
`report_thread_placeholder`) are intentional. They allow the US105 code to be compiled,
executed, and tested independently while the colleagues responsible for US106 and US107
develop their implementations. They will be replaced once `detection.c` is available, by
updating the Makefile to include `detection.o` and replacing the placeholder calls in
`main()` with the real thread functions declared in `detection.h`.

The `flight_process_run` function signature changes from Sprint 2. The `write_fd` and
`read_fd` pipe parameters are removed and replaced by `flight_index`, which is the index
of the flight's slot in the shared memory array. This change is the responsibility of the
colleague updating `flight_process.c` for US108.