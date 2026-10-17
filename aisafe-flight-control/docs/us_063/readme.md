# US063 - Edit a Customer's Collaborator

## 1. Context

This user story belongs to the organization and customer management area.

The goal is to allow a Backoffice Operator to edit limited information of a customer's collaborator.

According to the requirement, only the collaborator's email and phone number can be changed.

All other information must remain unchanged.

This functionality is available in the Backoffice application through the Customer Management menu.

## 1.1 List of Issues

- **Analysis:** Understand which collaborator information can be edited and which data must remain unchanged.
- **Design:** Define how to update a collaborator when the user email is also the identifier of the system user.
- **Implement:** Implement editing of email and phone number while preserving name, role, customer, position, and other data.
- **Test:** Validate that editable fields are changed and non-editable fields remain unchanged.

## 2. Requirements

**US063** As a Backoffice Operator, I want to edit the information of a customer's collaborator.

The editable information is:

- email;
- phone number.

All other information cannot be changed.

### Acceptance Criteria

- **US063.1:** A Backoffice Operator can edit a customer's collaborator.
- **US063.2:** The collaborator email can be changed.
- **US063.3:** The collaborator phone number can be changed.
- **US063.4:** The collaborator name cannot be changed.
- **US063.5:** The collaborator roles cannot be changed.
- **US063.6:** The collaborator customer cannot be changed.
- **US063.7:** The collaborator position cannot be changed.
- **US063.8:** The new email must not already belong to another user.
- **US063.9:** The operation must require authentication and proper authorization.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US061 because a collaborator must exist before it can be edited.
- Depends on US062 because the list operation displays the collaborator ID needed for editing.
- Related to US064 because disabled users should no longer access the system.

## 3. Analysis

### 3.1 Understanding

A customer collaborator is linked to a system user through the user's email.

In the current domain model, `Email` is the identifier of the `User`.

This means changing the email is not just changing a normal attribute. Since the email is the primary identity of the user, changing it requires special handling.

The implementation supports two cases:

1. The new email is the same as the current email.
2. The new email is different from the current email.

If the email is the same, only the phone number is updated.

If the email is different, the system creates a copy of the user with the new email and phone number, deletes the old user, saves the new user, and updates the collaborator association to reference the new email.

The collaborator ID required for editing can be obtained through US062, because the collaborator listing now displays the ID.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Only edit email and phone number | Matches the user story requirement. |
| Keep name unchanged | The requirement says all other information cannot be changed. |
| Keep roles unchanged | Prevents collaborator editing from becoming user role management. |
| Keep customer unchanged | Moving a collaborator to another customer is not part of this US. |
| Keep position unchanged | The requirement only allows email and phone number changes. |
| Reject duplicated email | Email identifies a user and must remain unique. |
| Copy user when email changes | Email is the user identifier, so direct mutation is not appropriate in the current model. |
| Use US062 to obtain collaborator ID | Avoids forcing the operator to guess internal identifiers. |

## 4. Design

### 4.1 Realization

The edit operation is handled by `CustomerCollaboratorController`.

The UI asks for:

- collaborator ID;
- new email;
- new phone number.

The collaborator ID is obtained from the collaborator listing screen.

The controller validates authorization, finds the collaborator, finds the associated user, and applies the update.

