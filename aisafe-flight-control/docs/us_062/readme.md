# US062 - List Customer's Collaborators

## 1. Context

This user story belongs to the organization and customer management area.

The goal is to allow a Backoffice Operator to list all active collaborators associated with a given customer.

A customer can be:

- an air transport company;
- an air control area.

Disabled collaborators must not be shown.

This functionality is available in the Backoffice application through the Customer Management menu.

## 1.1 List of Issues

- **Analysis:** Understand how collaborators are associated with customers and how disabled collaborators should be filtered.
- **Design:** Define the query flow using the customer type and customer code.
- **Implement:** Implement a controller method that returns only active collaborator associations.
- **Test:** Validate that active collaborators are listed and disabled collaborators are excluded.

## 2. Requirements

**US062** As a Backoffice Operator, I want to list all collaborators of a given customer.

The list should not include disabled collaborators.

### Acceptance Criteria

- **US062.1:** A Backoffice Operator can choose a customer.
- **US062.2:** The customer can be an air transport company.
- **US062.3:** The customer can be an air control area.
- **US062.4:** The system lists collaborators associated with that customer.
- **US062.5:** Disabled collaborators are not listed.
- **US062.6:** The operation must require authentication and proper authorization.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US061 because collaborators must exist before they can be listed.
- Depends on US060 for listing air transport companies.
- Depends on the Air Control Area module for listing air control areas.
- Related to US063 and US064.

## 3. Analysis

### 3.1 Understanding

A customer collaborator is stored as an association between:

- customer type;
- customer code;
- user email;
- position.

The collaborator's personal information is stored in the associated `User`.

Therefore, the listing process first finds the collaborator associations for the selected customer and then loads the corresponding active users.

A collaborator is considered disabled when the associated user is inactive.

For this reason, the implementation uses:

    findActiveByEmail(email)

This guarantees that inactive users are not included in the result.

The listing also displays the collaborator ID, because US063 and US064 require the Backoffice Operator to select a collaborator by ID.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| List collaborator records with user details | The collaborator ID is needed for edit and disable operations. |
| Use `findActiveByEmail` | Automatically excludes disabled collaborators. |
| Select customer before listing | Keeps the flow aligned with the requirement. |
| Reuse company listing for air transport companies | Avoids manual typing and improves usability. |
| List real air control areas | Integrates with the Air Control Area module and avoids manual code entry. |
| Show ID, email, name, phone and position | Gives the operator enough information to use US063 and US064. |

## 4. Design

### 4.1 Realization

The listing is handled by `CustomerCollaboratorController`.

The UI asks the Backoffice Operator to select:

- customer type;
- customer.

The controller returns active customer collaborator records.

For each collaborator, the UI loads the associated user and displays:

- collaborator ID;
- email;
- name;
- phone number;
- position.

### 4.2 Main Flow

    Backoffice Operator -> ListCustomerCollaboratorsUI: list customer's collaborators
    ListCustomerCollaboratorsUI -> Backoffice Operator: ask customer type
    Backoffice Operator -> ListCustomerCollaboratorsUI: select customer type

    alt air transport company
        ListCustomerCollaboratorsUI -> CustomerCollaboratorController: airTransportCompanies()
        CustomerCollaboratorController -> AirTransportCompanyRepository: findAll()
        ListCustomerCollaboratorsUI --> Backoffice Operator: list air transport companies
    else air control area
        ListCustomerCollaboratorsUI -> CustomerCollaboratorController: airControlAreas()
        CustomerCollaboratorController -> AirControlAreaRepository: findAll()
        ListCustomerCollaboratorsUI --> Backoffice Operator: list air control areas
    end

    Backoffice Operator -> ListCustomerCollaboratorsUI: select customer
    ListCustomerCollaboratorsUI -> CustomerCollaboratorController: listActiveCustomerCollaborators(customerType, customerCode)
    CustomerCollaboratorController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: findByCustomer(customerType, customerCode)
    loop for each collaborator
        CustomerCollaboratorController -> UserRepository: findActiveByEmail(collaborator.userEmail)
    end
    CustomerCollaboratorController --> ListCustomerCollaboratorsUI: active collaborators
    loop for each collaborator
        ListCustomerCollaboratorsUI -> CustomerCollaboratorController: userByEmail(email)
        CustomerCollaboratorController -> UserRepository: findByEmail(email)
    end
    ListCustomerCollaboratorsUI --> Backoffice Operator: display collaborators

