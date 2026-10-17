# US11 - As a Player, I want to list all trains.

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to list all trains.

### 1.2. Customer Specifications and Clarifications 

The enunciado specifies that:

- The display of trains must include the locomotive and the current cargoes.
- The trains should be grouped by locomotive type and then listed alphabetically by name.

This means that when a player requests to see all trains, they should get a well-organized view that shows:
1. Each train's locomotive information
2. The current cargoes being transported by each train
3. A grouping structure based on locomotive types
4. Alphabetical sorting within each locomotive type group

### 1.3. Acceptance Criteria

- The system must display all trains in the game
- For each train, the following information must be shown:
  - Locomotive type
  - Train name
  - Current cargoes being transported
- Trains must be grouped by their locomotive type
- Within each locomotive type group, trains must be listed alphabetically by name
- The display should be clear and easy to read

### 1.4. Found out Dependencies

- **US09** - As a Player, I want to buy a train (needed to have trains to list)
- **US10** - As a Player, I want to assign a selected train to a route with a valid station and the respective list of cargoes to be picked up (needed to show current cargoes and routes)

### 1.5 Input and Output Data

Input Data:
- None (the system will automatically fetch all trains)

Output Data:
- List of trains grouped by locomotive type, where each train entry contains:
  - Train name
  - Locomotive type
  - Current cargoes
  - Current route (if assigned)
- The list should be alphabetically sorted within each locomotive type group

### 1.6. System Sequence Diagram (SSD)

![US11-SSD](./svg/US11_SSD.svg)

1. Player requests to list all trains
2. System retrieves all trains from the database
3. System groups trains by locomotive type
4. System sorts trains alphabetically within each group
5. System displays the organized list of trains

### 1.7 Other Relevant Remarks

This functionality will be frequently used by players to monitor their trains and their cargo status. The grouping and sorting requirements make it easier for players to find specific trains and understand their fleet organization. 