# US05 - As a Player, I want to build a station

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![US05-DM](./svg/US05_DM.svg)

The domain model excerpt shows the following classes and their relationships:
- **Map**: Represents the game world where stations can be placed.
- **Station**: Represents a train station that serves cities and industries. It has different types:
  - **Depot**: Small station with a limited economic radius.
  - **Station**: Medium-sized station with a larger economic radius.
  - **Terminal**: Large station with the widest economic radius.
- **Location**: Represents the XY coordinates where an industry is placed.
- **Player**: Represents the user who manages and builds stations.
- **RailwayNetwork**: Manages all stations and their interactions with cities and industries.

The relationships show that:
- A Map contains multiple Stations.
- Each Station has exactly one Location.
- A Player can build multiple Stations.
- The RailwayNetwork manages all existing Stations.

### 2.2. Other Remarks

Stations are essential components of the railway network, allowing cities and industries to generate and receive cargo. They serve as hubs for transporting resources and passengers between locations.

The process of building a station involves:
1. **Map selection** - The system ensures a valid map is selected.
2. **XY coordinate validation** - The system checks if the selected coordinates fall within the map boundaries.
3. **Space availability check** - The system verifies if there is enough space and prevents overlapping with existing stations.
4. **Station type selection** - The player chooses between Depot, Station, or Terminal, each with different costs and coverage areas.
5. **Successful placement** - The system confirms the station has been added to the map and updates the railway network.

Since stations are permanent once built, future expansions may include station upgrades, demolitions, or additional features such as warehouses and maintenance facilities.