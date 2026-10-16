# US072 - List an air transport company's fleet

## 1. Context

This user story belongs to the fleet management area.

The goal is to allow an Air Transport Company Collaborator to list their company's aircraft fleet. This includes the base listing and specific filtering capabilities by model, maker, capacity, and age.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis**: Understand the fleet attributes and the specific filtering criteria (model, maker, capacity, age).
- **Design**: Define the controller logic, repository queries for filtered listings, and the TCP communication flow for remote data retrieval.
- **Implement**: Implement the listing logic, the various filters, and the DTO mapping for network transfer.
- **Test**: Validate that filters return correct results, ensure users only see their own company's fleet, and verify authorization requirements.

## 2. Requirements

US072 As an Air Transport Company Collaborator, I want to list my company's aircraft fleet.

This story includes several variations:
- US072a: List fleet by model.
- US072b: List fleet by maker.
- US072c: List fleet by capacity (number of passengers).
- US072d: List fleet by age.

### Acceptance Criteria

- US072.1: An Air Transport Company Collaborator can list all aircraft in their company's fleet.
- US072.2: The system must support filtering the list by model, maker, capacity, and age.
- US072.3: The list must display key details (registration, model, maker, capacity, and status).
- US072.4: The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), without direct interaction with the database.
- US072.5: The operation must be authenticated and authorized appropriately.
- US072.6: The user can only view aircraft belonging to their own company.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US070 (Add an aircraft) to have aircraft available in the fleet.
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

Listing the fleet involves retrieving `Aircraft` entities associated with the authenticated user's company.

Filtering criteria:
- Model: Match aircraft with a specific Aircraft Model ID or name.
- Maker: Match aircraft where the Aircraft Model's manufacturer matches the input.
- Capacity: Match aircraft where the total seat configuration (sum of seats in all classes) equals or falls within a specified range.
- Age: Match aircraft based on their registration date or year.

Because this is a remote application, the server must process the filters and send a list of DTOs (Data Transfer Objects) back to the client via TCP.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Use DTOs for Listing | Essential for TCP communication to avoid lazy loading issues and minimize data transfer size. |
| Server-side Filtering | Reduces the amount of data sent over the network by only transmitting relevant records. |
| Enforce Company Ownership | The query must always include the company ID to prevent data leakage between different airlines. |
| Use specific Repository methods | Optimized queries for maker, model, and capacity filters. |

## 4. Design

### 4.1 Realization

The process is handled by ListFleetController on the server side.

The UI/Client:
- Allows the user to choose the listing type (All, by Model, by Maker, by Capacity, by Age).
- Collects filter parameters if necessary.
- Sends the listing request to the server over TCP.

The Server:
- Receives the request and parameters over TCP.
- Identifies the collaborator's company.
- Calls the appropriate repository method based on the filter.
- Converts the resulting Aircraft list into a list of AircraftDTOs.
- Returns the list via TCP.

### 4.2 Main Flow

    Air Transport Company App -> FleetMenuUI: list fleet
    FleetMenuUI -> Air Transport Company Collaborator: asks for filter type
    Air Transport Company Collaborator -> FleetMenuUI: selects filter (e.g., by Maker)
    FleetMenuUI -> TcpClient: sendRequest(LIST_FLEET_BY_MAKER, makerName)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> ListFleetController: listByMaker(makerName)
    ListFleetController -> AuthService: getAuthenticatedUserCompany()
    ListFleetController -> AircraftRepository: findByCompanyAndMaker(company, makerName)
    ListFleetController -> DTOTransformer: convertToDTOList(aircraftList)
    ListFleetController --> TcpServer: DTO list
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> FleetMenuUI: display list
    FleetMenuUI --> Air Transport Company Collaborator: shows filtered fleet table

### 4.3 Alternative Flows

No aircraft found:
Repository returns an empty list.
Server returns an empty list response.
User receives a message: "No aircraft found matching the criteria."

Unauthorized user:
AuthService throws SecurityException.
Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- eapli.aisafe.fleet.domain.Aircraft
- eapli.aisafe.fleet.repositories.AircraftRepository
- eapli.aisafe.fleet.application.ListFleetController
- eapli.aisafe.fleet.dto.AircraftDTO

#### aisafe.persistence.impl

- eapli.aisafe.persistence.impl.jpa.JpaAircraftRepository (Specific methods for filtering)

#### aisafe.app.company.console (TCP Client)

- eapli.aisafe.app.company.console.presentation.fleet.ListFleetUI
- eapli.aisafe.app.company.console.connection.TcpClient

#### aisafe.app.server (TCP Server)

- eapli.aisafe.app.server.handlers.ListFleetRequestHandler

### 5.2 Repository Operations

The repository must support:

    findAllByCompany(company)
    findByCompanyAndModel(company, model)
    findByCompanyAndMaker(company, maker)
    findByCompanyAndCapacity(company, capacity)
    findByCompanyAndAge(company, age)

## 6. Demonstration

Run the server application:

    mvn clean install
    mvn -pl aisafe.app.server exec:java

Run the Company App Client in a separate terminal:

    mvn -pl aisafe.app.company.console exec:java

Login with:
collaborator@tap.pt
Collab123

Menu path:
1. Login
2. Fleet management
3. List fleet
4. Select filter (e.g., "By Maker")
5. Input maker: Airbus

Expected result:
A table showing all Airbus aircraft registered for TAP Air Portugal.

## 7. Tests

The following aspects are tested:

- successfully listing all aircraft in a company's fleet;
- verifying that the "Model" filter only returns aircraft of that specific model;
- verifying that the "Maker" filter correctly filters by manufacturer;
- verifying that the "Capacity" filter returns aircraft with the specified passenger count;
- ensuring a collaborator cannot see aircraft from another company;
- verifying the TCP client correctly displays the list returned by the server;
- ensuring empty results are handled gracefully.

## 8. Observations

The current implementation fully satisfies US072 and its sub-stories.

This listing functionality is essential for management and provides the basis for selecting aircraft in subsequent operations, such as decommissioning (US071) and creating flight plans (US080).