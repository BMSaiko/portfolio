# US060 - Register an Air Transport Company

## 1. Context

This user story belongs to the organization and customer management area.

The goal is to allow a Backoffice Operator to register an air transport company in the system.

An air transport company can later be used as a customer when registering customer collaborators.

This functionality is available in the Backoffice application through the Customer Management menu.

## 1.1 List of Issues

- **Analysis:** Understand which data identifies an air transport company and which fields must be unique.
- **Design:** Define the domain entity, repository, controller, persistence implementation, UI flow, and bootstrap process.
- **Implement:** Implement company registration with validation of name, IATA code, ICAO code, and uniqueness rules.
- **Test:** Validate domain rules, duplicate code prevention, and authorization requirements.

## 2. Requirements

**US060** As a Backoffice Operator, I want to register an air transport company.

An air transport company has:

- company name;
- IATA code with exactly 2 letters;
- ICAO code with 2 or 3 letters.

The IATA and ICAO codes must be unique.

This must also be achievable by a bootstrap process.

### Acceptance Criteria

- **US060.1:** A Backoffice Operator can register an air transport company.
- **US060.2:** The company must have a non-empty company name.
- **US060.3:** The IATA code must have exactly 2 letters.
- **US060.4:** The ICAO code must have 2 or 3 letters.
- **US060.5:** IATA code must be unique.
- **US060.6:** ICAO code must be unique.
- **US060.7:** Company registration must be available through bootstrap.
- **US060.8:** The operation must require authentication and proper authorization.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Used by US061 when associating a customer collaborator with an air transport company.
- Related to the Organization aggregate from the domain model.

## 3. Analysis

### 3.1 Understanding

An air transport company represents an organization that can be a customer of the system.

The domain model identifies an air transport company with:

- name;
- IATA code;
- ICAO code.

The IATA and ICAO codes are relevant business identifiers and therefore must be unique.

The system normalizes both codes to uppercase to avoid duplicated values with different letter casing.

Example:

    tp -> TP
    tap -> TAP

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Create `AirTransportCompany` as a domain entity | It represents a relevant organization concept. |
| Use generated numeric ID internally | Simpler persistence with JPA/Hibernate. |
| Keep IATA and ICAO as unique business attributes | Both are required to be unique by the requirement. |
| Normalize codes to uppercase | Prevents duplicates caused by lowercase/uppercase differences. |
| Validate format in the constructor | Keeps invalid companies out of the domain model. |
| Use controller-level uniqueness checks | Keeps business operation explicit and easier to test. |
| Allow `ADMIN` as well as `BACKOFFICE_OPERATOR` | Existing bootstrap admin has both roles and can operate the backoffice. |

## 4. Design

### 4.1 Realization

The registration is handled by `RegisterAirTransportCompanyController`.

The UI collects:

- company name;
- IATA code;
- ICAO code.

The controller checks authentication and authorization, validates uniqueness through the repository, creates the domain object, and persists it.

### 4.2 Main Flow

    Backoffice Operator -> OrganizationMenuUI: register air transport company
    OrganizationMenuUI -> RegisterAirTransportCompanyController: registerCompany(name, iataCode, icaoCode)
    RegisterAirTransportCompanyController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    RegisterAirTransportCompanyController -> AirTransportCompanyRepository: existsByIataCode(iataCode)
    RegisterAirTransportCompanyController -> AirTransportCompanyRepository: existsByIcaoCode(icaoCode)
    RegisterAirTransportCompanyController -> AirTransportCompany: new AirTransportCompany(name, iataCode, icaoCode)
    RegisterAirTransportCompanyController -> AirTransportCompanyRepository: save(company)
    AirTransportCompanyRepository --> RegisterAirTransportCompanyController: saved company
    RegisterAirTransportCompanyController --> OrganizationMenuUI: company
    OrganizationMenuUI --> Backoffice Operator: success message

### 4.3 Alternative Flows

IATA already exists:

    Controller detects existing IATA code.
    Operation is rejected.
    User receives an error message.

ICAO already exists:

    Controller detects existing ICAO code.
    Operation is rejected.
    User receives an error message.

Invalid format:

    Domain constructor validates the code format.
    Operation is rejected.
    User receives an error message.

Unauthenticated user:

    AuthService throws SecurityException.
    Operation is rejected.

Unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.organization.domain.AirTransportCompany`
- `eapli.aisafe.organization.repositories.AirTransportCompanyRepository`
- `eapli.aisafe.organization.application.RegisterAirTransportCompanyController`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAirTransportCompanyRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.MasterUsersBootstrapper`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI`

### 5.2 Domain Rules

The `AirTransportCompany` constructor validates:

    companyName cannot be null or blank
    iataCode must match exactly 2 letters
    icaoCode must match 2 or 3 letters

The constructor also normalizes:

    iataCode = iataCode.trim().toUpperCase()
    icaoCode = icaoCode.trim().toUpperCase()

### 5.3 Repository Operations

The repository supports:

    save(company)
    findByIataCode(iataCode)
    findByIcaoCode(icaoCode)
    findAll()
    existsByIataCode(iataCode)
    existsByIcaoCode(icaoCode)

### 5.4 Persistence

The entity is persisted in table:

    T_AIR_TRANSPORT_COMPANY

Important constraints:

    IATA_CODE unique
    ICAO_CODE unique

The entity is registered in `persistence.xml`.

### 5.5 Bootstrap

The bootstrap process creates initial air transport companies.

Examples:

| Company | IATA | ICAO |
|---|---|---|
| TAP Air Portugal | TP | TAP |
| Ryanair | FR | RYR |
| EasyJet Europe | EC | EJU |

This satisfies the requirement that the operation must also be achievable by bootstrap.

## 6. Demonstration

Run the application:

    mvn clean install
    mvn -pl aisafe.app.backoffice.console exec:java

Login with:

    admin@aisafe.com
    Admin123

or:

    backoffice@aisafe.com
    Back123

Menu path:

    1. Login
    2. Customer management
    1. Register air transport company

Example input:

    Company name: Lufthansa
    IATA code: LH
    ICAO code: DLH

Expected result:

    Air transport company registered successfully.

## 7. Tests

The following aspects are tested:

- valid company creation;
- company name cannot be blank;
- IATA code must have exactly 2 letters;
- ICAO code must have 2 or 3 letters;
- invalid IATA codes are rejected;
- invalid ICAO codes are rejected;
- codes are normalized to uppercase;
- duplicate IATA code is rejected;
- duplicate ICAO code is rejected;
- unauthenticated users cannot register companies;
- only authorized roles can register companies.

## 8. Observations

The current implementation fully satisfies US060.

The air transport company is also integrated with US061, where a customer collaborator can be associated with an existing company.

Future improvements may include:

- editing air transport company data;
- disabling companies instead of deleting them;
- importing companies from an external dataset;
- replacing generated IDs with a business identity if the project later requires it.
