package eapli.aisafe.weather.repositories;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.weather.domain.WeatherData;

import java.time.LocalDateTime;

/**
 * Repository contract for accessing and persisting {@link WeatherData}
 * aggregate roots.
 *
 * <p>
 * This repository supports the weather management use cases by allowing weather
 * data to be saved, retrieved by identity, listed, deleted and queried by air
 * control area and time interval.
 * </p>
 *
 * <p>
 * The repository identity type is {@link Long}, which corresponds to the
 * generated database identifier of {@link WeatherData}.
 * </p>
 */
public interface WeatherDataRepository extends Repository<WeatherData, Long> {

    /**
     * Finds weather data records for a given air control area whose period
     * starts inside the provided date-time interval.
     *
     * <p>
     * This method is typically used by the weather consultation use case to
     * retrieve all weather records for a specific air control area and day. The
     * filtering is based on the start timestamp of the {@link WeatherData}
     * validity period.
     * </p>
     *
     * @param area the air control area to filter by
     * @param start the inclusive lower bound for the weather period start
     * @param end the exclusive upper bound for the weather period start
     * @return an iterable collection of matching weather data records
     */
    Iterable<WeatherData> findByAreaAndPeriodStartBetween(
            AirControlArea area,
            LocalDateTime start,
            LocalDateTime end);
}