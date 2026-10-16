package eapli.aisafe.flightplanmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.framework.domain.model.AggregateRoot;
import eapli.aisafe.weather.domain.WeatherData;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "FLIGHT_PLAN")
public class FlightPlan implements AggregateRoot<FlightPlanId> {

    @EmbeddedId
    private FlightPlanId id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "FLIGHT_PLAN_WEATHER_DATA",
            joinColumns = @JoinColumn(name = "flight_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "weather_data_id")
    )
    private List<WeatherData> weatherData = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_designator", referencedColumnName = "DESIGNATOR", nullable = false)
    private FlightRoute flightRoute;

    @Column(name = "departure_airport", nullable = false)
    private String departureAirport;

    @Column(name = "arrival_airport", nullable = false)
    private String arrivalAirport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aircraft_registration", referencedColumnName = "REGISTRATION_NUMBER",
            nullable = false)
    private Aircraft aircraft;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "model_id", referencedColumnName = "model_id"),
            @JoinColumn(name = "maker_name", referencedColumnName = "maker_name")
    })
    private AircraftModel aircraftModel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pilot_id", nullable = false)
    private Pilot pilot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FlightPlanStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_plan_id")
    private List<FlightLeg> legs = new ArrayList<>();

    @Column(name = "total_distance", nullable = false)
    private double totalDistance;

    @Column(name = "estimated_fuel", nullable = false)
    private double estimatedFuel;

    @Column(name = "departure_date_time")
    private LocalDateTime departureDateTime;

    /**
     * DSL source text associated with this flight plan.
     *
     * <p>Stored by US080/US081 at creation time (or supplied later by
     * the Pilot in US085). Read by US085 to run DSL validation and the
     * C physical test without requiring an external file.</p>
     */
    @Column(name = "dsl_content", columnDefinition = "TEXT")
    private String dslContent;

    protected FlightPlan() {
    }

    // ── US080 constructor ─────────────────────────────────────────────────────

    public FlightPlan(final FlightPlanId id,
                      final FlightRoute flightRoute,
                      final Aircraft aircraft,
                      final Pilot pilot,
                      final LocalDateTime departureDateTime,
                      final double estimatedFuel) {

        if (id == null)            throw new IllegalArgumentException("Flight plan ID must not be null.");
        if (flightRoute == null)   throw new IllegalArgumentException("Flight route must not be null.");
        if (aircraft == null)      throw new IllegalArgumentException("Aircraft must not be null.");
        if (pilot == null)         throw new IllegalArgumentException("Pilot must not be null.");
        if (departureDateTime == null) throw new IllegalArgumentException("Departure date/time must not be null.");
        if (estimatedFuel <= 0)    throw new IllegalArgumentException("Estimated fuel must be positive.");

        this.id               = id;
        this.flightRoute      = flightRoute;
        this.aircraft         = aircraft;
        this.aircraftModel    = aircraft.model();
        this.pilot            = pilot;
        this.departureDateTime = departureDateTime;
        this.estimatedFuel    = estimatedFuel;
        this.totalDistance    = 0.0;
        this.legs             = new ArrayList<>();
        this.status           = FlightPlanStatus.DRAFT;
        this.departureAirport = flightRoute.origin().identity().toString();
        this.arrivalAirport   = flightRoute.destination().identity().toString();
    }

    // ── US081 constructors ────────────────────────────────────────────────────

    public FlightPlan(final FlightPlanId id,
                      final String route,
                      final String departureAirport,
                      final String arrivalAirport,
                      final AircraftModel aircraftModel,
                      final List<FlightLeg> legs,
                      final double totalDistance,
                      final double estimatedFuel) {
        this(id, route, departureAirport, arrivalAirport, aircraftModel,
                legs, totalDistance, estimatedFuel, null);
    }

    public FlightPlan(final FlightPlanId id,
                      final String route,
                      final String departureAirport,
                      final String arrivalAirport,
                      final AircraftModel aircraftModel,
                      final List<FlightLeg> legs,
                      final double totalDistance,
                      final double estimatedFuel,
                      final LocalDateTime departureDateTime) {

        if (id == null)            throw new IllegalArgumentException("Flight plan ID must not be null.");
        if (route == null || route.isBlank()) throw new IllegalArgumentException("Route must not be empty.");
        if (departureAirport == null || departureAirport.isBlank()) throw new IllegalArgumentException("Departure airport must not be empty.");
        if (arrivalAirport == null || arrivalAirport.isBlank()) throw new IllegalArgumentException("Arrival airport must not be empty.");
        if (aircraftModel == null) throw new IllegalArgumentException("Aircraft model must not be null.");
        if (legs == null || legs.isEmpty()) throw new IllegalArgumentException("At least one leg is required.");

        this.id               = id;
        this.departureAirport = departureAirport;
        this.arrivalAirport   = arrivalAirport;
        this.aircraftModel    = aircraftModel;
        this.totalDistance    = totalDistance;
        this.estimatedFuel    = estimatedFuel;
        this.departureDateTime = departureDateTime;
        this.status           = FlightPlanStatus.DRAFT;
        this.flightRoute      = null;
        this.aircraft         = null;
        this.pilot            = null;

        this.legs = new ArrayList<>();
        for (final FlightLeg leg : legs) {
            leg.setFlightPlan(this);
            this.legs.add(leg);
        }
    }

    // ── Business behaviour ────────────────────────────────────────────────────

    /**
     * Stores the DSL content for this flight plan.
     *
     * <p>Called by US080 (after the Pilot supplies DSL text or file) and by
     * US081 (after DSL import). The stored DSL is read by US085 for validation
     * and physical testing — the Pilot does not need to supply an external file.</p>
     */
    public void storeDsl(final String dslContent) {
        if (dslContent == null || dslContent.isBlank())
            throw new IllegalArgumentException("DSL content must not be empty.");
        this.dslContent = dslContent;
    }

    /**
     * Associates weather data with this flight plan (US082).
     *
     * <p>If the plan was previously validated or approved, adding weather
     * data reverts it to DRAFT — the previous validation is no longer valid.</p>
     */
    public void addWeatherData(final WeatherData data) {
        if (data == null)
            throw new IllegalArgumentException("Weather data is required.");

        if (status == FlightPlanStatus.ACTIVE
                || status == FlightPlanStatus.COMPLETED
                || status == FlightPlanStatus.CANCELLED)
            throw new IllegalStateException(
                    "Weather data can only be added before flight execution.");

        final boolean alreadyInserted = weatherData.stream()
                .anyMatch(existing -> existing == data
                        || existing.sameAs(data)
                        || data.sameAs(existing));

        if (alreadyInserted)
            throw new IllegalArgumentException(
                    "Weather data is already associated with this flight plan.");

        weatherData.add(data);

        if (status == FlightPlanStatus.VALIDATED || status == FlightPlanStatus.APPROVED)
            status = FlightPlanStatus.DRAFT;
    }

    /** Marks this flight plan as validated (US085 success). */
    public void validate() {
        this.status = FlightPlanStatus.VALIDATED;
    }

    /** Marks this flight plan as failed validation (US085 failure). */
    public void invalidate() {
        this.status = FlightPlanStatus.FAILED;
    }

    /** Marks this flight plan as approved. */
    public void approve() {
        this.status = FlightPlanStatus.APPROVED;
    }

    // ── AggregateRoot ─────────────────────────────────────────────────────────

    @Override
    public FlightPlanId identity() { return id; }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof FlightPlan)) return false;
        return this.id.equals(((FlightPlan) other).id);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public FlightRoute      flightRoute()       { return flightRoute;       }
    public String           departureAirport()  { return departureAirport;  }
    public String           arrivalAirport()    { return arrivalAirport;    }
    public Aircraft         aircraft()          { return aircraft;          }
    public AircraftModel    aircraftModel()     { return aircraftModel;     }
    public Pilot            pilot()             { return pilot;             }
    public FlightPlanStatus status()            { return status;            }
    public List<FlightLeg>  legs()              { return Collections.unmodifiableList(legs); }
    public double           totalDistance()     { return totalDistance;     }
    public double           estimatedFuel()     { return estimatedFuel;     }
    public LocalDateTime    departureDateTime() { return departureDateTime; }
    public List<WeatherData> weatherData()      { return Collections.unmodifiableList(weatherData); }

    /**
     * Returns the stored DSL content, or {@code null} if not yet set.
     * Used by US085 to validate the plan without requiring an external file.
     */
    public String dslContent() { return dslContent; }

    // ── equals / hashCode / toString ─────────────────────────────────────────

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightPlan)) return false;
        return id.equals(((FlightPlan) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s | Aircraft: %s | Pilot: %s | Status: %s | Fuel: %.1f kg",
                id,
                departureAirport,
                arrivalAirport,
                aircraft != null ? aircraft.identity() : aircraftModel,
                pilot != null ? pilot.email() : "N/A",
                status,
                estimatedFuel);
    }
}