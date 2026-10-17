package eapli.aisafe.weather.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result object that summarizes the outcome of a bulk weather data import
 * operation.
 *
 * <p>
 * The import process may partially succeed: valid records can be persisted
 * while invalid records are rejected. This class records both the number of
 * successfully imported records and the error messages associated with rejected
 * records.
 * </p>
 *
 * <p>
 * Instances of this class are returned by {@link ImportWeatherDataController}
 * and can be used by the user interface to present a clear import summary to
 * the user.
 * </p>
 */
public class WeatherImportResult {

    /**
     * Number of weather records successfully imported and persisted.
     */
    private int importedRecords;

    /**
     * Error messages for rejected weather records.
     */
    private final List<String> errors = new ArrayList<>();

    /**
     * Records one successfully imported weather data record.
     */
    public void recordImported() {
        importedRecords++;
    }

    /**
     * Records one rejected weather data record.
     *
     * @param error human-readable rejection reason
     */
    public void recordError(String error) {
        errors.add(error);
    }

    /**
     * Returns the number of successfully imported records.
     *
     * @return number of imported records
     */
    public int importedRecords() {
        return importedRecords;
    }

    /**
     * Returns the number of rejected records.
     *
     * @return number of rejected records
     */
    public int rejectedRecords() {
        return errors.size();
    }

    /**
     * Returns the rejection errors.
     *
     * <p>
     * The returned list is unmodifiable to protect the internal result state.
     * </p>
     *
     * @return unmodifiable list of error messages
     */
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Indicates whether the import operation rejected any records.
     *
     * @return {@code true} if at least one error was recorded; otherwise
     * {@code false}
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}