# USLP07 - Train Dispatch - Scheduling and Route Calculation

## 1. Requirements Engineering

### 1.1. User Story Description

As a **Freight Manager**, I want to use a scheduler that allows me to dispatch a list of trains, where each train has an associated route that can be simple or complex, so that I can efficiently plan and manage railway traffic.

### 1.2. Customer Specifications and Clarifications

The scheduler must be able to:

* Each train/route has a planned date/time to depart.
* The route to be taken by each train is defined **manually** by the Freight Manager during the dispatch phase.
* The manual definition of the path is intended to guide the Freight Manager when the automatic system (to be developed) is not available.
* The travel speed should be calculated based on the limitations of the track, the combined power of the locomotives in the train, and the weight of wagons (which can be loaded or not).
* The **crossing of trains** should be considered.
* In single lines, the crossing can be carried out when there are side lines or at the station before the crossing.
* The scheduler must indicate the **estimated times of passage** for each train at the various points along the route and the necessary **crossing operations**.
* A route can be **simple** (consists of a single freight) or **complex** (includes multiple freights).
* A freight consists of a set of wagons that must be transported from endpoint A to endpoint B.

### 1.3. Acceptance Criteria

* The system must allow manual input of the route (ordered list of stations).
* The speed calculation must consider the lowest permitted value from track restrictions and the train's traction capacity (power/weight).
* The result must be a list of passage and/or stop events along the route with their respective estimated times.
* The system must identify and report the necessary crossing points (crossing operations) to prevent collisions on single lines.
* Acceleration can be assumed to be instantaneous in this version (as per USLP03, though USLP07 is more comprehensive).

### 1.4. Found out Dependencies

* **BDDAD (Database Management)**: Requires a relational model instantiated with data on infrastructure (line segments, stations, etc.) and rolling stock (locomotives, wagons).
* **USLP03**: The capability to calculate the estimated travel time between two directly connected stations with a selected locomotive is a prerequisite or closely related calculation.
* **Domain Concepts**: Locomotives, Wagons, Line Segment (single/double track), Route, Freight, Crossing operation.

### 1.5 Input and Output Data

| Data Type | Examples/Fields | Source/Usage                                             |
| :--- | :--- |:---------------------------------------------------------|
| **Input** | Route (ordered list of stations/endpoints) | Defined by Freight Manager        |
| | Locomotives (Type, Power, Weight) | System Data/BDDAD            |
| | Wagons (Tare, Payload, Volume) | System Data/BDDAD            |
| | Line Segments (Max Speed Limit, Single/Double Track) | System Data/BDDAD             |
| | Planned Departure Date/Time | Defined by Freight Manager        |
| **Output** | Estimated Passage Times | Scheduler calculates and records  |
| | Required Crossing Operations | ]Scheduler identifies and indicates                      |
| | Calculated Speed per Segment | ]System Calculation                                      |

### 1.6. System Sequence Diagram (SSD)



The SSD illustrates the main flow for dispatching a train:
1.  **selectTrainAndRoute**: The Freight Manager selects the train, wagons, and route (manual).
2.  **calculateMaxSpeed**: The system calculates the maximum speed allowed on each segment (considering track restrictions, power, and weight).
3.  **calculateEstimatedTimes**: The system calculates the estimated passage times at each point on the route.
4.  **identifyCrossingOperations**: The system checks for conflicts on single-track lines and identifies necessary crossings.
5.  **dispatchTrain**: The scheduler records and presents the planned route, times, and crossing operations.

### 1.7 Other Relevant Remarks

* This module is a core component of the "Freight and Traffic System" (the `freightMngt` module implemented in JAVA).
* The complexity lies in accurately applying the speed calculation models (traction force vs. track constraints) and managing traffic conflicts (crossings) on single-track infrastructure.
* The manual path definition during dispatch serves as a guide, suggesting future development will include an automatic route-finding system.