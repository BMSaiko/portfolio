# USLP08 - Freight Route Planner - Define and Assign Routes for Pending Freights

## 6. Integration and Demo

The Freight Route Planning functionality (USLP08) is implemented in the **Freight and Traffic System** (`warehouseMngt`) module, developed in Java.

### 6.1. Integration Points

* **Database Integration (BDDAD)**: The system relies exclusively on **PL/SQL functions** accessed via **JDBC** to interact with the **Oracle** database. Key integration tables include:
    * **`Freight`**: To retrieve pending freight shipments awaiting route assignment.
    * **`Facility`**: To obtain origin and destination information for each freight.
    * **`Train`**: To assign planned routes (freights) to specific trains.
* **PL/SQL Functions**: All database operations are executed through stored functions, satisfying the core requirement (AC2):
    * `get_pending_freights()`: Returns all freights not yet assigned to any train (SYS_REFCURSOR).
    * `get_freight_by_id(freight_id)`: Retrieves detailed information for a specific freight (SYS_REFCURSOR).
    * `is_freight_pending(freight_id)`: Validates if a freight is still pending (returns 1 or 0).
    * `assign_multiple_freights_to_train(train_id, freight_ids)`: Assigns multiple freights to a train in a single atomic transaction.
* **Service Layer Integration**: The `FreightManagerMenuController` coordinates with `FreightRoutePlannerService`, maintaining strict separation between User Interface, Business Logic, and Data Access layers.
* **Route Optimization**: The service implements two distinct planning strategies:
    * **Simple Routes**: Direct origin $\rightarrow$ destination (2 stops, minimal complexity).
    * **Complex Routes**: Multi-freight optimization using facility grouping to minimize total stops and maximize operational efficiency.

### 6.2. Demo Scenario

The demonstration should focus on validating both route planning modes and the assignment workflow.

1.  **Setup**: Ensure the database contains multiple pending freights with various origin/destination combinations (e.g., Freight #2001: Munich → Paris, Freight #2002: Munich → Berlin, Freight #2003: Lyon → Paris).
2.  **Scenario A - Simple Route Planning**:
    * **Input**: The Freight Manager selects "Plan Simple Route" and inputs Freight ID `2001`.
    * **Processing**: The system calls `get_freight_by_id(2001)` and `is_freight_pending(2001)`.
    * **Output**: The system displays a **2-stop route**:
        * STOP #1 (Munich): LOAD Freight #2001 → Paris
        * STOP #2 (Paris): UNLOAD Freight #2001
3.  **Scenario B - Complex Route Planning**:
    * **Input**: The Freight Manager selects "Plan Complex Route" and inputs Freight IDs `2001,2002,2003`.
    * **Processing**: 
        * The system retrieves all three freights via `get_freight_by_id()`.
        * Validates all are pending using `is_freight_pending()`.
        * Optimizes stops by grouping common facilities (Munich loads 2 freights, Paris unloads 2).
    * **Output**: The system displays an **optimized 4-stop route**:
        * STOP #1 (Munich): LOAD Freight #2001, LOAD Freight #2002
        * STOP #2 (Lyon): LOAD Freight #2003
        * STOP #3 (Paris): UNLOAD Freight #2001, UNLOAD Freight #2003
        * STOP #4 (Berlin): UNLOAD Freight #2002
4.  **Scenario C - Route Assignment**:
    * **Input**: The manager assigns the complex route to Train ID `101`.
    * **Processing**: The system calls `assign_multiple_freights_to_train(101, '2001,2002,2003')`.
    * **Result**: Database executes a single transaction: `UPDATE freight SET train_id = 101 WHERE freight_id IN (2001, 2002, 2003)`.
    * **Confirmation**: System displays *"✓ Successfully assigned 3 freights to Train #101"*.
5.  **Validation**: Re-querying `get_pending_freights()` confirms the three freights no longer appear in the pending list.

---

## 7. Observations

* **PL/SQL Requirement**: The strict adherence to PL/SQL functions (AC2) ensures that all data access logic is encapsulated in the database layer, promoting maintainability and centralizing business rules. The use of `CallableStatement` and `OracleTypes.CURSOR` demonstrates proper Oracle integration patterns.

* **Route Sequence Display** (AC1): The formatted console output with box-drawing characters provides a clear, hierarchical view of the route plan. The display explicitly shows the **sequence of stops** (numbered 1, 2, 3, ...) and the **operations at each stop** (which freights to load/unload), satisfying the primary acceptance criterion.

* **Optimization Strategy**: The complex route planner uses a **HashMap-based grouping algorithm** that consolidates operations at shared facilities. This reduces the total number of stops and improves operational efficiency. The algorithm prioritizes loading operations before unloading to ensure cargo availability throughout the route.

* **Transactional Integrity**: The `assign_multiple_freights_to_train()` procedure implements an **atomic assignment** mechanism. All freights in a route are assigned together in a single database transaction, preventing partial assignments and maintaining data consistency. If any freight becomes unavailable during the assignment window, the entire operation fails gracefully.

* **Architectural Pattern**: The implementation strictly follows the **Controller-Service-Repository** pattern. The Controller handles user interaction, the Service contains route planning logic and orchestration, and the Repository encapsulates all database access. This separation ensures that UI code contains no SQL logic and business logic remains testable.

* **Extensibility**: The modular design allows for easy extension of optimization algorithms. Future enhancements could include:
    * **Distance-based optimization**: Minimizing total route distance.
    * **Time-window constraints**: Respecting delivery deadlines.
    * **Capacity constraints**: Ensuring train cargo limits are not exceeded.
    * **Multi-modal routing**: Incorporating intermodal transfer points.

* **Performance**: The batch assignment operation (`assign_multiple_freights_to_train()`) reduces database round-trips from N individual updates to a single procedure call, significantly improving performance for complex routes with many freights.
