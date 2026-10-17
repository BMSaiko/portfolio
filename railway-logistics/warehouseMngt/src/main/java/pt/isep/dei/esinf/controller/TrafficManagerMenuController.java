
package pt.isep.dei.esinf.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.train.SelectableVehicle;
import pt.isep.dei.esinf.service.TrafficManagerService;
import pt.isep.dei.esinf.ui.TrafficManagerMenu;

// Não herda de MainMenuController para evitar erros de compilação
public class TrafficManagerMenuController {

    private final TrafficManagerService trafficService;
    private final Scanner scanner;

    public TrafficManagerMenuController(Scanner scanner) {
        // Inicializa o serviço que contém a lógica de BD para a USLP09
        this.trafficService = new TrafficManagerService();
        this.scanner = scanner;
    }

    // Renomeado para show() para consistência com outros controladores
    public void show() {
        TrafficManagerMenu menu = new TrafficManagerMenu();
        String option;

        try {
            trafficService.connect();

            do {
                menu.run();
                option = scanner.nextLine(); // Leitura da opção

                switch (option) {
                    case "1":
                        // Executa a USLP09
                        assembleTrainUI();
                        break;
                    case "0":
                        System.out.println("Returning to the previous menu...");
                        break;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } while (!option.equals("0"));

        } catch (SQLException e) {
            System.err.println("\nDATABASE ERROR: Unable to complete the operation.");
            e.printStackTrace();
        } finally {
            trafficService.disconnect();
        }
    }

    private void assembleTrainUI() throws SQLException {
        System.out.print("\nEnter the STARTING FACILITY ID for the NEW ROUTE: ");
        String startId = scanner.nextLine().trim();

        try {
            // --- 1. CHAMAR O SERVIÇO E ARMAZENAR A LISTA ---
            List<SelectableVehicle> result =
                    trafficService.getSelectableVehiclesForAssembly(startId);

            // --- 2. PRINT HEADER ---
            System.out.println("\n=======================================================");
            System.out.printf("AVAILABLE VEHICLES (Ordered for Route %s)\n", startId);
            System.out.println("=======================================================");

            // --- 3. IMPRIMIR OS RESULTADOS OU MENSAGEM DE VAZIO ---
            if (result.isEmpty()) {
                System.out.println("No available vehicles were found in the database.");
            } else {
                // Itera e usa o método toString() do SelectableVehicle
                for (SelectableVehicle v : result) {
                    System.out.println(v);
                }
            }
            // -----------------------------------------------------------------

        } catch (NumberFormatException e) {
            System.err.println("\nInvalid Facility ID. It must be an integer.");
        }

        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}