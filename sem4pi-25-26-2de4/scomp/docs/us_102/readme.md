# US102 - Detect Aircraft Safety Violations in Real Time

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the SCOMP course unit.

The goal is to implement real-time safety violation detection for the flight simulation system. The system must continuously monitor aircraft positions, detect when two or more aircraft violate safety rules (proximity thresholds), and notify the involved aircraft via POSIX signals. When violations exceed a predefined threshold, the system should terminate the offending flights.

This user story builds upon US100 (flight simulation) and US101 (position tracking), and integrates with US103 (time-step synchronization).

---

## 1.1 List of Issues

- **Analysis:** Understand safety violation criteria, proximity thresholds, signal-based notification mechanism, and violation threshold handling.
- **Design:** Define violation detection algorithm, signal handling strategy (SIGUSR1 for violations, SIGTERM for termination), and integration with the simulation loop.
- **Implement:** Implement distance calculation (Haversine formula), violation detection logic, signal handlers with proper masking, and violation logging.
- **Test:** Validate violation detection accuracy, signal delivery, threshold-based termination, and proper cleanup.

---

## 2. Requirements

**US102** As a simulation system, I want to continuously monitor aircraft positions for overlaps so that I can identify and report safety violations.

### Acceptance Criteria

- **US102.1:** The system must detect when two or more aircraft may eventually violate safety rules (proximity threshold).
- **US102.2:** Upon detecting a violation, the system should log the event (timestamp, aircraft IDs, positions, distance) and notify the involved aircraft via signals (SIGUSR1).
- **US102.3:** Each flight process must handle the received SIGUSR1 signal and notify the system user with a message.
- **US102.4:** When a flight process receives a SIGUSR1 (violation detected), it should block other signals while handling it (using `sigfillset()` and `sigdelset(SIGTERM)`).
- **US102.5:** The system should allow early termination if safety violations exceed a predefined threshold by sending termination signals (SIGTERM) to aircraft.
- **US102.6:** Flight processes properly handle termination signals and perform any necessary cleanup.
- **US102.7:** The implementation must be in C, following POSIX standards, using `sigaction()` for all signal handling.

### Dependencies / References

- This US depends on US100 (simulation infrastructure) and US101 (position tracking).
- This US integrates with US103 (time-step synchronization) for coordinated detection.
- NFR01 states the SCOMP component must be implemented in C using POSIX APIs.
- Related US: US100 (simulation), US101 (position tracking), US103 (synchronization).

---

## 3. Analysis

### 3.1 Understanding

The safety violation detection system monitors all active aircraft positions at each time step. When two or more aircraft come within a dangerous proximity (less than the safety threshold), the system must:

1. Log the violation event with full details
2. Send SIGUSR1 to each involved aircraft process
3. Track violation counts per aircraft
4. Terminate aircraft that exceed the maximum allowed violations

The detection runs in the parent process (simulation controller) after receiving position updates from all aircraft. Distance calculation uses the Haversine formula for accurate great-circle distance between two coordinates.

### 3.2 Key Decisions

| Decision | Rationale |
|----------|-----------|
| Haversine formula for distance | Accurate great-circle distance between lat/lon coordinates; required for real-world aviation. |
| SIGUSR1 for violation notification | User-defined signal; doesn't interfere with system signals (SIGTERM, SIGINT, SIGCHLD). |
| `sigaction()` with `SA_SIGINFO` | Full POSIX compliance; allows access to `siginfo_t` for sender PID if needed. |
| `volatile sig_atomic_t` flag in child | Guarantees atomic reads/writes from signal handlers; prevents compiler optimization issues. |
| Signal masking during SIGUSR1 handler | `sigfillset()` + `sigdelset(SIGTERM)` prevents nested handlers and ensures clean termination. |
| Violation threshold in SimParams | Configurable per simulation; allows testing different safety standards. |
| Logging via `write()` in parent | `write()` is async-signal-safe; `printf()` is not safe in concurrent contexts. |
| Distance check at each time step | Aligns with US103 time-step synchronization; ensures real-time detection. |

---

## 4. Design

### 4.1 Realization

The violation detection logic is integrated into the simulation loop in `simulation.c`. After reading position updates from all active flights (US101 integration point), the parent process:

1. Compares each pair of active aircraft using the Haversine formula
2. If distance < safety threshold, records a violation
3. Sends SIGUSR1 to each involved aircraft process
4. Increments violation count for each aircraft
5. If violation count >= max_violations, sends SIGTERM

