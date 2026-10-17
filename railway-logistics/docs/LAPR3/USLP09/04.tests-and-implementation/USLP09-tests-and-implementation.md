# USLP09 - Train Assembly - Rolling Stock Selection

## 6. Integration and Demo

The Train Assembly functionality (USLP09) is implemented in the **Freight and Traffic System** (`freightMngt`) module, developed in Java.

### 6.1. Integration Points

* **Database Integration (BDDAD)**: The system relies heavily on **JDBC** connections to the **Oracle** database to perform inventory checks. It integrates with several key tables:
    * **`Facility`**: To validate the starting location input by the user.
    * **`Vehicle` (and subclasses)**: To retrieve all vehicles currently stationed at that facility.
    * **`VehicleLocation` / `Movement` tables**: To determine if a vehicle is "Parked" or "In Transit".
* **Service Layer Integration**: The `TrafficManagerMenuController` integrates directly with the `TrafficManagerService`, ensuring a separation of concerns between the User Interface (Console) and the Data Access Layer.
* **USLP10 Pre-requisite**: The output of this module (a list of available vehicles) is the direct input for the Scheduling module (USLP10), ensuring a cohesive workflow where a manager first *assembles* the train and then *schedules* it.

### 6.2. Demo Scenario

The demonstration should focus on validating the accurate retrieval and display of rolling stock availability.

1.  **Input**: The Traffic Manager launches the "Assemble Train" option and inputs a **Starting Facility ID** (e.g., `12`).
2.  **Processing**: The system queries the database to find all Locomotives and Wagons where `current_location_id = 12`.
3.  **Validation**:
    * **Scenario A (Success)**: The system finds 3 Locomotives and 5 Wagons. It displays them in a formatted list, distinguishing between those ready to use (Parked) and those arriving soon (In Transit).
    * **Scenario B (Empty)**: The user inputs a Facility ID with no vehicles. The system displays a clear message: "No available vehicles were found."
    * **Scenario C (Invalid ID)**: The user inputs a non-existent ID. The system catches the error and informs the user.
4.  **Output**: The final output is a list of `SelectableVehicle` objects displayed on the console, allowing the manager to verify which resources are available for the new train.

---

## 7. Observations

* **Data Accuracy**: The reliability of this feature depends entirely on the accuracy of the underlying SQL data. A vehicle must not appear in the list if it is currently assigned to another active train (status check).
* **Performance**: Since this is a direct database query, the system is optimized to fetch only relevant columns (ID, Type, Status) to ensure a quick response time for the user.
* **Architectural Pattern**: The implementation strictly follows the **Controller-Service-Repository** pattern (using JDBC as the repository implementation), ensuring that the UI code contains no SQL logic.
* **Extensibility**: The `SelectableVehicle` class is designed to be easily extended. If future requirements demand showing "Maintenance Status" or "Fuel Level", only the SQL query and the DTO mapping need to be updated, without breaking the rest of the application.