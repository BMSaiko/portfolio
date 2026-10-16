package pt.isep.dei.esinf.controller;

import java.util.Scanner;

/**
 * Controller class responsible for handling authentication and directing to role menus
 */
public class MainMenuController {

    private final Scanner scanner;
    private static final String PASSWORD = "123456";

    public MainMenuController(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the login prompt and directs to appropriate role menu
     */
    public void show() throws Exception {
        while (true) {
            System.out.println("================================================");
            System.out.println("       Welcome to G075 SM3 PI 25/26           ");
            System.out.println("================================================");
            
            System.out.println("Please log in: (Type '0' to exit)");


            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            if (username.equals("0")) {
                System.out.println("Exiting application. Goodbye!");
                return;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            
            if (!password.equals(PASSWORD)) {
                System.out.println("X Authentication failed. Invalid password.\n");
                continue;
            }
            
            System.out.println("\nLogin successful! Welcome, " + username);


            switch (username.toLowerCase().trim()) {
                case "planner" -> new PlannerMenuController(scanner).show();
                case "traffic manager" -> new TraficDispatcherMenuController(scanner).show();
                case "freight manager" -> new FreightManagerMenuController(scanner).show();
                default -> {
                    System.out.println("X Unknown role: " + username);
                    System.out.println("Valid roles: Planner, Traffic Manager, Freight Manager\n");
                }
            }
        }
    }
}