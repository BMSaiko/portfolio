package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Application controller responsible for inserting weather data into a pilot's
 * flight plan.
 *
 * <p>
 * This controller implements the application logic for US082. It allows an
 * authenticated pilot to associate an existing {@link WeatherData} record with
 * one of their own {@link FlightPlan} instances.
 * </p>
 *
 * <p>
 * The controller enforces authorization and ownership rules before delegating
 * the actual association and validation invalidation behavior to the
 * {@link FlightPlan} aggregate.
 * </p>
 */
public class InsertWeatherDataInFlightPlanController {

    /**
     * Authentication and authorization service used to validate the current
     * user session and role.
     */
    private final AuthService authService;

    /**
     * Repository used to retrieve and persist flight plans.
     */
    private final FlightPlanRepository flightPlanRepository;

    /**
     * Repository used to retrieve existing weather data records.
     */
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Creates the controller with all required dependencies.
     *
     * @param authService authentication and authorization service
     * @param flightPlanRepository repository used to access flight plans
     * @param weatherDataRepository repository used to access weather data
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public InsertWeatherDataInFlightPlanController(
            final AuthService authService,
            final FlightPlanRepository flightPlanRepository,
            final WeatherDataRepository weatherDataRepository) {

        if (authService == null || flightPlanRepository == null || weatherDataRepository == null) {
            throw new IllegalArgumentException("Dependencies must not be null.");
        }

        this.authService = authService;
        this.flightPlanRepository = flightPlanRepository;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Lists the flight plans assigned to the authenticated pilot.
     *
     * <p>
     * The method scans available flight plans and returns only those whose pilot
     * email matches the authenticated user's email.
     * </p>
     *
     * @return list of flight plans owned by the authenticated pilot
     * @throws SecurityException if the current user is not authenticated or does
     *                           not have the {@link Role#PILOT} role
     */
    public List<FlightPlan> myFlightPlans() {
        authService.ensureHasAnyRole(Role.PILOT);

        final Email currentPilotEmail = currentUserEmail();
        final List<FlightPlan> result = new ArrayList<>();

        flightPlanRepository.findAll().forEach(plan -> {
            if (plan.pilot() != null && currentPilotEmail.equals(plan.pilot().email())) {
                result.add(plan);
            }
        });

        return result;
    }

    /**
     * Lists all weather data records available for selection.
     *
     * @return iterable collection of weather data records
     * @throws SecurityException if the current user is not authenticated or does
     *                           not have the {@link Role#PILOT} role
     */
    public Iterable<WeatherData> availableWeatherData() {
        authService.ensureHasAnyRole(Role.PILOT);
        return weatherDataRepository.findAll();
    }

    /**
     * Inserts an existing weather data record into one of the authenticated
     * pilot's flight plans.
     *
     * <p>
     * The method validates the input identifiers, verifies that the flight plan
     * exists, checks that it belongs to the authenticated pilot, retrieves the
     * weather data record and then delegates the association to
     * {@link FlightPlan#addWeatherData(WeatherData)}.
     * </p>
     *
     * @param flightPlanId textual flight plan identifier
     * @param weatherDataId weather data identifier
     * @return the saved flight plan after weather data insertion
     * @throws SecurityException if the current user is not authenticated, does
     *                           not have the {@link Role#PILOT} role, or does
     *                           not own the selected flight plan
     * @throws IllegalArgumentException if {@code flightPlanId} is {@code null}
     *                                  or blank
     * @throws IllegalArgumentException if {@code weatherDataId} is {@code null}
     * @throws IllegalStateException if the flight plan does not exist
     * @throws IllegalStateException if the weather data record does not exist
     */
    public FlightPlan insertWeatherData(final String flightPlanId, final Long weatherDataId) {
        authService.ensureHasAnyRole(Role.PILOT);

        if (flightPlanId == null || flightPlanId.isBlank()) {
            throw new IllegalArgumentException("Flight plan ID is required.");
        }

        if (weatherDataId == null) {
            throw new IllegalArgumentException("Weather data ID is required.");
        }

        final FlightPlan flightPlan = flightPlanRepository.findById(FlightPlanId.of(flightPlanId))
                .orElseThrow(() -> new IllegalStateException("Flight plan not found: " + flightPlanId));

        ensureOwnedByCurrentPilot(flightPlan);

        final WeatherData weatherData = weatherDataRepository.findById(weatherDataId)
                .orElseThrow(() -> new IllegalStateException("Weather data not found: " + weatherDataId));

        flightPlan.addWeatherData(weatherData);

        return flightPlanRepository.save(flightPlan);
    }

    /**
     * Ensures that the selected flight plan belongs to the authenticated pilot.
     *
     * @param flightPlan flight plan to check
     * @throws IllegalArgumentException if the flight plan is not assigned to a
     *                                  pilot
     * @throws SecurityException if the authenticated user is not the assigned
     *                           pilot
     */
    private void ensureOwnedByCurrentPilot(final FlightPlan flightPlan) {
        if (flightPlan.pilot() == null) {
            throw new IllegalArgumentException("Flight plan is not assigned to a pilot.");
        }

        final Email currentPilotEmail = currentUserEmail();

        if (!currentPilotEmail.equals(flightPlan.pilot().email())) {
            throw new SecurityException("A pilot can only add weather data to their own flight plans.");
        }
    }

    /**
     * Returns the email of the currently authenticated user.
     *
     * @return authenticated user's email
     * @throws SecurityException if there is no authenticated user
     */
    private Email currentUserEmail() {
        return authService.currentUser()
                .orElseThrow(() -> new SecurityException("User not authenticated."))
                .email();
    }
}