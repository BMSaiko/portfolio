# USLP08 - Freight Route Planner - Define and Assign Routes for Pending Freights

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data (Section 1.6 and 1.5).**

| Interaction ID | Question: Which class is responsible for... | Answer | Justification (with patterns) |
|:------------- |:--------------------- |:------------|:---------------------------- |
| Step 1 | ...coordinating the route planning workflow? | FreightManagerMenuController | Controller Pattern: coordinates user input and orchestrates the route planning process. |
| Step 2 | ...retrieving pending freights from the database? | FreightRepository | Information Expert + Repository Pattern: knows how to access freight data using PL/SQL functions. |
| Step 3 | ...executing PL/SQL function calls? | FreightRepository | Information Expert: encapsulates database access logic and manages CallableStatement execution. |
| Step 4 | ...planning the optimal route sequence? | FreightRoutePlannerService | Pure Fabrication: dedicated service for route calculation and optimization logic. |
| Step 5 | ...representing a complete route? | FreightRoute | Information Expert: holds the route name, type, and ordered list of stops. |
| Step 6 | ...representing a stop with load/unload operations? | RouteStop | Information Expert: knows which freights to load/unload at a specific facility. |
| Step 7 | ...determining which freights to load at a stop? | RouteStop | Information Expert: maintains lists of freights associated with its facility. |
| Step 8 | ...assigning freights to a train? | FreightRepository | Information Expert: knows how to call the PL/SQL procedure for assignment. |
| Step 9 | ...validating freight pending status? | FreightRepository | Information Expert: executes the `is_freight_pending()` function. |
| Step 10 | ...displaying the route plan to the user? | FreightRoutePlannerService | Pure Fabrication: dedicated to formatting and presenting route information. |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* Freight
* Facility
* FreightRoute
* RouteStop

Other software classes (i.e. Pure Fabrication) identified:

* FreightManagerMenuController (Controller)
* FreightRoutePlannerService (Service/Business Logic)
* FreightRepository (Repository/Data Access)

---

## 3.2. Sequence Diagram (SD)

![USLP08-SD](./puml/USLP08-sequence-diagram.png)

The sequence diagram illustrates two main scenarios:

### 3.2.1. Simple Route Planning
1. FreightManager requests to plan a simple route
2. Controller requests freight ID
3. Repository executes `get_freight_by_id()` PL/SQL function
4. Repository executes `is_freight_pending()` to validate
5. Service creates FreightRoute with 2 stops (origin, destination)
6. Route display is presented to user

### 3.2.2. Complex Route Planning
1. FreightManager requests to plan a complex route
2. Controller retrieves pending freights via `get_pending_freights()`
3. FreightManager selects multiple freight IDs
4. Service validates all freights are pending
5. Service optimizes stops by grouping common facilities
6. Service creates FreightRoute with optimized stop sequence
7. Route display is presented to user

### 3.2.3. Route Assignment
1. FreightManager provides train ID for assignment
2. Service validates all freights in route are still pending
3. Repository calls `assign_multiple_freights_to_train()` procedure
4. Database updates all freight assignments in single transaction
5. Confirmation is returned to user

---

## 3.3. Class Diagram (CD)

![USLP08-CD](./puml/USLP08-class-diagram.png)

The class diagram shows:
* **FreightManagerMenuController**: UI controller managing user interactions
* **FreightRoutePlannerService**: Business logic for route planning and optimization
* **FreightRepository**: Data access layer using PL/SQL functions
* **FreightRoute**: Domain entity representing a complete route
* **RouteStop**: Domain entity representing a stop with operations
* **Freight**: Domain entity representing a freight shipment
* **Facility**: Domain entity representing an origin/destination station

---

## 3.4. Design Patterns Applied

* **Information Expert**: 
  * **FreightRepository** knows how to access database using PL/SQL functions.
  * **FreightRoute** knows its stops and can calculate total freight count.
  * **RouteStop** knows which freights to load/unload at its facility.
  
* **Pure Fabrication**: 
  * **FreightRoutePlannerService** is responsible solely for route planning logic and optimization algorithms.
  * This service doesn't represent a domain concept but provides essential functionality.
  
