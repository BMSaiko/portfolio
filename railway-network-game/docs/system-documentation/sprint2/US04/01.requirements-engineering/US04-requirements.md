# US04 - Create a Scenario for a Selected Map

## 1. Requirements Engineering

### 1.1. User Story Description

As an Editor, I want to create a scenario for a selected map.

### 1.2. Customer Specifications and Clarifications 

**From the specifications document:**

> Scenarios represent specific historical periods and geographical regions. Each scenario is defined for a specific map, with a start and end date, and includes technological restrictions (available locomotive types and industries) and historical events that impact gameplay.

**From the client clarifications:**

> **Question:** What information is required when creating a scenario?
>
> **Answer:** Each scenario must have a name, be tied to a specific map, have a defined time period (start and end dates), and include technological restrictions and at least one historical event.

> **Question:** Are the rates of production and quantity produce from industries decided in the scenario's specifications?
>
> **Answer:** The rates are predefined values but can be redefined in the scenario.


### 1.3. Acceptance Criteria

* **AC01:** Definition of the behavior of ports, which cargoes they import/export and/or transform.
* **AC02:** Definition of the available locomotion types (steam, diesel, and/or electric).
* **AC03:** Re)Definition of the factors that alter the generation (frequency) of generating industries.

### 1.4. Found out Dependencies

* There is a dependency on having maps already created in the system, as scenarios are created for existing maps.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
    * scenario name
    * start time (date)
    * end time (date)
    * historical events
    
* Selected data:
    * map
    * available locomotive types
    * available industry types 

**Output Data:**

* List of existing maps
* List of all locomotive types
* List of all industry types
* Success of the operation

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US04-SD.svg)

**_Other alternatives might exist._**

### 1.7 Other Relevant Remarks

* Created scenarios become available for players to select when starting a new game.
* Historical events provide context and challenges that affect gameplay.