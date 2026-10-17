# US12 - Generate Cargoes Automatically at Stations

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt 

![US12-DM](svg/US12-DM.svg)

The domain model shows the following classes and their relationships:

* **Cargo**: Represents goods that need transportation
  - Has a type (based on industry)
  - Has quantity/volume
  - Has origin and destination stations
  - Has value and priority

* **CargoType**: Classifies different kinds of cargo
  - Associated with specific industries
  - Has handling requirements
  - Has base value per unit

* **Station**: (From US05)
  - Has cargo storage capacity
  - Connected to cities and/or industries
  - Acts as origin/destination for cargo
  - Has a type that may affect cargo generation

* **CargoSimulator**: Core component for this US
  - Manages automatic cargo generation
  - Runs at configurable intervals
  - Applies generation rules and algorithms
  - Creates cargo based on network configuration

* **Industry**: Produces and consumes specific cargo types
  - Has production rates for output cargo
  - Has consumption needs for input cargo
  - May have seasonal variations
  - Located near stations

* **City**: Creates demand for various goods
  - Has population affecting demand volume
  - Has type (industrial, residential, etc.)
  - Creates passenger transport needs
  - Located near stations

* **RailwayLine**: (From US08)
  - Connects stations in the network
  - Enables cargo routing between stations
  - Distance affects transport cost and time

* **Scenario**: (From US04)
  - Defines game context and time period
  - Contains economic parameters
  - Controls technology availability

* **Player**:
  - Benefits from cargo transportation
  - Must satisfy cargo demand for profit
  - Builds railway infrastructure

### 2.2. Other Remarks

1. Cargo generation simulation should consider:
   - Network topology (connected stations)
   - Economic balance (value vs. distance)
   - Game difficulty adjustment
   - Strategic decision-making incentives

2. Implementation considerations:
   - Background processing for simulation
   - Configurable generation parameters
   - Clear visualization of generated cargo
   - Efficient algorithms for large networks

3. Technical validations required:
   - Station storage capacity checks
   - Valid origin-destination pairs
   - Appropriate cargo type assignment
   - Balanced distribution across the network