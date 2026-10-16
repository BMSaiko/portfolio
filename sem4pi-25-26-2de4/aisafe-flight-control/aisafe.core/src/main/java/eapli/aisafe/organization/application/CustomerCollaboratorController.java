package eapli.aisafe.organization.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.generic.domain.PhoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerCollaboratorController {

    private final AuthService auth;
    private final UserRepository userRepo;
    private final AirTransportCompanyRepository companyRepo;
    private final AirControlAreaRepository airControlAreaRepo;
    private final CustomerCollaboratorRepository collaboratorRepo;

    public CustomerCollaboratorController(AuthService auth, UserRepository userRepo,
                                          AirTransportCompanyRepository companyRepo,
                                          AirControlAreaRepository airControlAreaRepo,
                                          CustomerCollaboratorRepository collaboratorRepo) {
        this.auth = auth;
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.airControlAreaRepo = airControlAreaRepo;
        this.collaboratorRepo = collaboratorRepo;
    }

    public List<User> availableCollaboratorUsers(CustomerType customerType) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        Role required = collaboratorRoleFor(customerType);
        List<User> result = new ArrayList<>();
        for (User u : userRepo.findActiveByRole(required)) {
            if (collaboratorRepo.findByUserEmail(u.email()).isEmpty()) result.add(u);
        }
        return result;
    }

    public Iterable<AirTransportCompany> airTransportCompanies() {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return companyRepo.findAll();
    }

    public Iterable<AirControlArea> airControlAreas() {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return airControlAreaRepo.findAll();
    }



    public void addExistingUserAsCollaborator(CustomerType customerType,
                                              String customerCode,
                                              String userEmail,
                                              String position) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        Email email = new Email(userEmail);
        Role required = collaboratorRoleFor(customerType);
        User user = userRepo.findActiveByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Active user not found"));
        if (!user.hasRole(required))
            throw new IllegalArgumentException("User does not have required role");
        if (collaboratorRepo.findByUserEmail(email).isPresent())
            throw new IllegalArgumentException("User already a collaborator");
        String code = customerCode.trim().toUpperCase();
        validateCustomerExists(customerType, code);
        collaboratorRepo.save(new CustomerCollaborator(customerType, code, email, position));
    }

    public List<User> listActiveCollaborators(CustomerType customerType, String customerCode) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        List<User> result = new ArrayList<>();
        String code = customerCode.trim().toUpperCase();
        for (CustomerCollaborator c : collaboratorRepo.findByCustomer(customerType, code))
            userRepo.findActiveByEmail(c.userEmail()).ifPresent(result::add);
        return result;
    }

    public List<CustomerCollaborator> listActiveCustomerCollaborators(CustomerType t, String code) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        List<CustomerCollaborator> result = new ArrayList<>();
        String c = code.trim().toUpperCase();
        for (CustomerCollaborator col : collaboratorRepo.findByCustomer(t, c))
            if (userRepo.findActiveByEmail(col.userEmail()).isPresent()) result.add(col);
        return result;
    }

    public User userByEmail(String email) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return userRepo.findByEmail(new Email(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void editCollaborator(Long id, String newEmailStr, String newPhoneNumber) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        CustomerCollaborator collab = collaboratorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collaborator not found"));
        User current = userRepo.findByEmail(collab.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("System user not found"));
        Email newEmail = new Email(newEmailStr);
        if (newEmail.equals(current.email())) {
            current.changePhoneNumber(PhoneNumber.of(newPhoneNumber));
            userRepo.save(current);
            return;
        }
        if (userRepo.existsByEmail(newEmail))
            throw new IllegalArgumentException("New email already in use");
        User updated = current.copyWithNewEmailAndPhone(newEmail, PhoneNumber.of(newPhoneNumber));
        userRepo.delete(current);
        userRepo.save(updated);
        collab.changeUserEmail(newEmail);
        collaboratorRepo.save(collab);
    }

    public void disableCollaborator(Long id) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        CustomerCollaborator collab = collaboratorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collaborator not found"));
        User user = userRepo.findByEmail(collab.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("System user not found"));
        user.deactivate();
        userRepo.save(user);
    }

    private Role collaboratorRoleFor(CustomerType type) {
        if (type == null) throw new IllegalArgumentException("Customer type mandatory");
        if (type == CustomerType.AIR_TRANSPORT_COMPANY) return Role.AIR_TRANSPORT_COMPANY_COLLABORATOR;
        if (type == CustomerType.AIR_CONTROL_AREA) return Role.FLIGHT_CONTROL_OPERATOR;
        throw new IllegalArgumentException("Unsupported customer type");
    }

    private void validateCustomerExists(CustomerType type, String code) {
        if (code == null || code.trim().isEmpty())
            throw new IllegalArgumentException("Customer code mandatory");
        if (type == CustomerType.AIR_TRANSPORT_COMPANY && companyRepo.findByIcaoCode(code).isEmpty())
            throw new IllegalArgumentException("Air transport company not found");
        if (type == CustomerType.AIR_CONTROL_AREA
                && airControlAreaRepo.findByCode(AirControlAreaCode.of(code.trim().toUpperCase())).isEmpty())
            throw new IllegalArgumentException("Air control area not found");
    }
}
