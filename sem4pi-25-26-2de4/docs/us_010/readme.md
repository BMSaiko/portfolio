# US010 - Domain model

## 1. Context
This task focuses on establishing the core foundation of our software architecture. Its purpose is to conceptualize and map the AlSafe business domain using Domain-Driven Design (DDD) principles. Because the AlSafe domain is complex, involving distinct areas and complex business rules, a robust Domain Model is essential to ensure the software is a valid implementation of the business needs.

### 1.1 List of issues
- **Analysis:** Review Section 3 of the project requirements to identify all entities, value objects, and aggregates.
- **Design:** Elaborate the conceptual Domain Model diagram using PlantUML.
- **Implement:** Translate the conceptual model into a PlantUML script and export the corresponding PNG file.
- **Test:** Review and validate the model to ensure it covers all specified business rules.

## 2. Requirements
**US010 - Domain model**
As Project Manager, I want the team to elaborate a Domain Model using DDD.

**Acceptance Criteria:**
- **US010.1** The team must adopt DDD as a framework for tackling complexity in the software solution.
- **US010.2** The Domain Model must accurately reflect the business needs and complex rules of the AlSafe domain.
- **US010.3** The documentation must be available in the project repository (`docs` folder) in markdown format, including both the PlantUML source file and the exported PNG diagram.

**Dependencies/References:**
- **Main References:** Section 3 (System specifications), Section 3.2 (Definitions), and Section 3.4.1 (Core Flight DSL - Conceptual Model) of `Project_Requirements_V1.pdf`.
- **Dependent US:** US011 (Aggregate justification), which relies directly on the aggregates defined in this model.

## 3. Analysis
This user story requires a deep dive into the business concepts provided by AlSafe. The main artifact is a UML Class Diagram representing the Domain Model. The team must identify the nouns and verbs in the specification to abstract them into Aggregates, Entities, and Value Objects.

Key domain concepts identified for modeling include:
- **Users and Roles:** Admin, Backoffice Operator, Air transport company collaborator, Pilot, Flight Control Operator, and Weather Person.
- **Aviation Core:** Air transport companies, Aircraft models, Engine models, and Aircraft.
- **Geography and Infrastructure:** Air control areas, Airports (IATA/ICAO codes, coordinates, altitude), Routes, and Segments.
- **Flight Execution:** Flights, Flight Plans, Legs (departure, arrival, route, segments, fuel).
- **Simulation & Environment:** Weather data , Wind direction/speed.

## 4. Design
The design consists of building a conceptual UML Class Diagram focusing on DDD concepts. We will not include database IDs or database-driven relationships; instead, we will focus on business identity and rules.

### 4.1. Realization
The realization involves collaborative brainstorming to cluster the identified concepts into **Aggregates** to ensure transactional consistency. For example, a `FlightPlan` might be an aggregate root that encapsulates `Legs`, `Segments`, and `Fuel`.

### 4.2. Acceptance Tests
This US cannot be validated by automated code tests. It is validated by inspection and domain expert review.
- **Test 1 (Manual/Inspection):** Verify DDD compliance.
    - **Refers to Acceptance Criteria:** US010.1, US010.2
    - **Description:** During a sprint review, verify if the model clearly separates Aggregates, defines Root Entities, and uses Value Objects for measurable/descriptive attributes.

## 5. Implementation
The "implementation" of this US consists of writing the PlantUML code.
- Create `domain_model.puml` in the designated `docs` folder.
- Use PlantUML syntax to define classes, stereotypes (e.g., `<<Aggregate Root>>`, `<<Value Object>>`), and associations.

## 6. Integration/Demonstration
The output of this user story serves as the blueprint for all future backend development.
- The Domain Model will be continuously referenced when implementing the Java domain layer (which is required to have 90% test coverage per NFR03).
- It serves as the direct input for US011, where the team will justify the chosen Aggregates using sequence diagrams.

## 7. Observations
- The Domain Model is not static. As the team progresses into the Flight DSL parsing (US083) or the C-based simulation (US100), new domain concepts or attributes might be discovered, requiring iterative updates to this model.
- Special attention must be paid to how the *Core Flight DSL Conceptual Model* maps to the overall Domain Model, as a flight has multiple legs, and each leg contains specific operational data.