# US109 - Generate and Store Final Simulation Report

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the SCOMP course unit.

The goal is to implement a final simulation report generation system that compiles and stores comprehensive results after the simulation concludes. The report details flight execution statuses, safety violation events with timestamps and distances, and an overall validation result (pass/fail), enabling the Flight Control Operator to assess the safety and performance of the flights post-simulation.

This user story builds upon US100 (flight simulation), US101 (position tracking), US102 (safety violation detection), and US103 (time-step synchronization).

**Note:** Per instructor guidance (Sprint 2), the report generation is implemented using a dedicated child process via `fork()`. The thread-based implementation (as originally specified) will be developed in Sprint 3.

---

## 1.1 List of Issues

- **Analysis:** Understand the report structure, the data sources available at simulation end, and the file output format.
- **Design:** Define the report content, aggregation logic, validation result rules, and file storage mechanism.
- **Implement:** Implement a report generation process that runs after the simulation loop ends, aggregates fleet state and violation records, and writes the complete report to a file.
- **Test:** Validate report completeness, correctness of violation details, pass/fail determination, and file persistence.

---

## 2. Requirements

**US109** As a Flight Control Operator, I want a comprehensive report that details the simulation outcomes — including flight execution statuses, safety violation events (with timestamps, positions and velocity vectors), and overall validation results —, so that I can assess the safety and performance of the flights post-simulation.

### Acceptance Criteria

- **US109.1:** The report generation aggregates data once the simulation concludes.
- **US109.2:** The report includes the total number of flights, individual execution statuses, and detailed safety violation events.
- **US109.3:** The final validation result (pass/fail) is clearly indicated.
- **US109.4:** The complete report is saved to a file for future reference.

### Dependencies / References

- This US depends on US100 (simulation infrastructure and fleet state), US101 (position data), and US102 (safety violation detection).
- NFR01 states the SCOMP component must be implemented in C using POSIX APIs.

---

## 3. Analysis

### 3.1 Understanding

At the end of the simulation loop, the parent process has complete knowledge of:

1. **Fleet state** — the `AircraftState` array contains each flight's final position, execution status, violation count, active state, and position history.
2. **Violation records** — safety violations detected during US102 are stored in `violations.log`, written during execution for traceability.

The final report is generated only after `run_simulation()` returns. At that point, all child flight processes have already finished or been terminated and reaped, so the report uses final and consistent data.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Generate report after simulation loop ends | All flight and violation data is final and consistent. |
| Use a dedicated child process via fork() | Per instructor guidance for Sprint 2; thread-based implementation planned for Sprint 3. |
| Read violations from violations.log | Keeps implementation simple and reuses existing US102 output. |
| Timestamped filename | Avoids overwriting previous reports across multiple runs. |
| PASS/FAIL based on violation count | PASS when zero violations detected; FAIL otherwise. |
| Use FILE* / fprintf for output | Standard C I/O is sufficient since report runs outside signal handlers. |

---

## 4. Design

### 4.1 Realization

The report generation is implemented in `report.c` via `run_report_process()`.
The function is called in a dedicated child process created in `main()` after
`run_simulation()` returns:

```c
pid_t report_pid = fork();
if (report_pid == 0) {
    run_report_process(&report_data);
    exit(EXIT_SUCCESS);
}
if (report_pid > 0)
    waitpid(report_pid, NULL, 0);
```

### 4.2 Report Generation Flow

```
run_simulation() returns
        |
        v
Fill ReportData from fleet[] and params
        |
        v
fork() -- report child process
        |
        +-- Open simulation_report_YYYYMMDD_HHMMSS.txt
        |
        +-- Write simulation summary
        |     (area, time range, step, threshold, total flights)
        |
        +-- Write per-flight execution statuses
        |     (flight ID, status, history steps, violations, final position)
        |
        +-- Write safety violation events
        |     (from violations.log -- timestamp, flight IDs, distance)
        |
        +-- Write final validation result (PASS / FAIL)
        |
        +-- Close report file
        |
        v
waitpid(report_pid) -- pai aguarda
```

### 4.3 Report File Format

**No violations (PASS):**

