package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.weather.application.ImportWeatherDataController;
import eapli.aisafe.weather.application.WeatherImportResult;

import java.nio.file.Path;

/**
 * Console user interface for importing bulk weather data.
 *
 * <p>
 * This UI supports the weather data import use case by asking the user for the
 * path of a CSV file and delegating the import operation to
 * {@link ImportWeatherDataController}.
 * </p>
 *
 * <p>
 * The controller is responsible for authorization, parsing, validation and
 * persistence. This class is responsible only for collecting console input and
 * presenting the import result to the user.
 * </p>
 */
public class ImportWeatherDataUI extends AbstractUI {

    /**
     * Application controller used to execute the import operation.
     */
    private final ImportWeatherDataController controller;

    /**
     * Creates the import weather data UI.
     *
     * @param controller controller responsible for importing weather data
     */
    public ImportWeatherDataUI(ImportWeatherDataController controller) {
        this.controller = controller;
    }

    /**
     * Displays the import form, executes the import operation and prints a
     * summary of the result.
     *
     * <p>
     * The user must provide a CSV file path. After the import finishes, the UI
     * displays the number of imported and rejected records. If any records were
     * rejected, their error messages are also printed.
     * </p>
     *
     * @return {@code true} to keep the parent menu active
     */
    @Override
    protected boolean doShow() {
        System.out.println("Import bulk weather data");
        System.out.println();

        System.out.print("CSV file path: ");
        String path = scanner.nextLine();

        try {
            WeatherImportResult result = controller.importFrom(Path.of(path));

            System.out.println();
            System.out.println("Import finished.");
            System.out.println("Imported records: " + result.importedRecords());
            System.out.println("Rejected records: " + result.rejectedRecords());

            if (result.hasErrors()) {
                System.out.println();
                System.out.println("Errors:");
                result.errors().forEach(error -> System.out.println("- " + error));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return true;
    }
}