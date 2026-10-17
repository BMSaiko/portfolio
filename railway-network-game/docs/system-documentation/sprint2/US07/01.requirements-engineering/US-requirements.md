# US07 - As a Player, I want to list all the stations to select one to see its details, including the existing building and the demand/supply cargoes.
 
 ## 1. Requirements Engineering
 
 ### 1.1. User Story Description
 
 As a Player, I want to list all the stations to select one to see its
 details, including the existing building and the demand/supply cargoes.
 
 ### 1.2. Customer Specifications and Clarifications 
 
 The enunciation specifies that:
 
 - There are three different types of stations:
   1. **Depots (50k,3x3 radius)** 
   2. **Stations (100k, 4x4 radius)** 
   3. **Terminals (200k, 5x5 radius)** 
 
 - All industrial and population sites within the radius of a station send (supply) and receive (demand) business through the station.
 - By adding the supply and demand for cargoes from the industry and population in a range, the supply and demand for the station are determined.
 - When built, a station consists of just one building, which can be upgraded with other buildings that can increase the station’s performance.
 
 My interpretation is that the player can have access to all the stations, where he can select the one he wants to see the details of them the demand/supply cargoes.
 
 ### 1.3. Acceptance Criteria
 
 - It must be possible to see all three types of stations (depots, stations, and terminals).
 - It should be possible to list all the details of the cargo.
 
 ### 1.4. Found out Dependencies
 
 - **US05** - As a Player, I want to build a station. AC1: Overbuilding is not possible.
 - **US06** - As a Player, I want to upgrade a selected station with a building
 
 ### 1.5 Input and Output Data
 
 Input Data:
 - Selected station (selection from a list)
 - Station type (selection: depots, stations, or terminals)
 - Station details(view: existing building and the demand/supply cargoes)
 
 Output Data:
 - Success/failure message to list station details
 
 
 ### 1.6. System Sequence Diagram (SSD)
 
 ![US07-SSD](./svg/us07_ssd.svg)
 
 1. Player request to view stations
 2. System list all stations
 3. Player select a station
 4. System presents the selected station
 5. Player view details of selected station
 6. System present details about that station
 7. Player select the existing building
 8. System show building details
 9. Player see  loads
 10. System present loads
 11. System listing confirmation successful
 
 ### 1.7 Other Relevant Remarks
 
 The station listing functionality will be used frequently when viewing its details, including the existing building and the demand/supply cargoes.