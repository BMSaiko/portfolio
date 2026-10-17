package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.List;

public class ListUsersUI extends AbstractUI {

    private final ManageUserController controller;

    public ListUsersUI(ManageUserController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        Iterable<User> usersIterable = controller.findAllUsers();
        List<User> users = new ArrayList<>();
        usersIterable.forEach(users::add);

        System.out.println("\n================================================================================");
        System.out.println("Backoffice Users");
        System.out.println("================================================================================");
        System.out.printf("%-30s %-20s %-15s %-20s %-8s%n",
                "Email", "Name", "Phone", "Roles", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        for (User u : users) {
            String roles = u.roles().stream()
                    .map(Role::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            String status = u.isActive() ? "ACTIVE" : "DISABLED";
            System.out.printf("%-30s %-20s %-15s %-20s %-8s%n",
                    u.email().value(),
                    truncate(u.name().value(), 20),
                    u.phoneNumber().value(),
                    truncate(roles, 20),
                    status);
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total users: %d%n%n", users.size());
        return true;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }
}