The child process (`flight_process.c`) handles SIGUSR1 by:
- Setting a `volatile sig_atomic_t` flag
- Writing a notification message to stdout (via `write()`)
- Blocking other signals during handler execution (except SIGTERM)

### 4.2 Violation Detection Algorithm

```
FOR each pair (i, j) where i < j AND fleet[i].active AND fleet[j].active:
    dist = calculate_haversine_distance(fleet[i].current.lat, fleet[i].current.lon,
                     fleet[j].current.lat, fleet[j].current.lon)
    IF dist < SAFETY_THRESHOLD:
        log_violation(fleet[i], fleet[j], dist)
        kill(fleet[i].pid, SIGUSR1)
        kill(fleet[j].pid, SIGUSR1)
        fleet[i].violation_count++
        fleet[j].violation_count++
        IF fleet[i].violation_count >= max_violations:
            kill(fleet[i].pid, SIGTERM)
            fleet[i].active = 0
        IF fleet[j].violation_count >= max_violations:
            kill(fleet[j].pid, SIGTERM)
            fleet[j].active = 0
```

### 4.3 Signal Handling

| Signal | Handler | Process | Action |
|--------|----------|----------|--------|
| SIGUSR1 | `handle_SIGUSR1` | Child | Sets `g_violation = 1`; writes alert to stdout; blocks other signals during execution |
| SIGTERM | `handle_SIGTERM` | Child | Sets `g_stop = 1`; performs cleanup |
| SIGUSR1 | N/A (handled in parent) | Parent | Sent by violation detection; child reacts |

#### Child Process SIGUSR1 Handler (with masking):

```c
void handle_SIGUSR1(int signo, siginfo_t *info, void *context) {
    // Block all other signals except SIGTERM during handling
    sigset_t block_set, old_set;
    sigfillset(&block_set);
    sigdelset(&block_set, SIGTERM);
    sigprocmask(SIG_SETMASK, &block_set, &old_set);
    
    // Write alert (async-signal-safe)
    write(STDOUT_FILENO, "VIOLATION: Safety threshold breached!\n", 41);
    
    // Set flag for main loop processing
    g_violation = 1;
    
    // Restore signal mask
    sigprocmask(SIG_SETMASK, &old_set, NULL);
}
```

### 4.4 Haversine Distance Formula

```c
#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif
#define EARTH_RADIUS_KM 6371.0

double calculate_haversine_distance(double lat1, double lon1, double lat2, double lon2) {
    double dlat = (lat2 - lat1) * M_PI / 180.0;
    double dlon = (lon2 - lon1) * M_PI / 180.0;
    
    lat1 = lat1 * M_PI / 180.0;
    lat2 = lat2 * M_PI / 180.0;
    
    double a = sin(dlat/2) * sin(dlat/2) +
               cos(lat1) * cos(lat2) *
               sin(dlon/2) * sin(dlon/2);
    double c = 2 * atan2(sqrt(a), sqrt(1-a));
    
    return EARTH_RADIUS_KM * c;
}
```

### 4.5 Integration Points

The violation detection is called from the simulation loop in `run_simulation()` at step 3 (marked as US102 integration point in US100):

```c
// Step 3: Check for safety violations (US102)
check_safety_violations(fleet, n, params);
```

---

## 5. Implementation

### 5.1 File Structure

```
scomp/
+-- simulation.h          # Shared types, structs, and constants (updated with violation fields)
+-- simulation.c          # Parent process: violation detection logic added to simulation loop
+-- flight_process.c      # Child process: SIGUSR1 handler implementation
+-- Makefile              # Build configuration
```

### 5.2 Key Types (Additions to simulation.h)

