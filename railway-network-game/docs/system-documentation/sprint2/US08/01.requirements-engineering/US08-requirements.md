# US08 - Build a Railway Line Between Two Stations

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to build a railway line between two stations to establish rail connections for cargo and passenger
transportation.

### 1.2. Customer Specifications and Clarifications

**From the client clarifications:**

> **Question:** Does the type of station affect the type of railway lines that can be built?
>
> **Answer:** No.
> The line can be single or double; it can also be electrified or not; the distance between two stations S1 and S2,
> located at positions (x1,y1) and (x2,y2) respectively, can be entered, but the distance cannot be less than the
> Euclidean distance between the coordinates of the stations.


### 1.3. Acceptance Criteria

* **AC01:** The player should choose the stations from a list of registered stations.

### 1.4. Found out Dependencies

* Requires stations to be built first (dependency on US05 - Build a Station)
* Depends on the map creation (US01) as railway lines are built on the map
* May depend on terrain and elevation features defined in the map

### 1.5 Input and Output Data

**Input Data:**

* Selected data:
    * Starting station
    * Ending station
    * Path points for the railway line (if custom routing is supported)

**Output Data:**

* Preview of the railway line path
* Construction cost estimate
* Success/failure message
* Updated map showing the new railway connection

### 1.6. System Sequence Diagram (SSD)

![US08-SSD](svg/US08-SD.svg)

### 1.7 Other Relevant Remarks

* Railway construction is a core gameplay mechanic that enables the transportation network
* The cost of construction should be carefully balanced for gameplay purposes
* Future enhancements might include:
    * Different types of tracks (single/double track)
    * Track upgrades for higher speed limits
    * Maintenance costs over time
    * Bridge and tunnel construction for challenging terrain