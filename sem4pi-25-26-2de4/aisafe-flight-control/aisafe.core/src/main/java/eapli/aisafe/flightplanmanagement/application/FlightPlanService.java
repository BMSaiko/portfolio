package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.flightplanmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import pt.isep.sem4pi.lprog.application.FlightPlanImportService;
import pt.isep.sem4pi.lprog.application.ImportResult;
import eapli.aisafe.flightplanmanagement.domain.FileFormatValidator;

import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Facade service that coordinates DSL validation, domain construction,
 * and persistence for flight plans.
 */
public class FlightPlanService {

    private final AuthService authService;
    private final FlightPlanRepository flightPlanRepository;
    private final AircraftModelRepository aircraftModelRepository;
    private final FlightPlanImportService importService;
    private final FileFormatValidator fileFormatValidator;

    public FlightPlanService(final AuthService authService,
                             final FlightPlanRepository flightPlanRepository,
                             final AircraftModelRepository aircraftModelRepository) {
        this.authService = authService;
        this.flightPlanRepository = flightPlanRepository;
        this.aircraftModelRepository = aircraftModelRepository;
        this.importService = new FlightPlanImportService();
        this.fileFormatValidator = new FileFormatValidator();
    }

    /**
     * Imports a flight plan from a DSL file.
     */
    public FlightPlan importFlightPlan(final Path filePath,
                                       final String modelId,
                                       final String makerName,
                                       final double distance) throws IOException {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        // Step 1: Validate file format before DSL processing
        final var formatResult = fileFormatValidator.validate(filePath);
        if (!formatResult.isValid()) {
            final StringBuilder fmtErr = new StringBuilder("File format validation failed:\n");
            formatResult.errors().forEach(e -> fmtErr.append("  - ").append(e).append("\n"));
            throw new IllegalArgumentException(fmtErr.toString());
        }

        final ImportResult result = importService.importFlightPlan(filePath);
        if (!result.isSuccess()) {
            final StringBuilder sb = new StringBuilder("DSL validation failed:\n");
            result.getErrors().forEach(e -> sb.append("  - ").append(e).append("\n"));
            throw new IllegalArgumentException(sb.toString());
        }

        final AircraftModelId aircraftId = AircraftModelId.of(modelId, makerName);
        final AircraftModel aircraft = aircraftModelRepository.findById(aircraftId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aircraft model not found: " + modelId + " (" + makerName + ")"));

        final pt.isep.sem4pi.lprog.model.FlightPlan parsed = result.getFlightPlans().get(0);
        final FlightPlanId id = FlightPlanId.of(parsed.getId());
        final List<FlightLeg> legs = buildLegs(parsed);
        final double estimatedFuel = legs.stream().mapToDouble(FlightLeg::fuel).sum();

        final FlightPlan flightPlan = new FlightPlan(
                id, buildRoute(parsed),
                parsed.getLegs().get(0).getDeparture().getAirportCode(),
                parsed.getLegs().get(parsed.getLegs().size() - 1).getArrival().getAirportCode(),
                aircraft, legs, distance, estimatedFuel, LocalDateTime.now());

        return flightPlanRepository.save(flightPlan);
    }

    /**
     * Lists all flight plans.
     */
    public List<FlightPlan> listFlightPlans() {
        authService.ensureHasAnyRole(Role.PILOT, Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        final List<FlightPlan> result = new ArrayList<>();
        flightPlanRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * Finds a flight plan by ID.
     */
    public Optional<FlightPlan> findFlightPlan(final String id) {
        authService.ensureHasAnyRole(Role.PILOT, Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return flightPlanRepository.findById(FlightPlanId.of(id));
    }

    private List<FlightLeg> buildLegs(final pt.isep.sem4pi.lprog.model.FlightPlan parsed) {
        final List<FlightLeg> legs = new ArrayList<>();
        for (final pt.isep.sem4pi.lprog.model.FlightLeg parsedLeg : parsed.getLegs()) {
            final List<FlightSegment> segments = new ArrayList<>();
            for (final pt.isep.sem4pi.lprog.model.FlightSegment parsedSeg : parsedLeg.getSegments()) {
                final Waypoint start = Waypoint.of(
                        parsedSeg.getStart().getLat(),
                        parsedSeg.getStart().getLon(),
                        parsedSeg.getStart().getAltitude());
                final Waypoint end = Waypoint.of(
                        parsedSeg.getEnd().getLat(),
                        parsedSeg.getEnd().getLon(),
                        parsedSeg.getEnd().getAltitude());
                segments.add(FlightSegment.of(parsedSeg.getMode(), start, end));
            }
            legs.add(FlightLeg.of(
                    parsedLeg.getDeparture().getAirportCode(),
                    parsedLeg.getArrival().getAirportCode(),
                    parsedLeg.getFuel().getQuantity(),
                    segments));
        }
        return legs;
    }

    private String buildRoute(final pt.isep.sem4pi.lprog.model.FlightPlan parsed) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parsed.getLegs().size(); i++) {
            if (i > 0) sb.append("-");
            sb.append(parsed.getLegs().get(i).getDeparture().getAirportCode());
        }
        if (!parsed.getLegs().isEmpty()) {
            sb.append("-");
            sb.append(parsed.getLegs().get(parsed.getLegs().size() - 1).getArrival().getAirportCode());
        }
        return sb.toString();
    }
}
