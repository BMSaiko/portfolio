package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManageUserUI extends AbstractUI {

    private final ManageUserController controller;

    public ManageUserUI(ManageUserController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;

        Iterable<User> usersIterable = controller.findAllUsers();
        List<User> users = new ArrayList<>();
        usersIterable.forEach(users::add);

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return true;
        }

        System.out.println("\nUsers:");
        System.out.printf("%-3s %-30s %-20s %-10s%n", "#", "Email", "Name", "Status");
        System.out.println("----------------------------------------------------------------------");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            String status = u.isActive() ? "ACTIVE" : "DISABLED";
            System.out.printf("%-3d %-30s %-20s %-10s%n", i + 1, u.email().value(), u.name(), status);
        }

        System.out.print("\nSelect user number (0 to cancel): ");
        int option;
        try {
            option = Integer.parseInt(sc.nextLine().trim());
            if (option == 0) {
                return true;
            }
            if (option < 1 || option > users.size()) {
                System.out.println("Invalid number.");
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return true;
        }

        User selected = users.get(option - 1);
        System.out.println("\nSelected: " + selected.email().value() + " (" + selected.name() + ")");
        System.out.println("Current status: " + (selected.isActive() ? "ACTIVE" : "DISABLED"));

        if (selected.isActive()) {
            System.out.print("Do you want to DISABLE this user? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if ("y".equals(confirm) || "yes".equals(confirm)) {
                try {
                    controller.disableUser(selected.email().value());
                    System.out.println("User disabled successfully.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Operation cancelled.");
            }
        } else {
            System.out.print("Do you want to ENABLE this user? (y/n): ");
            String confirm = sc.nextLine().trim().toLowerCase();
            if ("y".equals(confirm) || "yes".equals(confirm)) {
                try {
                    controller.enableUser(selected.email().value());
                    System.out.println("User enabled successfully.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Operation cancelled.");
            }
        }

        return true;
    }
}