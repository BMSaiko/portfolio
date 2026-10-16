# US11 - List All Trains - Design

## 3.1. Rationale

| Interaction ID | Question: Which class is responsible for... | Answer  | Justification (with patterns)  |
|:-------------|:--------------------|:------------|:---------------------------- |
| Step 1      | ...interacting with the player? | ListTrainsUI | Pure Fabrication: UI class handles user interaction |
| Step 2      | ...coordinating the US? | TrainController | Controller Pattern: coordinates the use case |
| Step 3      | ...getting all trains? | TrainService | Service Layer Pattern: contains business logic |
| Step 4      | ...grouping and sorting trains? | TrainService | Information Expert: service has the necessary information |
| Step 5      | ...converting Train to TrainDTO? | TrainService | Information Expert: service knows both domain and DTO structures |
| Step 6      | ...creating a response object? | TrainController | Creator Pattern: controller creates response objects |
| Step 7      | ...displaying train information? | ListTrainsUI | Pure Fabrication: UI class handles display to user |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* Train (Domain Model)
* Locomotive (Domain Model)
* TrainDTO (Data Transfer Object)
* CargoDTO (Data Transfer Object)
* RouteDTO (Data Transfer Object)

Other software classes (i.e. Pure Fabrication) identified:

* ListTrainsUI
* TrainController
* TrainService
* TrainRepository
* Response

### Design Patterns Applied

* **DTO Pattern**: TrainDTO, CargoDTO, and RouteDTO transfer data between layers without exposing domain model details
* **Repository Pattern**: TrainRepository handles data access and persistence
* **Controller Pattern**: TrainController handles the coordination of the use case
* **Service Layer Pattern**: TrainService contains business logic and conversion between domain and DTO
* **Response Pattern**: Generic Response<T> class for standardized API responses
* **Domain Model Pattern**: Train represents the core business entity

## 3.2. Sequence Diagram

The sequence diagram illustrates the interaction flow for listing all trains:

![SD US11](svg/US11-SD.svg)

## 3.3. Class Diagram

The class diagram shows the relationships between the refactored classes:

![CD US11](svg/US11-CD.svg)

## 3.4. Design Decisions and Alternatives

1. **DTO Implementation**
   * Decision: Use dedicated DTOs for each domain entity
   * Alternative: Use simple Map<String, Object> for data transfer
   * Rationale: Typed DTOs provide better type safety, maintainability, and clarity

2. **Grouping Strategy**
   * Decision: Group trains by locomotive type in memory
   * Alternative: Group in database query
   * Rationale: In-memory grouping provides more flexibility and easier maintenance

3. **Response Pattern**
   * Decision: Use generic Response<T> class to wrap all controller responses
   * Alternative: Return raw DTOs or specialized response types
   * Rationale: Standard response structure improves API consistency and error handling

4. **Service vs. Mapper**
   * Decision: Embed mapping logic in the service
   * Alternative: Use dedicated mapper classes
   * Rationale: For this use case, embedded mapping is simpler; dedicated mappers could be introduced for more complex scenarios