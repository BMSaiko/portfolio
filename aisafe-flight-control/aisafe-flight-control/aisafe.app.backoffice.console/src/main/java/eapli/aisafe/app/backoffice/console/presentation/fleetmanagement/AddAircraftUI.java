package eapli.aisafe.app.backoffice.console.presentation.fleetmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.fleetmanagement.application.AddAircraftController;
import eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.framework.io.util.Console;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddAircraftUI extends AbstractUI {

    private final AddAircraftController controller;
    private final Scanner scanner;

    public AddAircraftUI(AddAircraftController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n--- Add Aircraft to Fleet ---");

        try {
            String registration = Console.readLine("Aircraft Registration (e.g., CS-TPA): ");

            // ==========================================
            // 1. SELEÇÃO INTERATIVA DA MARCA (MAKER)
            // ==========================================
            System.out.println("\n--- Select Maker ---");
            List<String> makers = StreamSupport.stream(controller.getAircraftMakers().spliterator(), false)
                    .collect(Collectors.toList());

            if (makers.isEmpty()) {
                System.out.println("[ERROR] No Aircraft Makers found in the database. Please register models first.");
                return false;
            }

            for (int i = 0; i < makers.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, makers.get(i));
            }
            int makerIdx = Console.readInteger("Option: ") - 1;
            String makerName = makers.get(makerIdx);

            // ==========================================
            // 2. SELEÇÃO INTERATIVA DO MODELO (DEPENDENTE)
            // ==========================================
            System.out.println("\n--- Select Model for " + makerName + " ---");
            List<AircraftModel> models = StreamSupport.stream(controller.getModelsByMaker(makerName).spliterator(), false)
                    .collect(Collectors.toList());

            for (int i = 0; i < models.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, models.get(i).identity().modelId());
            }
            int modelIdx = Console.readInteger("Option: ") - 1;
            String modelId = models.get(modelIdx).identity().modelId();

            // ==========================================
            // 3. RESTANTES DADOS DA CABINE E PAÍS
            // ==========================================
            String country = Console.readLine("\nCountry of Registration (e.g., Portugal): ");

            System.out.println("\n--- Seat Configuration ---");
            int firstClass = Console.readInteger("First Class Seats: ");
            int business = Console.readInteger("Business Class Seats: ");
            int economy = Console.readInteger("Economy Class Seats: ");

            // ==========================================
            // 4. SELEÇÃO INTERATIVA DO STATUS
            // ==========================================
            System.out.println("\n--- Select Operational Status ---");
            String[] statuses = {"ACTIVE", "MAINTENANCE", "RETIRED"};
            for (int i = 0; i < statuses.length; i++) {
                System.out.printf("%d. %s\n", i + 1, statuses[i]);
            }
            int statusIdx = Console.readInteger("Option: ") - 1;
            String status = statuses[statusIdx];

            // ==========================================
            // 5. ANO DE FABRICO
            // ==========================================
            int manufacturingYear = Console.readInteger("\nManufacturing Year (e.g., 2015): ");

            // Create the DTO
            AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                    registration, modelId, makerName, country, firstClass, business, economy, status, manufacturingYear
            );

            // Call the controller
            controller.addAircraft(dto);

            System.out.println("\n[SUCCESS] Aircraft " + registration + " successfully added to the fleet!");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("\n[ERROR] Invalid option selected from the menu.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n[BUSINESS ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[UNEXPECTED ERROR] " + e.getMessage());
        }

        return false;
    }
}