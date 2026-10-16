package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.application.RegisterUserController;

/**
 * Console submenu for User Management operations.
 * Groups US031 (Register user), US032 (Disable/Enable users),
 * and US033 (List users) under a single menu entry.
 */
public class UsersMenuUI extends AbstractUI {

    private final RegisterUserController registerController;
    private final ManageUserController manageUserController;

    public UsersMenuUI(final RegisterUserController registerController,
                       final ManageUserController manageUserController) {
        this.registerController = registerController;
        this.manageUserController = manageUserController;
    }

    @Override
    protected boolean doShow() {
        while (true) {
            printHeader("User Management");

            System.out.println("1. List users");
            System.out.println("2. Register user");
            System.out.println("3. Enable/Disable user");
            System.out.println("0. Back");

            System.out.print("\nOption: ");
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "1":
                        new ListUsersUI(manageUserController).show();
                        pause();
                        break;
                    case "2":
                        new RegisterUserUI(registerController).show();
                        pause();
                        break;
                    case "3":
                        new ManageUserUI(manageUserController).show();
                        pause();
                        break;
                    case "0":
                        return false;
                    default:
                        System.out.println("Invalid option.");
                        pause();
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
                pause();
            }
        }
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }

    private void pause() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
