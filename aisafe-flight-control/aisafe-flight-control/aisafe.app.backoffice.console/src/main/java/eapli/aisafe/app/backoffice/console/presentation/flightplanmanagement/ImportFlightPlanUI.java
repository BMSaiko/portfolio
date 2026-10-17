package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.framework.io.util.Console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Console UI for importing flight plans from DSL files.
 */
public class ImportFlightPlanUI extends AbstractUI {

    private static final Path DSL_EXAMPLES_DIR = Paths.get(
            "aisafe.core", "src", "test", "resources");

    private final FlightPlanService flightPlanService;

    public ImportFlightPlanUI(final FlightPlanService flightPlanService) {
        this.flightPlanService = flightPlanService;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n=== Import Flight Plan from DSL ===");

        try {
            final List<Path> dslFiles = listDslFiles();

            if (dslFiles.isEmpty()) {
                System.out.println("No DSL files found in " + DSL_EXAMPLES_DIR.toAbsolutePath());
                return false;
            }

            System.out.println("\nAvailable DSL files:");
            for (int i = 0; i < dslFiles.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, dslFiles.get(i).getFileName());
            }
            System.out.println("  0. Cancel");

            final int choice = Console.readInteger("\nSelect a file: ");
            if (choice <= 0 || choice > dslFiles.size()) {
                System.out.println("Import cancelled.");
                return false;
            }

            final Path selectedPath = dslFiles.get(choice - 1);
            System.out.println("Selected: " + selectedPath);

            final String modelId = Console.readLine("Aircraft Model ID (e.g. BOEING-737): ");
            final String makerName = Console.readLine("Maker Name (e.g. Boeing): ");
            final double distance = Console.readInteger("Total distance (km): ");

            final FlightPlan result = flightPlanService.importFlightPlan(
                    selectedPath, modelId, makerName, distance);

            System.out.println("\nSuccess! Flight plan imported:");
            System.out.println("  " + result);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR: " + e.getMessage());
        }

        return false;
    }

    private List<Path> listDslFiles() throws IOException {
        if (!Files.isDirectory(DSL_EXAMPLES_DIR)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(DSL_EXAMPLES_DIR)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".dsl"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }
}