### 4.2 Main Flow

    Backoffice Operator -> ListCustomerCollaboratorsUI: list customer's collaborators
    ListCustomerCollaboratorsUI --> Backoffice Operator: displays collaborator ID

    Backoffice Operator -> EditCustomerCollaboratorUI: edit customer's collaborator
    EditCustomerCollaboratorUI -> Backoffice Operator: ask collaborator ID
    Backoffice Operator -> EditCustomerCollaboratorUI: enter collaborator ID
    EditCustomerCollaboratorUI -> Backoffice Operator: ask new email
    Backoffice Operator -> EditCustomerCollaboratorUI: enter new email
    EditCustomerCollaboratorUI -> Backoffice Operator: ask new phone number
    Backoffice Operator -> EditCustomerCollaboratorUI: enter new phone number
    EditCustomerCollaboratorUI -> CustomerCollaboratorController: editCollaborator(id, newEmail, newPhone)
    CustomerCollaboratorController -> AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
    CustomerCollaboratorController -> CustomerCollaboratorRepository: findById(id)
    CustomerCollaboratorController -> UserRepository: findByEmail(currentEmail)

    alt same email
        CustomerCollaboratorController -> User: changePhoneNumber(newPhone)
        CustomerCollaboratorController -> UserRepository: save(user)
    else different email
        CustomerCollaboratorController -> UserRepository: existsByEmail(newEmail)
        CustomerCollaboratorController -> User: copyWithNewEmailAndPhone(newEmail, newPhone)
        CustomerCollaboratorController -> UserRepository: delete(oldUser)
        CustomerCollaboratorController -> UserRepository: save(newUser)
        CustomerCollaboratorController -> CustomerCollaborator: changeUserEmail(newEmail)
        CustomerCollaboratorController -> CustomerCollaboratorRepository: save(collaborator)
    end

    EditCustomerCollaboratorUI --> Backoffice Operator: success message

### 4.3 Alternative Flows

Collaborator not found:

    The controller rejects the operation.

Associated user not found:

    The controller rejects the operation.

New email is invalid:

    The Email value object rejects the operation.

New email already belongs to another user:

    The controller rejects the operation.

New phone number is blank:

    The User domain object rejects the operation.

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
- `eapli.aisafe.usermanagement.domain.Email`
- `eapli.aisafe.usermanagement.repositories.UserRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaCustomerCollaboratorRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaUserRepository`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.ListCustomerCollaboratorsUI`
- `eapli.aisafe.app.backoffice.console.presentation.organization.EditCustomerCollaboratorUI`

### 5.2 User Domain Support

The `User` class supports phone updates through:

    changePhoneNumber(newPhoneNumber)

The `User` class supports copying a user with a new email through:

    copyWithNewEmailAndPhone(newEmail, newPhoneNumber)

This keeps unchanged fields such as:

- name;
- password hash;
- active status;
- roles;
- security clearance data;
- skills assessment data.

### 5.3 Customer Collaborator Support

The `CustomerCollaborator` class supports changing the user email reference through:

    changeUserEmail(newEmail)

This is used only after the system user has been recreated with the new email.

### 5.4 Controller Operation

The main controller method is:

    editCollaborator(Long collaboratorId, String newEmailStr, String newPhoneNumber)

This method:

1. checks authentication and authorization;
2. finds the collaborator;
3. finds the associated user;
4. validates the new email;
5. checks whether the new email is already used;
6. updates the phone number if email is unchanged;
7. recreates the user and updates the collaborator email reference if email changed;
8. saves the changes.

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

Then edit the collaborator.

Menu path:

    2. Customer management
    4. Edit customer's collaborator

Example input:

    Collaborator ID: 4
    New email: new.collab@aisafe.com
    New phone number: +351900000099

Expected result:

    Customer collaborator updated successfully.

To verify the change, list the customer's collaborators again using US062.

Expected updated output:

    ID    Email                            Name                      Phone           Position
    4     new.collab@aisafe.com            Lufthansa Collaborator    +351900000099   Operations Manager

The name, role, customer and position remain unchanged.

## 7. Tests

The following aspects are tested:

- editing email and phone number;
- editing only phone number when email remains the same;
- keeping the name unchanged;
- keeping the roles unchanged;
- keeping the position unchanged;
- keeping the customer type unchanged;
- keeping the customer code unchanged;
- rejecting duplicated email;
- rejecting unknown collaborator;
- requiring authentication;
- requiring authorization.

## 8. Observations

The current implementation satisfies US063.

The implementation is adapted to the current domain model, where `Email` is the identifier of the `User`.

The collaborator ID is now visible through US062, so the Backoffice Operator no longer needs to guess or know internal IDs manually.

If the project later changes `User` to have a generated ID instead of email as the primary identifier, this operation could become simpler because email would be a normal editable field.

Future improvements may include:

- showing collaborator details before editing;
- asking confirmation before changing email;
- adding a collaborator search screen;
- replacing collaborator ID input with a selectable collaborator list.
