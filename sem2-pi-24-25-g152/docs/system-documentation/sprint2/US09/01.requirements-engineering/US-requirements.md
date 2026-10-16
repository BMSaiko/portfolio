# US09 - As a Player, I want to buy a train.

## 1. Requirements Engineering

### 1.1. User Story Description

As a Player, I want to buy a train

### 1.2. Customer Specifications and Clarifications

From the specifications document:

The player can purchase a train composed of a locomotive and carriages.
Locomotives have different characteristics:

- Steam, Diesel, or Electric (Electric requires electrified tracks).
- Power, acceleration, top speed, acquisition cost, operational cost, and lifespan.
- Available locomotives depend on the scenario's time period.

Carriages are distinguished by the type of cargo they transport, such as:

- Passengers, Mail, Coal, Iron Ore, Steel, Cars, etc.
- Carriages do not have an acquisition cost.


### 1.3. Acceptance Criteria

- **AC1:** Display only era-appropriate locomotives 

- **AC2:** Validate electrified track availability for electric locomotives

- **AC3:** Enforce minimum 1 carriage selection

- **AC4:** Realtime funds verification 

- **AC5:** Generate unique train ID upon purchase

### 1.4. Found out Dependencies

- There is a dependency on "US08 - Build a Railway Line" since trains require a railway network to operate.

### 1.5. Input and Output Data

Input Data:
- Selected locomotive from available options.
- Selected carriages

Output Data:
- Confirmation message with train details
- Updated player funds


### 1.6. System Sequence Diagram (SSD)

![US09-SSD](svg/US09_SSD.svg)

1. Player requests available locomotives 
2. Player selects composition 

- Funds sufficiency

- Track compatibility (for electric)

- Minimum carriages

3. System creates train entity 

### 1.7. Other Relevant Marks

Purchasing trains is essential for railway network operation. Players must strategically choose locomotives and carriages based on their transport needs. 