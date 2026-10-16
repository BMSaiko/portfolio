# US01 - As an Editor, I want to create a map with a size and a name.

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for... | Answer  | Justification (with patterns)  |
|:-------------  |:--------------------- |:------------|:---------------------------- |
| Step 1  		 | ...interacting with the editor? | CreateMapUI | Pure Fabrication: UI class handles user interaction |
| Step 2  		 | ...coordinating the US? | MapController | Controller Pattern: coordinates the use case |
| Step 3  		 | ...creating the DTO? | MapController | Creator Pattern: controller creates the DTO to pass to service |
| Step 4  		 | ...validating map name? | MapService | Information Expert: service knows about existing maps |
| Step 5  		 | ...validating map size? | GameMap | Information Expert: domain model knows its size constraints |
| Step 6  		 | ...creating the map domain object? | MapService | Creator Pattern: service creates domain objects |
| Step 7  		 | ...saving the map? | MapRepository | Repository Pattern: responsible for persistence |
| Step 8  		 | ...converting domain to DTO? | MapService | Information Expert: service knows both domain and DTO structures |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* GameMap (Domain Model)
* MapDTO (Data Transfer Object)

Other software classes (i.e. Pure Fabrication) identified:

* CreateMapUI  
* MapController
* MapService
* MapRepository
* Response

### Design Patterns Applied

* **DTO Pattern**: MapDTO is used to transfer data between layers without exposing domain model details
* **Repository Pattern**: MapRepository handles data access and persistence
* **Controller Pattern**: MapController handles the coordination of the use case
* **Service Layer Pattern**: MapService contains business logic and conversion between domain and DTO
* **Domain Model Pattern**: GameMap represents the core business entity

## 3.2. Sequence Diagram (SD)

The sequence diagram illustrates the interaction flow for creating a map:

1. **Editor** opens the map creation interface.
2. **Editor** inputs the map name, width, and height.
3. **UI** calls the MapController's createMap method.
4. **Controller** creates a MapDTO with the provided parameters.
5. **Controller** calls the MapService's createMap method with the MapDTO.
6. **Service** validates the map name for uniqueness via MapRepository.
7. If the name is not unique, an exception is thrown and error message displayed.
8. If the name is unique, **Service** creates a new GameMap domain object.
9. **Service** saves the GameMap via the MapRepository.
10. **Service** converts the GameMap back to a MapDTO and returns it.
11. **Controller** returns the MapDTO to the UI.
12. **UI** displays the success message to the editor.

## 3.3. Class Diagram (CD)

The class diagram shows the relationships between the refactored classes:

![US01-CD](./svg/US01_CD.svg)