* **Controller**: 
  * **FreightManagerMenuController** handles all user interactions and coordinates the system operations.
  
* **Repository**: 
  * **FreightRepository** encapsulates all database access, isolating PL/SQL calls from business logic.
  * This ensures the service layer doesn't need to know about database implementation details.

* **Strategy Pattern** (implicit):
  * The route planning service uses different strategies for simple vs. complex routes.
  * Simple routes: Direct origin → destination (2 stops).
  * Complex routes: Optimization algorithm to group common facilities.

---

## 3.5. Database Integration - PL/SQL Functions

All database operations are performed through PL/SQL functions to meet acceptance criteria AC2:

### 3.5.1. Query Functions

| PL/SQL Function | Purpose | Return Type |
|:---------------|:--------|:-----------|
| `get_pending_freights()` | Returns all freights not assigned to trains | SYS_REFCURSOR |
| `get_all_freights()` | Returns all freights (pending + assigned) | SYS_REFCURSOR |
| `get_freight_by_id(freight_id)` | Returns specific freight details | SYS_REFCURSOR |
| `is_freight_pending(freight_id)` | Checks if freight is pending | NUMBER (1=true, 0=false) |

### 3.5.2. Assignment Procedure

| PL/SQL Procedure | Purpose | Parameters |
|:----------------|:--------|:-----------|
| `assign_multiple_freights_to_train(train_id, freight_ids)` | Assigns multiple freights to a train in single transaction | train_id: NUMBER, freight_ids: VARCHAR2 (comma-separated) |

### 3.5.3. Usage Pattern in FreightRepository

```java
// Example: Calling get_pending_freights()
String sql = "{ ? = call get_pending_freights() }";
CallableStatement stmt = connection.prepareCall(sql);
stmt.registerOutParameter(1, OracleTypes.CURSOR);
stmt.execute();
ResultSet rs = (ResultSet) stmt.getObject(1);

// Example: Calling assign_multiple_freights_to_train()
String sql = "{ call assign_multiple_freights_to_train(?, ?) }";
CallableStatement stmt = connection.prepareCall(sql);
stmt.setInt(1, trainId);
stmt.setString(2, "2001,2002,2003"); // comma-separated IDs
stmt.execute();
```

---

## 3.6. Route Optimization Algorithm (Complex Routes)

For complex routes with multiple freights, the service applies the following optimization:

1. **Collect all unique facilities** from origins and destinations of selected freights.
2. **Group operations by facility**:
   * If facility X is the origin of Freight A → mark "Load Freight A" at facility X.
   * If facility Y is the destination of Freight B → mark "Unload Freight B" at facility Y.
3. **Sort stops** to prioritize loading before unloading:
   * Stops with load operations come first.
   * Stops with only unload operations come later.
   * Within each category, sort by facility ID for consistency.
4. **Assign stop order** sequentially (1, 2, 3, ...).

**Benefit**: Reduces the total number of stops by consolidating operations at shared facilities.

**Example**:
- Freight 1: Facility A → Facility C
- Freight 2: Facility A → Facility D
- Freight 3: Facility B → Facility C

**Optimized Route**:
1. Stop 1 (Facility A): Load Freight 1, Load Freight 2
2. Stop 2 (Facility B): Load Freight 3
3. Stop 3 (Facility C): Unload Freight 1, Unload Freight 3
4. Stop 4 (Facility D): Unload Freight 2

---

## 3.7. Key Design Decisions

| Decision | Rationale |
|:---------|:----------|
| All database access via PL/SQL functions | Satisfies AC2; encapsulates business logic in database; maintains data integrity |
| Separate service for route planning | Follows Single Responsibility Principle; facilitates testing and maintenance |
| RouteStop holds both load and unload operations | Supports complex routes where a facility might serve as both origin and destination |
| Transactional assignment | Ensures atomicity - all freights assigned together or none at all |
| Optimization prioritizes loads before unloads | Ensures cargo is available before attempting to unload at later stops |
