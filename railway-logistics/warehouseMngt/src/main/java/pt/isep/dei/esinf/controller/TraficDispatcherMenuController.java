package pt.isep.dei.esinf.controller;

import java.sql.SQLException;
import java.util.Scanner;

import pt.isep.dei.esinf.service.TravelTimeCalculatorService;
import pt.isep.dei.esinf.service.warehouse.TrainMovementService;
import pt.isep.dei.esinf.ui.TraficDispatcherMenu;
import java.sql.Connection;

/**
 * Controller class responsible for handling the main menu and user interactions
 */
public class TraficDispatcherMenuController {

    private final Scanner scanner;


    public TraficDispatcherMenuController(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the main menu and handles user input
     */
    public void show() throws SQLException {
        while (true) {
            new TraficDispatcherMenu().run();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> new TravelTimeCalculatorService().showMenu(scanner);
                case "2" -> new TrainSchedulerMenuController(scanner).show();
                case "3" -> new TrafficManagerMenuController(scanner).show();
                case "4" -> new TrainMovementService().showMenu(scanner);
                case "0" -> { return; }
                default -> System.out.println("Invalid option, please try again!");
            }
        }
    }
}