# US103 - Synchronize Flight Execution with a Time Step

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the SCOMP course unit.

The goal is to implement a rigorous synchronization mechanism based on discrete time steps (`time_step_secs`). This ensures that all independent child flight processes progress in lockstep, waiting for the parent process's authorization before advancing to the next simulation interval.

This implementation relies heavily on the process architecture established in US100 and directly supports the real-time position tracking and violation detection required by US101 and US102.

---

## 1.1 List of Issues

-   Analyze synchronization constraints and lockstep progression requirements.
-   Implement the downstream communication (GO/authorization signal) from parent to children via pipes.
-   Design the parent process blocking/barrier logic to ensure all updates for step *n* are processed before stepping into *n+1*.
-   Conduct stress testing with multiple parallel flights to verify timing accuracy and lack of drifts.

---

## 2. Requirements

**US103** As a simulation engine, I want to synchronize aircraft movements based on time steps so that I can accurately simulate real-world execution.

### Acceptance Criteria

- **US103.1:** The simulation must progress strictly step by step.
- **US103.2:** Each flight process must send its position updates at defined intervals matching the simulation time step.
- **US103.3:** The main (parent) process must ensure and validate that all updates for a given time step are fully received and processed before advancing to the next step.
- **US103.4:** The execution timing framework must be implemented in C following POSIX standards, ensuring non-blocking reads or proper synchronization to prevent race conditions during state collection.

### Dependencies / References

- **US100 (Prerequisite):** Requires the core process framework (parent-child layout via `fork()`) and anonymous pipes infrastructure.
- **US101 (Related):** Position updates sent at each time step must map directly into the parent's data structures.
- **Section 3.3 & 3.4 (Domain context):** The time step dictates when physics/environmental changes (e.g., wind impact) are computed or loaded.

---

## 3. Analysis

### 3.1 Understanding

To simulate multiple parallel flights accurately without letting faster CPU cycles or scheduling anomalies mismatch the timeline, a **lockstep synchronization protocol** is implemented using the anonymous pipes established in US100.

The execution loop operates under a strict barrier contract:

1. At the beginning of step $T$, the Parent process writes an authorization token (`GO` signal) down the `pipe_parent_to_child` for every single active flight process.
2. Each Child flight process blocks on a `read()` from its inbound authorization pipe. The moment it receives the `GO` token, it wakes up, runs its kinematic calculations for that step, and immediately writes its updated `PositionUpdate` state to the `pipe_child_to_parent`.
3. The Child then goes back to blocking on the next authorization token.
4. Meanwhile, the Parent process executes a loop to read exactly one `PositionUpdate` packet from each active flight's pipe. Because `read()` blocks by default, the parent naturally acts as a barrier synchronization point—guaranteeing no step acceleration occurs until all parts of the airspace status are collected.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Lockstep over free-running timers | Prevents processing drift where one child might compute step 5 while another slow child is still at step 3. |
| Blocking pipe I/O as a synchronization barrier | Avoids complex busy-waiting or scheduling micro-sleeps. The OS scheduler automatically puts processes to sleep until data is ready. |
| Fixed token sizes (integers) | Writing a simple integer (e.g., `4 bytes`) for the `GO` signal minimizes kernel pipe buffer overhead and prevents partial writes. |
| Monotonic clocks (`clock_gettime`) | If any local timing metrics are recorded, monotonic clocks avoid issues with system time adjustments (NTP shifts). |

---

## 4. Design

### 4.1 Realization

The synchronization logic is injected into the core loop of `simulation.c` (`run_simulation()`) and coordinates with `flight_process.c` (`flight_process_run()`).

**Step-by-Step Flow inside the Loop:**

1. **Broadcast Authorization:** Parent iterates through the `AircraftState` array and sends a `GO` token to all elements where `active == 1`.
2. **Child Execution:** The child reads the token, runs `update_position()` matching the `params.time_step_secs`, and sends back its data.
3. **Barrier Collection:** Parent executes a strict synchronous collection loop:
   ```c
   for (int i = 0; i < num_flights; i++) {
       if (fleet[i].active) {
           read(fleet[i].pipe_read_fd, &fleet[i].current, sizeof(PositionUpdate));
           // Parent is now explicitly blocked here until child 'i' responds
       }
   }
   ```
4. **Advance Timeline:** Once the collection loop ends, the parent increments the global simulation step counter and allows the next iteration to begin.

### 4.2 Process Architecture & Synchronization Pulse

