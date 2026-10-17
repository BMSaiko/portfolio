# US101 - Capture and Process Flight Movements

## 1. Context

This user story belongs to Sprint 2 and is part of the SCOMP component of the project.

The goal is to extend the flight simulation created in US100 by allowing the main simulation process to receive movement updates from each flight process, track current aircraft positions, store past positions, and use that history to anticipate possible safety issues.

US101 builds directly on top of US100. US100 provides the multi-process architecture, anonymous pipes, signal handling and simulation loop. US101 processes the position updates received through the child-to-parent pipes.

---

## 1.1 List of Issues

- **Analysis:** Understand how child flight processes send movement updates and how the parent process should store and process them.
- **Design:** Extend the parent simulation loop with position tracking, historical storage and basic prediction logic.
- **Implement:** Implement current position updates, history accumulation and next-position prediction based on previous movements.
- **Test:** Validate that position updates are received through pipes, stored in the parent, accumulated in history and used to predict the next position.

---

## 2. Requirements

**US101** As a simulation process, I want to receive movement commands from flight processes so that I can track aircraft positions over time.

### Acceptance Criteria

- **US101.1:** Each flight process must send position updates to the main process via a pipe.
- **US101.2:** The main process should track aircraft positions.
- **US101.3:** The system must store past positions to anticipate and detect potential safety violations.

### Dependencies / References

- Depends on US100, which creates the child processes and the anonymous pipes.
- Supports US102, because safety violation detection requires current and historical aircraft positions.
- Related US: US100 (flight simulation), US102 (safety violations), US103 (time-step synchronisation).

---

## 3. Analysis

### 3.1 Understanding

In the simulation, each flight runs as an independent child process. At each simulation step, the parent process sends a GO command to each active child. After receiving that command, each child updates its position and sends a PositionUpdate structure back to the parent through the child-to-parent pipe.

US101 is responsible for what happens after the parent receives that structure.

For each received update, the parent process must:

1. Store the latest position as the aircraft current position.
2. Append the received position to that aircraft's history.
3. Use the stored history to predict the next position.
4. Warn if the predicted next position may leave the controlled area.

This provides the information needed by future safety checks.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Process updates in the parent process | The parent owns the global view of all aircraft. |
| Store history inside AircraftState | US100 already defines the aircraft state and history array. |
| Use PositionUpdate as the movement message | It is already sent by child processes through the pipe. |
| Keep a fixed-size history array | Avoids dynamic memory allocation and keeps the implementation simple. |
| Shift history when full | Preserves the most recent positions when the maximum history size is reached. |
| Predict next position using the last two updates | Simple approach based on recent movement direction. |
| Warn when predicted position leaves the controlled area | Demonstrates anticipation of potential safety problems without implementing full US102. |
| Keep US102 collision/proximity detection separate | US102 is responsible for advanced safety violation detection. |

---

## 4. Design

### 4.1 Realization

US101 is implemented in simulation.c, inside the main simulation loop.

The existing US100 loop already reads one PositionUpdate from each active child process. US101 adds a processing step after each successful read.

The processing is done through the following helper functions:

- store_position_update()
- predict_next_position()
- predicted_position_outside_area()
- process_position_update()

### 4.2 Movement Update Flow

Child process waits for the GO command from the parent.

The parent sends GO through the parent-to-child pipe.

The child updates its position using update_position().

The child sends a PositionUpdate to the parent through the child-to-parent pipe.

The parent reads the PositionUpdate from pipe_read_fd.

The parent calls process_position_update().

The parent stores the current position.

The parent appends the position to the aircraft history.

The parent predicts the next position.

The parent warns if the predicted position leaves the simulation area.

### 4.3 US101 Processing Flow

The parent reads a PositionUpdate from the child pipe.

Then process_position_update() is called.

This function first calls store_position_update().

store_position_update() updates fleet[i].current with the received update.

store_position_update() also appends the update to fleet[i].history.

If the aircraft has at least two stored positions, predict_next_position() is called.

predict_next_position() uses the last two positions to calculate the next expected position.

The predicted position is compared with the simulation area bounds.

If the predicted position is outside the area, the system prints a warning.

### 4.4 Prediction Logic

The prediction uses the last two known positions.

If the last two positions are previous and current, the system calculates:

- delta_lat = current latitude - previous latitude
- delta_lon = current longitude - previous longitude
- delta_alt = current altitude - previous altitude

Then the predicted next position is:

- predicted_lat = current latitude + delta_lat
- predicted_lon = current longitude + delta_lon
- predicted_alt = current altitude + delta_alt

This is not a full physics model. It is a simple prediction based on recent movement, suitable for anticipating potential problems and preparing the data needed by US102.

---

## 5. Implementation

### 5.1 File Structure

scomp/Sprint2/
- simulation.h: Shared types, structs, constants and function declarations.
- simulation.c: Parent process, simulation loop and US101 processing.
- flight_process.h: Child process interface.
- flight_process.c: Child process position update and pipe write.
- Makefile: Build configuration.