```
========================================
  SIMULATION REPORT
========================================
Generated  : 2026-05-17 16:00:00
Area       : OPO_MAD
Time range : 0 -- 3600 s (step 60 s, 60 steps total)
Threshold  : 50 km
Flights    : 2
----------------------------------------

FLIGHT DETAILS
Flight ID         Status        History steps   Violations  Final position
---------         ------        -------------   ----------  --------------
AA123             COMPLETED     46              0           lat=40.4895 lon=-3.5643 alt=610
AA124             COMPLETED     46              0           lat=40.9895 lon=-3.0643 alt=610

----------------------------------------
SAFETY VIOLATIONS: 0 total

----------------------------------------
VALIDATION RESULT: PASS
========================================
```

**With violations (FAIL):**

```
========================================
  SIMULATION REPORT
========================================
Generated  : 2026-05-17 16:05:00
Area       : OPO_MAD
Time range : 0 -- 3600 s (step 60 s, 60 steps total)
Threshold  : 100 km
Flights    : 2
----------------------------------------

FLIGHT DETAILS
Flight ID         Status        History steps   Violations  Final position
---------         ------        -------------   ----------  --------------
AA123             TERMINATED    1               3           lat=41.3219 lon=-8.6312 alt=803
AA124             TERMINATED    1               3           lat=41.8219 lon=-8.1312 alt=803

----------------------------------------
SAFETY VIOLATIONS: 3 total

Time step   Flight 1          Flight 2          Distance (km)
---------   --------          --------          ------------
0           AA123             AA124             69.43
0           AA123             AA124             69.43
0           AA123             AA124             69.43

----------------------------------------
VALIDATION RESULT: FAIL
Reason: 3 safety violation(s) detected during simulation.
========================================
```

---

## 5. Implementation

### 5.1 File Structure

```
scomp/us100/
+-- simulation.h      # Shared types, structs, constants
+-- simulation.c      # Parent process -- calls report fork() in main()
+-- flight_process.h  # Child process interface
+-- flight_process.c  # Child process: position updates, signal handling
+-- report.h          # ReportData struct and run_report_process() prototype
+-- report.c          # Report generation logic
+-- Makefile          # Build configuration
```

### 5.2 Key Types

**`ReportData`** — passed to the report process after simulation ends:

```c
typedef struct {
    char          area_code[16];
    int           num_flights;
    int           time_start;
    int           time_end;
    int           time_step_secs;
    int           safety_threshold;
    AircraftState fleet[MAX_FLIGHTS];
} ReportData;
```

### 5.3 Key Functions

**`run_report_process()`** — entry point of the report child process. Opens the
report file, writes simulation summary, per-flight statuses, violation events
parsed from `violations.log`, and the final PASS/FAIL result.

**`count_violations_from_log()`** — counts total lines in `violations.log`.

**`count_violations_for_flight()`** — counts violations involving a specific
flight ID.

---

## 6. Demonstration

```bash
make clean && make

# Normal scenario -- expect PASS
rm -f violations.log
./air_sim config/opo_mad.cfg
cat simulation_report_*.txt

# Violation scenario -- expect FAIL
rm -f violations.log simulation_report_*.txt
./air_sim config/collisions.cfg
cat simulation_report_*.txt
cat violations.log
```

---

## 7. Tests

| Test | Method | Expected Result | Result |
|---|---|---|---|
| Clean compilation | `make clean && make` | Zero errors, zero warnings | Passed |
| Report file created | Run any config | `simulation_report_*.txt` exists | Passed |
| Report header correct | Inspect file | Area, time range, threshold, flights present | Passed |
| Per-flight status | Inspect file | Each flight listed with status, steps, violations, position | Passed |
| No violations -- PASS | Run `opo_mad.cfg` | `VALIDATION RESULT: PASS` | Passed |
| With violations -- FAIL | Run `collisions.cfg` | `VALIDATION RESULT: FAIL` with violation table | Passed |
| Violation log created | `cat violations.log` | Violation entries present | Passed |

---

## 8. Observations

The report generation is performed by a dedicated child process created with
`fork()` after `run_simulation()` returns. All child flight processes have
already been reaped at this point, so the `fleet[]` data is fully consistent
and there are no race conditions.

The `violations.log` file is cleared at the start of each simulation run to
avoid accumulating entries from previous runs. Each report file uses a
timestamped filename so previous reports are preserved across runs.

For Sprint 3, this implementation will be extended to use a POSIX thread with
mutex and condition variable synchronisation, as originally specified.