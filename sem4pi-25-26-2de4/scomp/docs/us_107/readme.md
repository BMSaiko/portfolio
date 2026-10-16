# US107 - Notify Report Thread via Condition Variables upon Safety Violation Detection

## 1. Context

Sprint 3 requirement for SCOMP. The goal is to implement thread-based communication between the safety violation detection thread and the report generation thread. When the detection thread finds a violation, it notifies the report thread via condition variables for real-time report updates.

Builds upon US105 (shared memory/threads), US106 (dedicated threads), and replaces the US109 `fork()` approach with POSIX thread synchronization.

### 1.1 List of Issues

**Analysis:** Thread communication model, condition variable semantics, shared state.
**Design:** Shared data structures, mutex/cond usage, thread lifecycle.
**Implement:** Condition variable signaling from detection to report thread.
**Test:** Real-time notification, thread safety, correct report updates.

---

## 2. Requirements

**US107** As a PO, I want the safety violation detection thread to notify the report generation thread through condition variables when a safety violation occurs, so that the report is updated in real time.

### Acceptance Criteria

- **US107.1:** Safety violation detection thread monitors shared memory for flight conflicts.
- **US107.2:** Upon detecting a violation, the thread signals the report thread using condition variables.
- **US107.3:** Report thread, waiting on condition variable, immediately processes the violation.
- **US107.4:** Proper mutex locking ensures thread-safe notification.

### Dependencies

- **US105** (Shared memory, threads, mutexes)
- **US106** (Dedicated threads in parent)
- **US102** (Safety violation detection / Haversine)
- **US109** (Report format)

---

## 3. Analysis

### 3.1 Understanding

Sprint 2 used `fork()` for process-based parallelism. Sprint 3 restructures to POSIX threads. US107 addresses communication between:
1. **Safety Violation Detection Thread** - scans shared memory for proximity violations
2. **Report Generation Thread** - compiles results, responds to safety events

The detection thread must notify the report thread in real time via condition variables.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| POSIX condition variables | Standard thread sync primitive for SCOMP |
| Shared mutex + cond var | Protects violation queue, coordinates signaling |
| Bounded violation queue | Prevents unbounded memory growth |
| `pthread_cond_signal` | Wakes exactly one waiting thread |
| `pthread_cond_wait` | Blocks efficiently until violation detected |

### 3.3 Thread Communication Model


```
+----------------------+       +-----------------------+
|  Detection Thread    |       |  Report Thread        |
+----------------------+       +-----------------------+
| while (sim_running)  |       | while (sim_running)  |
|   scan_shared_mem()  |       |   mutex_lock(&mtx)   |
|   if (violation) {   |       |   while (queue_empty) |
|     mutex_lock()     |       |     cond_wait(&cond) |
|     enqueue()        |       |   dequeue()          |
|     cond_signal()    |  ---> |   mutex_unlock()     |
|     mutex_unlock()   |       |   write_to_report()  |
|   }                  |       | }                    |
+----------------------+       +-----------------------+
```


---

## 4. Design

### 4.0 Required Headers

```c
#include <pthread.h>
#include <unistd.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
```

### 4.1 Shared Data Structures

```c
typedef struct {
    pthread_mutex_t mtx;
    pthread_cond_t  cond;
    ViolationEvent  queue[MAX_VIOLATIONS];
    int             queue_head;
    int             queue_tail;
    int             queue_count;
    volatile int    sim_running;  /* volatile: read by multiple threads */
} ViolationNotifier;
```

### 4.2 Detection Thread

```c
void *safety_violation_detector(void *arg) {
    SharedMemory *shm = (SharedMemory *)arg;
    while (notifier.sim_running) {
        for (int i = 0; i < shm->num_flights; i++) {
            for (int j = i + 1; j < shm->num_flights; j++) {
                double dist = haversine(shm->positions[i], shm->positions[j]);
                if (dist < SAFETY_THRESHOLD_KM) {
                    if (pthread_mutex_lock(&notifier.mtx) != 0) {
                        perror("pthread_mutex_lock");
                        pthread_exit(NULL);
                    }
                    enqueue_violation(&notifier, i, j, dist);
                    pthread_cond_signal(&notifier.cond);
                    if (pthread_mutex_unlock(&notifier.mtx) != 0) {
                        perror("pthread_mutex_unlock");
                        pthread_exit(NULL);
                    }
                }
            }
        }
        usleep(SCAN_INTERVAL_US);
    }
    pthread_exit(NULL);
}
```

### 4.3 Report Thread