### 4.3 Alternative Flows

No collaborators found:

    The system shows that no active collaborators were found for the selected customer.

Disabled collaborators:

    The collaborator association may exist.
    However, the associated user is inactive.
    The user is not returned by findActiveByEmail.
    The collaborator is not displayed.

Unauthenticated or unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.

Invalid customer selection:

    UI rejects the invalid option.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.organization.domain.CustomerCollaborator`
- `eapli.aisafe.organization.domain.CustomerType`
- `eapli.aisafe.organization.application.CustomerCollaboratorController`
- `eapli.aisafe.organization.repositories.CustomerCollaboratorRepository`
- `eapli.aisafe.usermanagement.repositories.UserRepository`
- `eapli.aisafe.usermanagement.domain.User`
- `eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaCustomerCollaboratorRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaUserRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaAirControlAreaRepository`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.ListCustomerCollaboratorsUI`

### 5.2 Controller Operations

The controller supports two listing methods:

    listActiveCollaborators(CustomerType customerType, String customerCode)

This returns active users associated with the selected customer.

    listActiveCustomerCollaborators(CustomerType customerType, String customerCode)

This returns active collaborator records associated with the selected customer.

The second method is used by the UI because it includes the collaborator ID.

The controller also provides:

    userByEmail(String email)

This allows the UI to display the user information associated with each collaborator.

### 5.3 UI Output

The UI displays:

- collaborator ID;
- email;
- name;
- phone number;
- position.

Example output:

    Active collaborators:
    ----------------------------------------------------------------------------------------------------
    ID    Email                            Name                      Phone           Position
    ----------------------------------------------------------------------------------------------------
    4     collab.lufthansa@aisafe.com      Lufthansa Collaborator    +351910000001   Operations Manager

The ID can then be used in:

- US063 - Edit a customer's collaborator;
- US064 - Disable a customer's collaborator.

## 6. Demonstration

Run the application:

    ./scripts/run-backoffice.sh

Login with:

    admin@aisafe.com
    Admin123

Menu path:

    1. Login
    2. Customer management
    3. List customer's collaborators

Example input for air transport company:

    Customer type: 1
    Select company: 1

Example input for air control area:

    Customer type: 2
    Select air control area: 1

Expected result if collaborators exist:

    Active collaborators:
    ----------------------------------------------------------------------------------------------------
    ID    Email                            Name                      Phone           Position
    ----------------------------------------------------------------------------------------------------
    4     collab.lufthansa@aisafe.com      Lufthansa Collaborator    +351910000001   Operations Manager

Expected result if no active collaborators exist:

    No active collaborators found for this customer.

## 7. Tests

The following aspects are tested:

- active collaborators are listed for a selected air transport company;
- active collaborators are listed for a selected air control area;
- collaborators from other customers are not listed;
- disabled collaborators are not listed;
- empty result is handled correctly;
- unauthenticated users cannot list collaborators;
- only authorized users can list collaborators;
- registered air control areas can be listed by the controller.

## 8. Observations

The current implementation satisfies US062.

The previous limitation where air control areas had to be typed manually was removed.

The UI now lists real air control areas from the Air Control Area repository.

The listing also shows the collaborator ID, solving the usability problem where the operator could not know which ID to use for edit or disable operations.

Future improvements may include:

- sorting collaborators by name or email;
- adding search by email;
- adding pagination if the number of collaborators grows.
