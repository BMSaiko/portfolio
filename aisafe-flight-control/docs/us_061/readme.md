# US061 - Add a Customer's Collaborator

## 1. Context

This user story belongs to the organization and customer management area.

The goal is to allow a Backoffice Operator to associate a system user with a customer.

A customer can be:

- an air transport company;
- an air control area.

The collaborator is also a user of the system. Therefore, each collaborator must be associated with a distinct system user.

This functionality is available in the Backoffice application through the Customer Management menu.

## 1.1 List of Issues

- **Analysis:** Understand how a customer collaborator relates to users, air transport companies, and air control areas.
- **Design:** Define the customer collaborator entity, customer type enum, repositories, controller logic, UI flow, and bootstrap process.
- **Implement:** Allow a Backoffice Operator to select an existing user with role `COLLABORATOR` and associate that user with a real customer.
- **Test:** Validate collaborator creation, distinct user rule, customer association, authorization, and bootstrap support.

## 2. Requirements

**US061** As a Backoffice Operator, I want to register a customer's collaborator.

A customer may be:

- an air transport company;
- an air control area.

The collaborator will also be a user of the system.

Each collaborator must be a distinct system user.

There is no need to verify that the collaborator's email is in the customer's domain.

This must also be achievable by a bootstrap process.

### Acceptance Criteria

- **US061.1:** A Backoffice Operator can register a customer's collaborator.
- **US061.2:** The collaborator must be an existing system user.
- **US061.3:** The selected user must have role `COLLABORATOR`.
- **US061.4:** Each collaborator must be associated with a distinct user.
- **US061.5:** The customer can be an air transport company.
- **US061.6:** The customer can be an air control area.
- **US061.7:** The system must not validate the user's email domain against the customer domain.
- **US061.8:** Collaborator registration must be available through bootstrap.
- **US061.9:** The operation must require authentication and proper authorization.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on user registration functionality to create users with role `COLLABORATOR`.
- Depends on US060 for listing and selecting air transport companies.
- Depends on the Air Control Area module for listing and validating air control areas.
- Related to US062, US063, and US064.

## 3. Analysis

### 3.1 Understanding

The collaborator is not just a text record. It must be connected to a system user.

The final implementation follows this approach:

1. A user is first created in the system with role `COLLABORATOR`.
2. The Backoffice Operator opens Customer Management.
3. The system lists active users with role `COLLABORATOR` that are not yet associated with a customer.
4. The Backoffice Operator selects one of those users.
5. The Backoffice Operator selects the customer type.
6. If the customer is an air transport company, the system lists existing air transport companies.
7. If the customer is an air control area, the system lists existing air control areas.
8. The Backoffice Operator selects the customer.
9. The Backoffice Operator enters the collaborator position.
10. The system creates the customer collaborator association.

This approach respects the rule that each collaborator is a distinct system user and avoids creating duplicate users inside the collaborator registration flow.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Use existing users with role `COLLABORATOR` | Ensures the collaborator is a system user before being associated with a customer. |
| Only list active users | Disabled users should not be assigned to customers. |
| Exclude already associated users | Guarantees that each collaborator is a distinct system user. |
| Store customer type and customer code | Allows the same collaborator model to support companies and air control areas. |
| Do not validate email domain | Requirement explicitly states that this validation is not necessary. |
| List existing air transport companies | Improves UI usability and avoids typing wrong company codes. |
| List existing air control areas | Integrates with the real Air Control Area module and avoids manual code entry. |
| Validate selected customers through repositories | Prevents collaborators from being associated with non-existing customers. |

## 4. Design

### 4.1 Realization

The operation is handled by `CustomerCollaboratorController`.

The UI first asks the controller for available users with role `COLLABORATOR`.

Then the UI asks the Backoffice Operator to choose:

- user;
- customer type;
- customer;
- position.

Finally, the controller validates the operation and saves the customer collaborator.

### 4.2 Main Flow

    Backoffice Operator -> OrganizationMenuUI: add customer's collaborator
    OrganizationMenuUI -> CustomerCollaboratorController: availableCollaboratorUsers()
    CustomerCollaboratorController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    CustomerCollaboratorController -> UserRepository: findActiveByRole(COLLABORATOR)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: findByUserEmail(email)
    CustomerCollaboratorController --> OrganizationMenuUI: available users
    OrganizationMenuUI --> Backoffice Operator: list available users
    Backoffice Operator -> OrganizationMenuUI: select user
    OrganizationMenuUI --> Backoffice Operator: ask customer type
    Backoffice Operator -> OrganizationMenuUI: select customer type

    alt air transport company
        OrganizationMenuUI -> CustomerCollaboratorController: airTransportCompanies()
        CustomerCollaboratorController -> AirTransportCompanyRepository: findAll()
        OrganizationMenuUI --> Backoffice Operator: list air transport companies
    else air control area
        OrganizationMenuUI -> CustomerCollaboratorController: airControlAreas()
        CustomerCollaboratorController -> AirControlAreaRepository: findAll()
        OrganizationMenuUI --> Backoffice Operator: list air control areas
    end

    Backoffice Operator -> OrganizationMenuUI: select customer
    OrganizationMenuUI --> Backoffice Operator: ask position
    Backoffice Operator -> OrganizationMenuUI: enter position
    OrganizationMenuUI -> CustomerCollaboratorController: addExistingUserAsCollaborator(type, code, email, position)
    CustomerCollaboratorController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    CustomerCollaboratorController -> UserRepository: findActiveByEmail(email)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: findByUserEmail(email)
    CustomerCollaboratorController -> AirTransportCompanyRepository/AirControlAreaRepository: validate customer exists
    CustomerCollaboratorController -> CustomerCollaborator: new CustomerCollaborator(type, code, email, position)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: save(collaborator)
    OrganizationMenuUI --> Backoffice Operator: success message