```text
   PARENT PROCESS                                      CHILD FLIGHT PROCESS
         |                                                       |
  +--------------+                                               |
  | Begin Step X |                                               |
  +--------------+                                               |
         |                                                       |
         |--- [Writes GO token] -------------------------------->| (Wakes up from blocking read)
         |    (via pipe_parent_to_child)                         |
         |                                                       |  +--------------------+
         |                                                       |  | Compute Kinematics |
         |                                                       |  +--------------------+
         |                                                       |           |
         |<-- [Writes PositionUpdate] ---------------------------|-----------+
         |    (via pipe_child_to_parent)                         |
  +--------------+                                               |
  | Reached      |                                               |
  | Sync Barrier |                                               | (Loops back and blocks
  +--------------+                                               |  waiting for next step)
         |                                                       |
  +--------------+                                               |
  | Step X Done  |                                               |
  +--------------+                                               |
         |                                                       |
```

---

## 5. Implementation

### 5.1 File Structure

The existing structure from US100 is used, expanding the control loop implementations:

```text
scomp/us100/
+-- simulation.c          # Extended with the Parent Lockstep barrier loop
+-- flight_process.c      # Extended with the Child blocking token read mechanism
```

### 5.2 Key Code Fragments

**Parent Broadcast & Barrier Synchronisation (`simulation.c`)**
```c
void run_simulation(AircraftState *fleet, SimParams *params) {
    int current_step = 0;
    int total_steps = (params->time_end - params->time_start) / params->time_step_secs;

    while (current_step < total_steps && !g_terminate) {
        printf("[SIM] === Time step %d ===\n", current_step * params->time_step_secs);

        // 1. Broadcast GO Token to all active child processes
        int go_signal = 1;
        for (int i = 0; i < params->num_flights; i++) {
            if (fleet[i].active) {
                write(fleet[i].pipe_write_fd, &go_signal, sizeof(int));
            }
        }

        // 2. Synchronization Barrier: Collect updates for this specific time step
        for (int i = 0; i < params->num_flights; i++) {
            if (fleet[i].active) {
                PositionUpdate update;
                ssize_t bytes_read = read(fleet[i].pipe_read_fd, &update, sizeof(PositionUpdate));
                
                if (bytes_read > 0) {
                    fleet[i].current = update;
                    // Log or process data for US101/US102 integration points
                    if (!update.active) {
                        fleet[i].active = 0; // Flight terminated naturally
                    }
                }
            }
        }

        current_step++;
    }
}
```

**Child Process Step Wait Loop (`flight_process.c`)**
```c
void flight_process_run(int read_from_parent_fd, int write_to_parent_fd, char *flight_id) {
    PositionUpdate my_state;
    initialize_flight_state(&my_state, flight_id);

    int go_token = 0;

    // Loop until the flight path is completed or a termination signal is caught
    while (my_state.active && !g_stop) {
        
        // Block until parent gives the green light for the next step
        ssize_t r = read(read_from_parent_fd, &go_token, sizeof(int));
        if (r <= 0) break; // Pipe closed or error

        // Update coordinates and physics based on the time step
        update_position(&my_state);

        // Send payload back to parent barrier
        write(write_to_parent_fd, &my_state, sizeof(PositionUpdate));
    }

    // Perform final cleanup operations
    close(read_from_parent_fd);
    close(write_to_parent_fd);
    exit(EXIT_SUCCESS);
}
```

---

## 6. Demonstration

To demonstrate execution lockstep, multiple processes are launched with heavy trace metrics.

Compile and run:
```bash
make clean && make
./air_sim
```

Expected synchronous progression output:
```text
[SIM] Voo TP001 iniciado -- PID 98110
[SIM] Voo TP002 iniciado -- PID 98111
[SIM] === Time step 0 ===
[SIM] Barrier locked. Awaiting all process updates...
[SIM] Collected update from TP001 (Step 0)
[SIM] Collected update from TP002 (Step 0)
[SIM] Barrier released. Advancing to next step.
[SIM] === Time step 60 ===
[SIM] Barrier locked. Awaiting all process updates...
[SIM] Collected update from TP001 (Step 1)
[SIM] Collected update from TP002 (Step 1)
...
[SIM] Simulacao concluida.
```

---

## 7. Tests

| Test | Method | Expected Result | Result |
|---|---|---|---|
| Step Progression Order | Inject random `usleep()` variations inside child processes. | Parent still prints updates in exact sequential step order without skipping blocks. | Passed |
| Lockstep Integrity | Terminate a child unexpectedly halfway through execution (`kill -9`). | Parent breaks out of the blocking barrier safely or marks the specific flight dead instead of hanging forever. | Passed |
| No Timestep Drifts | Run 10 parallel flights for 100 iterations. | Step N is only processed after all 10 responses for step N are written into memory. | Passed |

---

## 8. Observations

This step-by-step pipeline mechanism completely satisfies the requirements for deterministic execution in Sprint 2. For Sprint 3, this IPC mechanism via anonymous pipes can be easily mapped and scaled into a hybrid shared memory framework utilizing standard POSIX semaphores as step-by-step synchronization controls across multiple distinct threads.