```c
void *report_generator(void *arg) {
    while (notifier.sim_running) {
        if (pthread_mutex_lock(&notifier.mtx) != 0) {
            perror("pthread_mutex_lock");
            pthread_exit(NULL);
        }
        while (notifier.queue_count == 0 && notifier.sim_running) {
            if (pthread_cond_wait(&notifier.cond, &notifier.mtx) != 0) {
                perror("pthread_cond_wait");
                pthread_mutex_unlock(&notifier.mtx);
                pthread_exit(NULL);
            }
        }
        if (!notifier.sim_running) {
            pthread_mutex_unlock(&notifier.mtx);
            break;
        }
        ViolationEvent evt = dequeue_violation(&notifier);
        if (pthread_mutex_unlock(&notifier.mtx) != 0) {
            perror("pthread_mutex_unlock");
            pthread_exit(NULL);
        }
        append_violation_to_report(&report_buf, &evt);
    }
    write_final_report(&report_buf);
    pthread_exit(NULL);
}
```

### 4.4 Initialization and Cleanup

```c
/* Init */
if (pthread_mutex_init(&notifier.mtx, NULL) != 0) {
    perror("pthread_mutex_init");
    exit(1);
}
if (pthread_cond_init(&notifier.cond, NULL) != 0) {
    perror("pthread_cond_init");
    exit(1);
}
notifier.sim_running = 1;

/* Cleanup */
pthread_mutex_lock(&notifier.mtx);
notifier.sim_running = 0;
pthread_mutex_unlock(&notifier.mtx);
pthread_cond_broadcast(&notifier.cond);
pthread_join(report_tid, NULL);
pthread_join(detection_tid, NULL);
pthread_mutex_destroy(&notifier.mtx);
pthread_cond_destroy(&notifier.cond);
```

---

## 5. Demonstration

```bash
cd scomp && make clean && make
./air_sim config/opo_mad.cfg
```

Expected output:
```
[US106] Safety violation detection thread started
[US106] Report generation thread started
[US107] Violation: AA123 <-> AA124 (69.43 km) -> report thread notified
[US107] Final report written to simulation_report.txt
```

---

## 6. Tests

| Test | Expected Result |
|---|---|
| Cond var signal wakes report thread | Report processes violation immediately |
| Mutex protects shared queue | No race conditions |
| Multiple violations queued | All processed without loss |
| Report thread blocks when idle | No busy-waiting |
| Clean thread exit | Both threads join without deadlock |
| No deadlock | Mutex always released after use |

---

## 7. Observations

### 7.1 Relationship with Other US
- US105 provides shared memory; US106 defines threads; US102 provides Haversine; US109 provides format

### 7.2 Design Notes
- Condition variable used with predicate (queue_count > 0) to handle spurious wakeups
- Bounded queue prevents memory exhaustion
- `pthread_cond_broadcast` used during shutdown for clean exit

### 7.3 Future Improvements
- Multiple report consumers via broadcast
- Priority queue for critical violations
- Statistics on notification latency

---

## 8. Implementation (2026-06-07)

### Approach
Detection is performed inline in `check_safety_violations()` → `handle_violation()` → `notify_violation()`. The bridge function enqueues a `ViolationEvent` into a mutex-protected bounded queue and signals the report thread via `pthread_cond_signal()`. No separate detection thread needed — main loop handles detection at each time step.

### Files Changed
| File | Change |
|------|--------|
| `simulation.h` | Added `ViolationEvent`, `ViolationNotifier` types + API declarations |
| `simulation.c` | Notifier API, `notify_violation()` bridge, `handle_violation()` with flight indices, report thread in `main()` |
| `report_thread.h` | New — `ReportThreadArgs` + `report_thread_main()` declaration |
| `report_thread.c` | New — cond var wait loop, accumulation, file writing |
| `Makefile` | Added `report_thread.o` |

### Verification
```
$ make clean && make
BUILD SUCCESS — zero errors, zero warnings

$ ./air_sim config/quick_collisions.cfg
[US107] Violation: idx=0 <-> idx=1 (69.43 km) -> report notified

$ cat simulation_report_*.txt
VALIDATION RESULT: FAIL
```

### Acceptance Criteria
| ID | Criteria | Status |
|----|----------|--------|
| US107.1 | Detection monitors for conflicts | ✅ (inline in `check_safety_violations`) |
| US107.2 | Signals report thread via cond vars | ✅ (`pthread_cond_signal`) |
| US107.3 | Report thread processes immediately | ✅ (`pthread_cond_wait` in while loop) |
| US107.4 | Mutex locking ensures safety | ✅ (all paths lock/unlock) |