### 5.2 Key Types

PositionUpdate is the movement update sent by each child process.

It contains:

- flight_id
- latitude
- longitude
- altitude
- velocity values
- time_step
- active flag

AircraftState is the state maintained by the parent process.

It contains:

- flight_id
- child process PID
- child-to-parent pipe read file descriptor
- parent-to-child pipe write file descriptor
- current position
- history array
- history count
- active flag

### 5.3 Functions Implemented for US101

store_position_update()

This function stores the latest update as the aircraft current position and appends it to the history.

If the history is not full, the update is added to the next free position.

If the history is full, the oldest position is discarded and the newest one is stored at the end of the array.

predict_next_position()

This function predicts the next position using the last two stored positions.

The function returns 0 if there is not enough history yet. At least two positions are needed to calculate a movement delta.

predicted_position_outside_area()

This function checks whether the predicted position is outside the simulation area bounds.

It compares latitude and longitude with:

- params->lat_min
- params->lat_max
- params->lon_min
- params->lon_max

process_position_update()

This function coordinates the full US101 processing:

1. Store current position.
2. Store historical position.
3. Print tracking information.
4. Predict next position when possible.
5. Print a warning if the prediction leaves the controlled area.

### 5.4 Integration with US100

US100 already reads updates from child pipes in run_simulation().

The US101 integration point is after the parent successfully reads a PositionUpdate from pipe_read_fd.

After a successful read, US101 processes the update with process_position_update(params, &fleet[i], &upd).

US100 still handles:

- process creation;
- pipes;
- time steps;
- child termination;
- zombie prevention;
- signal handling.

US101 only extends the movement processing after updates are received.

---

## 6. Demonstration

Compile and run from the scomp/Sprint2/ directory:

make clean
make
./air_sim

Expected output includes both US100 and US101 messages.

Example output:

[US100] Voo TP001 iniciado -- PID 3958
[US100] Voo TP002 iniciado -- PID 3959
[US100] === Time step 0 ===
[US101] TP001 position stored: lat=37.0100 lon=-8.4900 alt=10000 history=1
[US100] TP001 -> lat=37.0100 lon=-8.4900 alt=10000
[US101] TP002 position stored: lat=37.0100 lon=-8.4900 alt=10000 history=1
[US100] TP002 -> lat=37.0100 lon=-8.4900 alt=10000
[US100] === Time step 60 ===
[US101] TP001 position stored: lat=37.0200 lon=-8.4800 alt=10000 history=2
[US101] TP001 predicted next position: lat=37.0300 lon=-8.4700 alt=10000
[US100] TP001 -> lat=37.0200 lon=-8.4800 alt=10000

To show only US101 output, run:

./air_sim | grep US101

Example filtered output:

[US101] TP001 position stored: lat=37.0100 lon=-8.4900 alt=10000 history=1
[US101] TP002 position stored: lat=37.0100 lon=-8.4900 alt=10000 history=1
[US101] TP001 position stored: lat=37.0200 lon=-8.4800 alt=10000 history=2
[US101] TP001 predicted next position: lat=37.0300 lon=-8.4700 alt=10000

---

## 7. Tests

All tests were executed on the target Linux virtual machine using antiX and gcc.

Compilation flags:

-Wall -Wextra -pedantic -std=c11 -D_POSIX_C_SOURCE=200809L

| Test | Method | Expected Result | Result |
|---|---|---|---|
| Clean compilation | make clean && make | Zero errors, zero warnings | Passed |
| Normal execution | ./air_sim | Simulation runs until completion | Passed |
| Child-to-parent pipe updates | Inspect output | Each active flight sends PositionUpdate to parent | Passed |
| Current position tracking | Inspect US100 and US101 output | Parent displays updated aircraft positions | Passed |
| History accumulation | Inspect history=N in output | History increases every time step per flight | Passed |
| Prediction after two updates | Inspect predicted next position output | Prediction appears from history=2 onward | Passed |
| Multiple aircraft tracking | Inspect TP001 and TP002 output | Both aircraft are tracked independently | Passed |
| Clean termination | End of execution | Both flights terminate and simulation concludes | Passed |

---

## 8. Observations

The current movement model is intentionally simple. Each child process increments latitude and longitude by a fixed value at every step. This is enough to validate inter-process communication, movement tracking and history processing.

The prediction implemented in US101 is also simple. It uses the difference between the last two positions to estimate the next one. This satisfies the requirement of storing past positions to anticipate potential safety issues.

The advanced detection of safety violations, such as aircraft proximity conflicts and signal-based alerts, is intentionally left for US102.

The implementation preserves the US100 architecture and only adds processing after the parent receives each PositionUpdate.

---

## 9. Evidence

The following execution confirms US101 behavior:

./air_sim | grep US101

Observed evidence:

- position stored confirms that the parent receives and stores movement updates.
- history=1, history=2, ..., history=60 confirms that past positions are stored.
- predicted next position confirms that the history is used to anticipate movement.
