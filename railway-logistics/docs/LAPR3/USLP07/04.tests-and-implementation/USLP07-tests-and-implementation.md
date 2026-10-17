## 6. Integration and Demo

The Train Dispatch functionality (USLP07) is implemented in the **Freight and Traffic System** (`freightMngt` ) module, primarily developed in Java.

### 6.1. Integration Points

* **Database Integration (BDDAD)**: The system uses **JDBC** to connect to the **Oracle** database (`databaseMngt` ) to retrieve static data required for calculations:
    * **Rolling Stock** (Locomotives, Wagons): To determine total power and weight for $V_{traction}$ calculation.
    * **Infrastructure** (Line Segments): To fetch length and maximum speed limits ($V_{track}$).
* **Safety System Interface (C/Assembly)**: While the `stationMngt`  system is primarily for real-time station management, the planned **Route** and **PassageEvents** created by the scheduler must be available to the Safety System (potentially via `csv files` ) for real-time traffic coordination and conflict warning.
* **User Interface (LAPR3)**: The final dispatch plan is presented to the **Freight Manager** through a user-friendly interface.

### 6.2. Demo Scenario

The demonstration should focus on validating the core requirements: accurate time calculation and conflict resolution.

1.  **Input**: The Freight Manager selects a Train (with specific Locomotives and loaded Wagons) and inputs a **manual Route** (e.g., Endpoint A $\rightarrow$ Endpoint B $\rightarrow$ Endpoint C).
2.  **Calculation**: The system iterates through the Line Segments, calculating the limiting speed $V_{max} = \min(V_{track}, V_{traction})$, and determines the Estimated Time of Passage for each Endpoint.
3.  **Conflict Simulation**: The demo includes a scenario where two trains are dispatched on a **single-track** segment with planned conflicting times.
4.  **Output**: The system demonstrates the **Crossing Operation** being inserted into the schedule, indicating the necessary delay at a preceding station/siding, and outputs the final estimated times for all points.

---

## 7. Observations

* **Technical Language Requirement**: The database management will use **PL/SQL**, and the main application logic will be developed in **Java**.
* **Constraint Handling**: The project successfully enforces the crucial business constraint that the final travel speed is determined by the most restrictive factor: either the **track's speed limit** ($V_{track}$) or the **train's capacity to pull the load** ($V_{traction}$).
* **Simplification (MVP)**: Following the Minimum Viable Product (MVP) approach, the implementation correctly adheres to the simplification that **acceleration can be assumed to be instantaneous**  for travel time calculation.
* **Route Definition**: The requirement to allow the route to be **defined manually** by the Freight Manager is met, postponing the development of a complex automatic pathfinding system for a later sprint.