package pt.isep.dei.esinf;

import java.util.Scanner;

import pt.isep.dei.esinf.controller.MainMenuController;

/**
 * Main application entry point
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            new MainMenuController(scanner).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
