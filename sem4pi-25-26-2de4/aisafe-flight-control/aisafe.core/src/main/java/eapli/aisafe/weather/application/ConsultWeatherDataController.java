package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Application controller responsible for consulting weather data by air control
 * area and date.
 *
 * <p>
 * This controller implements the application logic for US043. It validates the
 * authenticated user's role, resolves the requested air control area and
 * delegates the weather data query to {@link WeatherDataRepository}.
 * </p>
 *
 * <p>
 * Weather data is filtered by the start timestamp of its validity period. When
 * a user asks for a specific date, the controller searches for records whose
 * period starts from the beginning of that day, inclusive, until the beginning
 * of the next day, exclusive.
 * </p>
 */
public class ConsultWeatherDataController {

    /**
     * Authentication and authorization service used to restrict access to
     * weather consultation.
     */
    private final AuthService auth;

    /**
     * Repository used to resolve the requested air control area.
     */
    private final AirControlAreaRepository areaRepo;

    /**
     * Repository used to retrieve matching weather data records.
     */
    private final WeatherDataRepository weatherRepo;

    /**
     * Creates a controller for consulting weather data.
     *
     * @param auth authentication and authorization service
     * @param areaRepo repository used to find air control areas
     * @param weatherRepo repository used to query weather data
     */
    public ConsultWeatherDataController(AuthService auth,
                                        AirControlAreaRepository areaRepo,
                                        WeatherDataRepository weatherRepo) {
        this.auth = auth;
        this.areaRepo = areaRepo;
        this.weatherRepo = weatherRepo;
    }

    /**
     * Consults weather data for a specific air control area and date.
     *
     * <p>
     * The operation is available to weather users, pilots, flight control
     * operators and administrators.
     * </p>
     *
     * @param areaCode code of the air control area to consult
     * @param date date for which weather data should be returned
     * @return iterable collection of matching weather data records
     * @throws SecurityException if the authenticated user does not have one of
     *                           the allowed roles
     * @throws IllegalArgumentException if {@code areaCode} is {@code null} or
     *                                  blank
     * @throws IllegalArgumentException if {@code date} is {@code null}
     * @throws IllegalArgumentException if no air control area exists with the
     *                                  given code
     */
    public Iterable<WeatherData> consult(String areaCode, LocalDate date) {
        auth.ensureHasAnyRole(
                Role.WEATHER_PERSON,
                Role.PILOT,
                Role.FLIGHT_CONTROL_OPERATOR,
                Role.ADMIN
        );

        if (areaCode == null || areaCode.isBlank()) {
            throw new IllegalArgumentException("Air control area code is required.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }

        AirControlArea area = areaRepo.findByCode(AirControlAreaCode.of(areaCode))
                .orElseThrow(() -> new IllegalArgumentException("Air control area not found: " + areaCode));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        return weatherRepo.findByAreaAndPeriodStartBetween(area, startOfDay, startOfNextDay);
    }
}