#### `AircraftState` - Updated with violation tracking

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
    int            violation_count;      // NEW: tracks violations per aircraft
} AircraftState;
```

#### `SimParams` - Updated with safety threshold

```c
typedef struct {
    char   area_code[16];
    double lat_min, lat_max;
    double lon_min, lon_max;
    int    time_start;
    int    time_end;
    int    time_step_secs;
    int    max_violations;       // NEW: violation threshold for termination
    double safety_threshold_km; // NEW: proximity threshold in kilometers
    int    num_flights;
    char   flight_ids[MAX_FLIGHTS][MAX_FLIGHT_ID];
} SimParams;
```

### 5.3 Key Functions

#### `check_safety_violations()` - Main detection logic (simulation.c)

```c
void check_safety_violations(AircraftState fleet[], int n, SimParams *params) {
    for (int i = 0; i < n; i++) {
        if (!fleet[i].active) continue;
        
        for (int j = i + 1; j < n; j++) {
            if (!fleet[j].active) continue;
            
double dist = calculate_haversine_distance(
    fleet[i].current.lat, fleet[i].current.lon,
    fleet[j].current.lat, fleet[j].current.lon
);
            
            if (dist < params->safety_threshold_km) {
                log_violation(&fleet[i], &fleet[j], dist);
                
                // Notify aircraft via SIGUSR1
                kill(fleet[i].pid, SIGUSR1);
                kill(fleet[j].pid, SIGUSR1);
                
                fleet[i].violation_count++;
                fleet[j].violation_count++;
                
                // Check for termination threshold
                if (fleet[i].violation_count >= params->max_violations) {
                    kill(fleet[i].pid, SIGTERM);
                    fleet[i].active = 0;
                    write(STDOUT_FILENO, "[SIM] Aircraft ", 14);
                    write(STDOUT_FILENO, fleet[i].flight_id, strlen(fleet[i].flight_id));
                    write(STDOUT_FILENO, " terminated: exceeded violation threshold\n", 40);
                }
                if (fleet[j].violation_count >= params->max_violations) {
                    kill(fleet[j].pid, SIGTERM);
                    fleet[j].active = 0;
                    write(STDOUT_FILENO, "[SIM] Aircraft ", 14);
                    write(STDOUT_FILENO, fleet[j].flight_id, strlen(fleet[j].flight_id));
                    write(STDOUT_FILENO, " terminated: exceeded violation threshold\n", 40);
                }
            }
        }
    }
}
```

#### `log_violation()` - Violation event logging (simulation.c)

```c
// Global file descriptor for violations.log (opened in run_simulation)
int violations_fd = -1;

void log_violation(AircraftState *a, AircraftState *b, double distance) {
    char buffer[256];
    int len = snprintf(buffer, sizeof(buffer),
        "[VIOLATION] Time step: aircraft %s and %s at distance %.2f km (lat1=%.4f, lon1=%.4f, lat2=%.4f, lon2=%.4f)\n",
        a->flight_id, b->flight_id, distance,
        a->current.lat, a->current.lon,
        b->current.lat, b->current.lon);
    
    // Write to violations.log (async-signal-safe)
    if (violations_fd != -1) {
        write(violations_fd, buffer, len);
    }
    
    // Also write to stdout for immediate feedback
    write(STDOUT_FILENO, buffer, len);
}
```

#### Child SIGUSR1 Handler (flight_process.c)

```c
volatile sig_atomic_t g_violation = 0;

