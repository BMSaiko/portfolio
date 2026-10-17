# USLP10 - Train Dispatch - Scheduling

## 6. Integration and Demo

The Train Scheduling functionality is implemented in the **Freight and Traffic System**, specifically bridging the Java Application and the Oracle Database via stored procedures.

### 6.1. Integration Points

* **PL/SQL Integration**: Unlike other modules that use standard SQL, this module integrates with **Oracle Stored Functions** (`func_get_train_tech_data` and `func_get_train_path`). This ensures that complex joins (calculating total cargo weight or finding path segments) are handled efficiently by the Database Engine.
* **Java JDBC**: Uses `CallableStatement` and `OracleTypes.CURSOR` to retrieve data sets from the PL/SQL functions.
* **Physics Engine**: The module integrates a lightweight physics calculation:
    * $V_{actual} = \min(V_{track\_limit}, V_{train\_limit})$
    * $Time = \frac{Distance}{V_{actual}} + DwellTime$
* **User Interface**: The console interface is "Interactive", capable of pausing execution to accept user input during the scheduling process (Manual Mode).

### 6.2. Demo Scenario

The demonstration focuses on the **Interactive Manual Dispatch** capability.

1.  **Setup**: Ensure a train (e.g., ID `5437`) is assembled in the DB.
2.  **Start**: Run Option 4 (Dispatch) from the main menu.
3.  **Input**:
    * **Train ID**: `5437`
    * **Mode**: `2` (Manual)
4.  **Process**:
    * The system displays: *"TRAIN SCHEDULING #5437 | Mass: 460.0t | Power: 5600kW"*
    * The system calculates the arrival at **Valença**.
    * The system detects a **Single Line** segment towards the next station.
    * **Alert**: *"⚠️ [ALERT] Single line ahead! Crossing operation required."*
5.  **Interaction**:
    * The user selects: `1 - Wait for crossing`.
    * The system confirms: *"[OK] Train is waiting. New departure time: [Time + 10min]"*.
6.  **Result**: The schedule continues to generate times for the rest of the route, incorporating the user-defined delay.

---

## 7. Observations

* **PL/SQL Requirement**: The strict adherence to using PL/SQL functions for data retrieval satisfies the core technical requirement for this User Story.
* **Architecture**: The separation of the `TechnicalData` DTO prevents "Primitive Obsession" (passing 4-5 separate variables between methods) and makes the calculation logic cleaner.
* **Extensibility**: The "Manual Mode" logic is modular. It can easily be expanded to include other manual interventions, such as "Change Speed" or "Emergency Stop", without rewriting the entire scheduling engine.