package pt.isep.dei.esinf.controller;

import pt.isep.dei.esinf.service.warehouse.TrainMovementService;

import java.util.Scanner;

public class TrainMovementController {
    private final Scanner scanner;
    private final TrainMovementService service;

    public TrainMovementController(Scanner scanner) {
        this.scanner = scanner;
        this.service = new TrainMovementService();
    }

    public void show() {
        service.showMenu(scanner);
    }
}
