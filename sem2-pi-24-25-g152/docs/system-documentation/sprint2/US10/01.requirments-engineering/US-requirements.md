# US10 -  As a Player, I want to assign a selected train to a route with a valid station and the respective list of cargoes to be picked up.
 
 ## 1. Requirements Engineering
 
 ### 1.1. User Story Description
 
As a Player, I want to assign a selected train to a route with a valid station and the respective list of cargoes to be picked up.
 
 ### 1.2. Customer Specifications and Clarifications 
 
 The enunciation specifies that:
 
   1. In the simulator, the player/user purchases trains within the available budget and can put the train into service on a specific route. 
   2. A route is a list of stations where the train passes, along which it carries (wagons) cargo at each station.
 
 
 
 ### 1.3. Acceptance Criteria
 
 - It must be possible the player must be able to select an available train.
 - It must be possible the player select a valid route.
 - It must be possible the system must check whether the loads can be transported by the train.
 
 ### 1.4. Found out Dependencies
 
 - **US08** - As a Player, I want to build a railway line between two stations.
 - **US09** - As a Player, I want to buy a train. AC1: The player should choose the locomotive from a list of available locomotives for the scenario as well as a current date.
 
 ### 1.5 Input and Output Data
 
 Input Data:
 - Select train
 - Select route
 - Select station
 - Select load list
 
 Output Data:
 - Success/failure message to list station details
 - Validation Confirmation
 
 
 ### 1.6. System Sequence Diagram (SSD)
 
 ![US10-SSD](./svg/us10_ssd.svg)
 
1. The Player selects a train. The system validates this selection.

2. The Player selects a route. The system checks if the route is valid.

3. The Player selects a station. The system validates the station.

4. The Player selects a list of cargo. The system checks if it is allowed.

5. The train is assigned to the route, and the game state is updated.

6. The system sends a confirmation to the Player.
 
 ### 1.7 Other Relevant Remarks
 
The system must ensure that the train assignment to the route is saved correctly in the database so that it is correctly validated by the same so that the next step is carried out correctly to avoid conflicts later.
 