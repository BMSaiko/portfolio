# US11 - Design

## Sequence Diagram

![SD US11](svg/US11-SD.svg)

## Class Diagram

![CD US11](svg/US11-CD.svg)

## Applied Patterns

* **Model-View-Controller (MVC)**
  * TrainController handles the request to list trains
  * TrainService contains the business logic
  * Train represents the domain model
  * TrainView handles the display of trains

* **Repository Pattern**
  * TrainRepository manages data access for trains
  * Provides abstraction over the data storage mechanism

* **Iterator Pattern**
  * Used to traverse the collection of trains
  * Helps in grouping and sorting operations

* **Strategy Pattern**
  * Applied for different sorting strategies
  * Allows for flexible sorting implementation

## Design Decisions and Alternatives

1. **Grouping Strategy**
   * Decision: Group trains by locomotive type in memory
   * Alternative: Group in database query
   * Rationale: In-memory grouping provides more flexibility and easier maintenance

2. **Sorting Implementation**
   * Decision: Use built-in sorting with custom comparator
   * Alternative: Custom sorting algorithm
   * Rationale: Built-in sorting is efficient and well-tested

3. **Data Transfer**
   * Decision: Use DTOs for train display
   * Alternative: Direct entity usage
   * Rationale: DTOs provide better control over displayed data and performance

4. **View Rendering**
   * Decision: Text-based grouped display
   * Alternative: Table format
   * Rationale: Grouped display better represents the hierarchical nature of the data 