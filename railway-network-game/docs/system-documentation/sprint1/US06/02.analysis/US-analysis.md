# US06 - As a Player, I want to upgrade a selected station with a building

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![US06-DM](svg/US06_DM.svg)

The domain model excerpt shows the following classes and their relationships:

- **Station**: Represents a train station that can be upgraded with buildings.
- **Building**: Represents an upgrade that can be added to a station.
    - **Telegraph, Café, Customs, Post Office, Hotel, Silo, Liquid Storage** are different types of buildings.
- **Player**: Represents the user who manages stations and upgrades.
- **RailwayNetwork**: Manages all stations and their buildings.
- 
The relationships show that:
- A Station can have zero-to-many Building upgrades.
- A Player can upgrade multiple Stations.
- The RailwayNetwork aggregates all Stations.

### 2.2. Other Remarks

Upgrading a station follows these steps:

1. **Station selection** - The player selects a station from a list of built stations.
2. **Upgrade selection** - The player chooses a valid building for the station.
3. **Validation** - The system ensures that:
   - The station type supports the chosen upgrade.
   - The building has not already been added to the station.
4. **Upgrade application** - The building is added, and the station’s attributes are updated.
5. **Success confirmation** - The player receives a confirmation message.

If the upgrade is invalid (e.g., adding a Hotel to a Depot), the system will display an error message.