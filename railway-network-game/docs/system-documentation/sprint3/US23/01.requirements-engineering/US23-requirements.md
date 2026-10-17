# US23 - Save a Simulation (Game) in a file for later use

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to Save a Simulation (Game) in a file for later use.

### 1.2. Customer Specifications and Clarifications 

**From the specifications document:**

> Players should be able to save their current game state at any point during gameplay, allowing them to return to that exact point in the simulation later. This includes preserving all game data such as player progress, train positions, cargo loads, financial status, and simulation time.

**From the client clarifications:**

> **Question:** What information needs to be saved in the game file?
>
> **Answer:** The save file must include the complete game state: player data, scenario information, all trains with their current positions and cargo, station statuses, financial information, and the current simulation time.

> **Question:** What file format should be used for saving games?
>
> **Answer:** The system should use a human-readable format like JSON or XML to allow for easier debugging and potential manual editing if needed.

> **Question:** Should there be multiple save slots or overwrite functionality?
>
> **Answer:** The system should support multiple save files with descriptive names chosen by the player.

### 1.3. Acceptance Criteria

* **AC01:** The system must save all relevant game state data to a file.
* **AC02:** The system must allow the player to specify a filename for the save file.
* **AC03:** The system must validate that the save operation completed successfully.
* **AC04:** Save files must be stored in a dedicated saves directory.
* **AC05:** The system must handle file I/O errors gracefully and inform the user.

### 1.4. Found out Dependencies

* There is a dependency on having an active game session to save.
* The system must have file system write permissions.
* All domain objects must be serializable.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
    * save file name
    
* System data:
    * current game state
    * player data
    * scenario information
    * train positions and cargo
    * station statuses
    * financial information
    * simulation time

**Output Data:**

* Confirmation of successful save operation
* Save file location
* Error messages if save fails

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US23-SD.svg)

**_Other alternatives might exist._**

### 1.7 Other Relevant Remarks

* Save files should include metadata such as save date/time and player name.
* The system should prevent overwriting existing files without user confirmation.
* Save operations should be atomic to prevent corrupted save files.
