package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.usermanagement.application.RegisterUserController;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.app.common.console.AbstractUI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class RegisterUserUI extends AbstractUI {

    private static final List<Role> MAIN_ROLES = Arrays.asList(
            Role.ADMIN,
            Role.BACKOFFICE_OPERATOR,
            Role.COLLABORATOR,
            Role.PILOT,
            Role.WEATHER_PERSON
    );

    private static final List<Role> COLLABORATOR_SUBTYPES = Arrays.asList(
            Role.AIR_TRANSPORT_COMPANY_COLLABORATOR,
            Role.FLIGHT_CONTROL_OPERATOR
    );

    private final RegisterUserController controller;

    public RegisterUserUI(RegisterUserController controller) {
        this.controller = controller;
    }

    private void displayRoleOptions(List<Role> roles) {
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + ". " + roles.get(i));
        }
        System.out.println("0. Finish selection");
    }

    private boolean isValidRoleNumber(int option, int maxRoles) {
        return option >= 1 && option <= maxRoles;
    }

    private Optional<Integer> parseRoleInput(String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private void processRoleSelection(Role selected, Set<Role> roles) {
        if (roles.contains(selected)) {
            System.out.println("Role " + selected + " already selected. Choose another or 0 to finish.");
        } else {
            roles.add(selected);
            System.out.println("Added: " + selected);
            System.out.println("Current selected roles: " + roles);
        }
    }

    private void selectCollaboratorSubtype(Scanner sc, Set<Role> roles) {
        System.out.println("\nSelect collaborator type:");
        for (int i = 0; i < COLLABORATOR_SUBTYPES.size(); i++) {
            System.out.println((i + 1) + ". " + COLLABORATOR_SUBTYPES.get(i));
        }
        System.out.println("0. None (keep generic COLLABORATOR role)");

        while (true) {
            System.out.print("\nCollaborator type (0 to keep generic): ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            Optional<Integer> optOpt = parseRoleInput(input);
            if (optOpt.isEmpty()) {
                System.out.println("Invalid number. Please enter a valid option.");
                continue;
            }
            int opt = optOpt.get();
            if (opt == 0) {
                System.out.println("Keeping generic COLLABORATOR role.");
                return;
            }
            if (isValidRoleNumber(opt, COLLABORATOR_SUBTYPES.size())) {
                Role subType = COLLABORATOR_SUBTYPES.get(opt - 1);
                roles.remove(Role.COLLABORATOR);
                roles.add(subType);
                System.out.println("COLLABORATOR replaced with: " + subType);
                System.out.println("Current selected roles: " + roles);
                return;
            }
            System.out.println("Invalid number. Please choose between 0 and " + COLLABORATOR_SUBTYPES.size());
        }
    }

    private Set<Role> selectRoles(Scanner sc) {
        Set<Role> roles = new HashSet<>();
        System.out.println("\nSelect roles (type the number, 0 to finish):");
        displayRoleOptions(MAIN_ROLES);

        while (true) {
            System.out.print("\nRole number (0 to finish): ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            Optional<Integer> optOpt = parseRoleInput(input);
            if (optOpt.isEmpty()) {
                System.out.println("Invalid number. Please enter a valid role number.");
                continue;
            }
            int opt = optOpt.get();
            if (opt == 0) {
                break;
            }
            if (isValidRoleNumber(opt, MAIN_ROLES.size())) {
                Role selected = MAIN_ROLES.get(opt - 1);
                processRoleSelection(selected, roles);
                if (selected == Role.COLLABORATOR && roles.contains(Role.COLLABORATOR)) {
                    selectCollaboratorSubtype(sc, roles);
                }
            } else {
                System.out.println("Invalid number. Please choose between 1 and " + MAIN_ROLES.size());
            }
        }
        return roles;
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password (min 8 chars, one digit, one uppercase): ");
        String pwd = sc.nextLine();
        System.out.print("Full name: ");
        String name = sc.nextLine();
        System.out.print("Phone number: ");
        String phone = sc.nextLine();

        Set<Role> roles = selectRoles(sc);

        if (roles.isEmpty()) {
            System.out.println("No roles selected. User will have no permissions.");
        } else {
            System.out.println("Final roles: " + roles);
        }

        try {
            controller.registerUser(email, pwd, name, phone, roles);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }
}