### 4.3 Alternative Flows

No available users:

    The system shows that there are no available users with role COLLABORATOR.
    The Backoffice Operator must create a user with role COLLABORATOR first.

Selected user is inactive:

    The controller rejects the operation.

Selected user does not have role COLLABORATOR:

    The controller rejects the operation.

Selected user is already associated with a customer:

    The controller rejects the operation.

Air transport company does not exist:

    The controller rejects the operation.

Air control area does not exist:

    The controller rejects the operation.

Unauthenticated or unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.organization.domain.CustomerCollaborator`
- `eapli.aisafe.organization.domain.CustomerType`
- `eapli.aisafe.organization.application.CustomerCollaboratorController`
- `eapli.aisafe.organization.repositories.CustomerCollaboratorRepository`
- `eapli.aisafe.organization.repositories.AirTransportCompanyRepository`
- `eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository`
- `eapli.aisafe.aircontrolmanagement.domain.AirControlArea`
- `eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode`
- `eapli.aisafe.usermanagement.repositories.UserRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaCustomerCollaboratorRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaAirTransportCompanyRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaAirControlAreaRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaUserRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.MasterUsersBootstrapper`
- `eapli.aisafe.bootstrap.AirControlAreaBootstrap`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.AddCustomerCollaboratorUI`

### 5.2 Domain Model

`CustomerCollaborator` stores:

    id
    customerType
    customerCode
    userEmail
    position

`CustomerType` supports:

    AIR_TRANSPORT_COMPANY
    AIR_CONTROL_AREA

For an air transport company, the stored customer code is the company ICAO code.

For an air control area, the stored customer code is the air control area code.

### 5.3 Repository Operations

`CustomerCollaboratorRepository` supports:

    save(collaborator)
    findById(id)
    findByUserEmail(email)
    findByCustomer(customerType, customerCode)
    findAll()

`UserRepository` supports:

    findActiveByRole(role)
    findActiveByEmail(email)
    findByEmail(email)

`AirTransportCompanyRepository` supports:

    findAll()
    findByIcaoCode(icaoCode)

`AirControlAreaRepository` supports:

    findAll()
    findByCode(areaCode)

### 5.4 Controller Operations

The main controller methods are:

    availableCollaboratorUsers()
    airTransportCompanies()
    airControlAreas()
    addExistingUserAsCollaborator(customerType, customerCode, userEmail, position)

`availableCollaboratorUsers()` returns only users that:

- are active;
- have role `COLLABORATOR`;
- are not already associated with a customer.

`addExistingUserAsCollaborator(...)` validates:

- authentication;
- authorization;
- active user existence;
- role `COLLABORATOR`;
- distinct user rule;
- customer existence for air transport companies;
- customer existence for air control areas;
- non-empty position.

### 5.5 Persistence

The collaborator is persisted in table:

    T_CUSTOMER_COLLABORATOR

The field `USER_EMAIL` is unique to guarantee that one user cannot be associated as multiple customer collaborators.

### 5.6 Bootstrap

The bootstrap process creates sample collaborators and associates them with initial customers.

Bootstrap collaborators now use role:

    COLLABORATOR

This keeps bootstrap data aligned with the UI rule that only users with role `COLLABORATOR` can be selected as customer collaborators.

## 6. Demonstration

Run the application:

    ./scripts/run-backoffice.sh

Login with:

    backoffice@aisafe.com
    Back1234

First create a user with role `COLLABORATOR`.

Menu path:

    1. Login
    1. Register user

Example user:

    Email: collab.lufthansa@aisafe.com
    Password: Collab123
    Full name: Lufthansa Collaborator
    Phone number: +351910000001
    Role: COLLABORATOR

Then associate that user with a customer.

Menu path:

    2. Customer management
    2. Add customer's collaborator

Example input for air transport company:

    Select user: 1
    Customer type: 1
    Select company: 1
    Position: Operations Manager

Example input for air control area:

    Select user: 1
    Customer type: 2
    Select air control area: 1
    Position: Flight Control Contact

Expected result:

    Customer collaborator added successfully.

## 7. Tests

The following aspects are tested:

- listing available users with role `COLLABORATOR`;
- excluding users without role `COLLABORATOR`;
- excluding users already associated with a customer;
- adding a collaborator to an air transport company;
- adding a collaborator to an air control area;
- rejecting inactive users;
- rejecting users without role `COLLABORATOR`;
- rejecting users already associated with a customer;
- rejecting unknown air transport companies;
- rejecting unknown air control areas;
- requiring authentication;
- requiring authorization.

## 8. Observations

The current implementation satisfies US061.

The previous temporary solution where the air control area code was manually typed was replaced by integration with the real Air Control Area module.

The UI now lists existing air control areas, allowing the Backoffice Operator to select a valid area instead of typing a code manually.

Future improvements may include:

- displaying more customer details before confirmation;
- adding a dedicated collaborator details screen;
- supporting collaborator search by email or customer;
- improving pagination if the number of users/customers grows.