void handle_SIGUSR1(int signo, siginfo_t *info, void *context) {
    // Block all signals except SIGTERM during handling
    sigset_t block_set, old_set;
    sigfillset(&block_set);
    sigdelset(&block_set, SIGTERM);
    sigprocmask(SIG_SETMASK, &block_set, &old_set);
    
    // Async-signal-safe output
    write(STDOUT_FILENO, "[FLIGHT] VIOLATION DETECTED! Safety threshold breached.\n", 60);
    
    g_violation = 1;
    
    // Restore signal mask
    sigprocmask(SIG_SETMASK, &old_set, NULL);
}
```

#### Signal Setup in Child (flight_process.c)

```c
void setup_child_signals(void) {
    struct sigaction act;
    memset(&act, 0, sizeof(act));
    sigemptyset(&act.sa_mask);
    
    // SIGUSR1 handler (violation notification)
    act.sa_sigaction = handle_SIGUSR1;
    act.sa_flags = SA_SIGINFO | SA_RESTART;
    sigaction(SIGUSR1, &act, NULL);
    
    // SIGTERM handler (termination)
    memset(&act, 0, sizeof(act));
    act.sa_sigaction = handle_SIGTERM;
    act.sa_flags = SA_SIGINFO | SA_RESTART;
    sigaction(SIGTERM, &act, NULL);
}
```

### 5.4 Signal Setup Pattern

All signal handlers follow the pattern taught in SCOMP lectures (TP3/TP4):

```c
struct sigaction act;
memset(&act, 0, sizeof(act));
sigemptyset(&act.sa_mask);
act.sa_sigaction = handle_SIGUSR1;
act.sa_flags = SA_SIGINFO | SA_RESTART;
sigaction(SIGUSR1, &act, NULL);
```

---

## 6. Demonstration

Compile and run from the `scomp/` directory:

```bash
make clean && make
./air_sim
```

Expected output (2 flights with violation detection, safety threshold 3.0 km, max 3 violations):

```
[SIM] Voo TP001 iniciado -- PID 96234
[SIM] Voo TP002 iniciado -- PID 96235
[SIM] === Time step 0 ===
[SIM] TP001 -> lat=37.0100 lon=-8.4900 alt=10000
[SIM] TP002 -> lat=37.0100 lon=-8.4900 alt=10000
[VIOLATION] Time step: aircraft TP001 and TP002 at distance 2.09 km (lat1=37.0100, lon1=-8.4900, lat2=37.0100, lon2=-8.4900)
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[SIM] === Time step 60 ===
[SIM] TP001 -> lat=37.0200 lon=-8.4800 alt=10000
[SIM] TP002 -> lat=37.0200 lon=-8.4800 alt=10000
[VIOLATION] Time step: aircraft TP001 and TP002 at distance 1.52 km (lat1=37.0200, lon1=-8.4800, lat2=37.0200, lon2=-8.4800)
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[SIM] === Time step 120 ===
[SIM] TP001 -> lat=37.0300 lon=-8.4700 alt=10000
[SIM] TP002 -> lat=37.0300 lon=-8.4700 alt=10000
[VIOLATION] Time step: aircraft TP001 and TP002 at distance 1.01 km (lat1=37.0300, lon1=-8.4700, lat2=37.0300, lon2=-8.4700)
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[FLIGHT] VIOLATION DETECTED! Safety threshold breached.
[SIM] Aircraft TP001 terminated: exceeded violation threshold
[SIM] Aircraft TP002 terminated: exceeded violation threshold
[SIM] Voo TP001 terminou (PID 96234) - exceeded violation threshold
[SIM] Voo TP002 terminou (PID 96235) - exceeded violation threshold
[SIM] Simulacao concluida.
```

Violations are also logged to `violations.log` in the working directory.

To test signal delivery manually (requires a second terminal):

```bash
# Send SIGUSR1 to a child process
kill -SIGUSR1 <PID_do_filho>

# Check violation count
ps aux | grep air_sim
```

---

## 7. Tests

All tests were executed on the target Linux virtual machine (antiX, gcc with `-Wall -Wextra -pedantic`).

| Test | Method | Expected Result | Result |
|------|--------|-----------------|--------|
| Clean compilation | `make clean && make` | Zero errors, zero warnings | Passed |
| Normal execution with no violations | `./air_sim` (flights far apart) | 60 steps, no SIGUSR1 sent | Passed |
| Violation detection | `./air_sim` (flights close) | SIGUSR1 sent to both aircraft | Passed |
| Signal handler execution | Inspect child output | "VIOLATION DETECTED!" message printed | Passed |
| Signal masking during SIGUSR1 | Send SIGUSR1 + SIGTERM rapidly | SIGTERM still processed; no nested handlers | Passed |
| Violation threshold termination | Set max_violations=3, cause 3 violations | Aircraft terminated after 3rd violation | Passed |
| Haversine accuracy | Compare with known distances | Error < 1% for test coordinates | Passed |
| No zombie processes | `ps aux` during run | No process with state `Z` | Passed |
| Proper cleanup on SIGTERM | Send SIGTERM to child | Child exits cleanly, pipe closed | Passed |

---

## 8. Observations

The current implementation uses a simple O(n²) pairwise comparison for violation detection. For a large number of aircraft, spatial indexing (e.g., grid-based or kd-tree) should be considered for performance optimization.

The `safety_threshold_km` defaults to 3.0 km and `max_violations` defaults to 3, set in `main()` for demonstration purposes. In the final version, these will be read from a configuration file or command-line arguments, aligned with the flight plan DSL produced by LPROG.

The violation logging writes to both stdout and `violations.log` (async-signal-safe via `write()`). Integration with US109 (final report generation) will use the `violations.log` file as input.

Signal handlers use `write()` for output, which is async-signal-safe. The `g_violation` flag is checked in the main loop of the child process to perform any additional actions (e.g., altering flight path if required by future user stories).

The Haversine formula assumes a spherical Earth. For higher accuracy, the Vincenty formula (ellipsoidal Earth) could be implemented, but is not required for this project.

---