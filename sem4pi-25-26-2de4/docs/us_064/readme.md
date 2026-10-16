# US064 - Disable a Customer's Collaborator

## 1. Context

This user story belongs to the organization and customer management area.

The goal is to allow a Backoffice Operator to disable a customer's collaborator so that the collaborator can no longer use the system.

A collaborator is also a system user. Therefore, disabling the collaborator is implemented by deactivating the associated user.

This functionality is available in the Backoffice application through the Customer Management menu.

## 1.1 List of Issues

- **Analysis:** Understand how a customer collaborator is linked to a system user and what it means to disable that collaborator.
- **Design:** Define whether the collaborator should be deleted or deactivated.
- **Implement:** Implement collaborator disabling by deactivating the associated system user.
- **Test:** Validate that disabled collaborators cannot login and are no longer listed as active collaborators.

## 2. Requirements

**US064** As a Backoffice Operator, I want to disable a customer's collaborator so that he may not use the system anymore.

### Acceptance Criteria

- **US064.1:** A Backoffice Operator can disable a customer's collaborator.
- **US064.2:** The collaborator must no longer be able to use the system.
- **US064.3:** The associated system user must become inactive.
- **US064.4:** The disabled collaborator must not be listed as an active collaborator.
- **US064.5:** The operation must require authentication and proper authorization.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US061 because a collaborator must exist before it can be disabled.
- Depends on US062 because the list operation displays the collaborator ID needed for disabling.
- Related to US063 because both use the collaborator's associated system user.

## 3. Analysis

### 3.1 Understanding

A customer collaborator is connected to a system user through the user's email.

The user story says that the collaborator should not be able to use the system anymore.

In the current system, users can only login if they are active.

The authentication flow uses:

    findActiveByEmail(email)

Therefore, disabling a collaborator can be achieved by deactivating the associated user.

The collaborator association is not deleted. This keeps the relationship available for history and avoids losing information.

The collaborator ID required for disabling can be obtained through US062, because the collaborator listing now displays the ID.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Deactivate the associated user | Prevents login and system usage. |
| Do not delete the collaborator association | Preserves historical data. |
| Do not delete the system user | Keeps user information available while blocking access. |
| Use existing `User.deactivate()` method | Reuses existing user domain behavior. |
| Filter disabled collaborators in US062 | Ensures disabled users are not shown as active collaborators. |
| Use US062 to obtain collaborator ID | Avoids forcing the operator to guess internal identifiers. |

## 4. Design

### 4.1 Realization

The operation is handled by `CustomerCollaboratorController`.

The UI asks for the collaborator ID and confirmation.

The collaborator ID is obtained from the collaborator listing screen.

The controller finds the collaborator, finds the associated user, deactivates the user, and saves it.

### 4.2 Main Flow

    Backoffice Operator -> ListCustomerCollaboratorsUI: list customer's collaborators
    ListCustomerCollaboratorsUI --> Backoffice Operator: displays collaborator ID

    Backoffice Operator -> DisableCustomerCollaboratorUI: disable customer's collaborator
    DisableCustomerCollaboratorUI -> Backoffice Operator: ask collaborator ID
    Backoffice Operator -> DisableCustomerCollaboratorUI: enter collaborator ID
    DisableCustomerCollaboratorUI -> Backoffice Operator: ask confirmation
    Backoffice Operator -> DisableCustomerCollaboratorUI: confirm
    DisableCustomerCollaboratorUI -> CustomerCollaboratorController: disableCollaborator(id)
    CustomerCollaboratorController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: findById(id)
    CustomerCollaboratorController -> UserRepository: findByEmail(collaborator.userEmail)
    CustomerCollaboratorController -> User: deactivate()
    CustomerCollaboratorController -> UserRepository: save(user)
    DisableCustomerCollaboratorUI --> Backoffice Operator: success message

### 4.3 Alternative Flows

Operation cancelled:

    If the Backoffice Operator does not confirm with Y, the operation is cancelled.

Collaborator not found:

    The controller rejects the operation.

Associated user not found:

    The controller rejects the operation.

Unauthenticated or unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.organization.domain.CustomerCollaborator`
- `eapli.aisafe.organization.application.CustomerCollaboratorController`
- `eapli.aisafe.organization.repositories.CustomerCollaboratorRepository`
- `eapli.aisafe.usermanagement.domain.User`
- `eapli.aisafe.usermanagement.repositories.UserRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaCustomerCollaboratorRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaUserRepository`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.ListCustomerCollaboratorsUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.DisableCustomerCollaboratorUI`

### 5.2 User Domain Support

The `User` class already supports activation status:

    activate()
    deactivate()
    isActive()

The disable operation uses:

    user.deactivate()

### 5.3 Authentication Impact

The login process only searches active users:

    findActiveByEmail(email)

This means that after deactivation, the collaborator can no longer login.

### 5.4 Listing Impact

US062 lists collaborators through active-user filtering.

The controller checks:

    findActiveByEmail(email)

This means that after deactivation, the collaborator no longer appears in active collaborator lists.

### 5.5 Controller Operation

The main controller method is:

    disableCollaborator(Long collaboratorId)

This method:

1. checks authentication and authorization;
2. finds the collaborator by ID;
3. finds the associated user by email;
4. deactivates the user;
5. saves the user.

## 6. Demonstration

Run the application:

    ./scripts/run-backoffice.sh

Login with:

    admin@aisafe.com
    Admin123

First list collaborators to obtain the collaborator ID.

Menu path:

    1. Login
    2. Customer management
    3. List customer's collaborators

Example output:

    Active collaborators:
    ----------------------------------------------------------------------------------------------------
    ID    Email                            Name                      Phone           Position
    ----------------------------------------------------------------------------------------------------
    4     collab.lufthansa@aisafe.com      Lufthansa Collaborator    +351910000001   Operations Manager

Then disable the collaborator.

Menu path:

    2. Customer management
    5. Disable customer's collaborator

Example input:

    Collaborator ID: 4
    Confirm disable collaborator? Y

Expected result:

    Customer collaborator disabled successfully.

To verify the operation:

1. Try to login with the disabled collaborator credentials.
2. List the collaborators of the same customer using US062.

Expected verification result:

    The disabled collaborator cannot login.
    The disabled collaborator is not listed as an active collaborator.

## 7. Tests

The following aspects are tested:

- disabling an existing collaborator;
- disabled collaborator cannot login;
- disabled collaborator is not listed in US062;
- rejecting unknown collaborator;
- requiring authentication;
- requiring authorization;
- preserving the collaborator association while deactivating the user.

## 8. Observations

The current implementation satisfies US064.

The operation disables access by deactivating the associated system user instead of deleting data.

This is safer because the system keeps historical information and avoids losing the relationship between the customer and the collaborator.

The collaborator ID is now visible through US062, so the Backoffice Operator no longer needs to guess or know internal IDs manually.

Future improvements may include:

- showing collaborator details before disabling;
- adding a reason for disabling;
- adding a reactivation use case;
- listing disabled collaborators in an administrative report.
