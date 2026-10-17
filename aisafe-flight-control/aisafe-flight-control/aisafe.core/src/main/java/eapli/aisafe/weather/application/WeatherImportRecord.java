package eapli.aisafe.weather.application;

import java.time.LocalDateTime;

/**
 * Data transfer object that represents one parsed weather record before it is
 * converted into the weather domain model.
 *
 * <p>
 * Instances of this class are produced by {@link WeatherDataImporter}
 * implementations and consumed by {@link ImportWeatherDataController}. The
 * purpose of this object is to provide a normalized intermediate representation
 * for weather data coming from different external providers.
 * </p>
 *
 * <p>
 * This class intentionally stores simple public final fields because it is used
 * as an immutable parsing result rather than as a domain entity. Domain
 * validation is performed later when the controller creates value objects such
 * as temperature, wind speed, humidity and pressure.
 * </p>
 */
public class WeatherImportRecord {

    /**
     * Code of the air control area to which the weather data applies.
     */
    public final String areaCode;

    /**
     * Start date and time of the weather data validity period.
     */
    public final LocalDateTime start;

    /**
     * End date and time of the weather data validity period.
     */
    public final LocalDateTime end;

    /**
     * Temperature value parsed from the source, or a provider-specific default.
     */
    public final double temperature;

    /**
     * Wind speed value parsed from the source, expressed in meters per second.
     */
    public final double windSpeed;

    /**
     * Wind direction value parsed from the source, expressed in degrees.
     */
    public final double windDirection;

    /**
     * Humidity value parsed from the source, expressed as a percentage.
     */
    public final double humidity;

    /**
     * Atmospheric pressure value parsed from the source, expressed in hPa.
     */
    public final double pressure;

    /**
     * Name or identifier of the external weather data provider.
     */
    public final String provider;

    /**
     * Creates a parsed weather import record.
     *
     * @param areaCode code of the air control area
     * @param start start date and time of the validity period
     * @param end end date and time of the validity period
     * @param temperature temperature value
     * @param windSpeed wind speed in meters per second
     * @param windDirection wind direction in degrees
     * @param humidity humidity percentage
     * @param pressure atmospheric pressure in hPa
     * @param provider weather data provider identifier
     */
    public WeatherImportRecord(String areaCode,
                               LocalDateTime start,
                               LocalDateTime end,
                               double temperature,
                               double windSpeed,
                               double windDirection,
                               double humidity,
                               double pressure,
                               String provider) {
        this.areaCode = areaCode;
        this.start = start;
        this.end = end;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.pressure = pressure;
        this.provider = provider;
    }
}