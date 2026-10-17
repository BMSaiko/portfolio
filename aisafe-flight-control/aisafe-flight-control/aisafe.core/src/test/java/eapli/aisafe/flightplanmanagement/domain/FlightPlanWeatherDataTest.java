package eapli.aisafe.flightplanmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.CabinConfiguration;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.weather.domain.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.Assert.*;

public class FlightPlanWeatherDataTest {

    @Test
    public void addWeatherDataShouldAssociateWeatherRecordWithFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        WeatherData weatherData = weatherData();

        flightPlan.addWeatherData(weatherData);

        assertEquals(1, flightPlan.weatherData().size());
        assertTrue(flightPlan.weatherData().contains(weatherData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWeatherDataShouldRejectNullWeatherData() {
        FlightPlan flightPlan = flightPlan();

        flightPlan.addWeatherData(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWeatherDataShouldRejectDuplicateWeatherData() {
        FlightPlan flightPlan = flightPlan();
        WeatherData weatherData = weatherData();

        flightPlan.addWeatherData(weatherData);
        flightPlan.addWeatherData(weatherData);
    }

    @Test
    public void addWeatherDataShouldKeepDraftFlightPlanAsDraft() {
        FlightPlan flightPlan = flightPlan();

        flightPlan.addWeatherData(weatherData());

        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
    }

    @Test
    public void addWeatherDataShouldInvalidateValidatedFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        flightPlan.validate();

        assertEquals(FlightPlanStatus.VALIDATED, flightPlan.status());

        flightPlan.addWeatherData(weatherData());

        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
    }

    @Test
    public void addWeatherDataShouldInvalidateApprovedFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        flightPlan.approve();

        assertEquals(FlightPlanStatus.APPROVED, flightPlan.status());

        flightPlan.addWeatherData(weatherData());

        assertEquals(FlightPlanStatus.DRAFT, flightPlan.status());
    }

    @Test(expected = IllegalStateException.class)
    public void addWeatherDataShouldRejectActiveFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        forceStatus(flightPlan, FlightPlanStatus.ACTIVE);

        flightPlan.addWeatherData(weatherData());
    }

    @Test(expected = IllegalStateException.class)
    public void addWeatherDataShouldRejectCompletedFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        forceStatus(flightPlan, FlightPlanStatus.COMPLETED);

        flightPlan.addWeatherData(weatherData());
    }

    @Test(expected = IllegalStateException.class)
    public void addWeatherDataShouldRejectCancelledFlightPlan() {
        FlightPlan flightPlan = flightPlan();
        forceStatus(flightPlan, FlightPlanStatus.CANCELLED);

        flightPlan.addWeatherData(weatherData());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void weatherDataAccessorShouldReturnUnmodifiableList() {
        FlightPlan flightPlan = flightPlan();
        flightPlan.addWeatherData(weatherData());

        flightPlan.weatherData().add(weatherData());
    }

    private FlightPlan flightPlan() {
        AirTransportCompany company = company();
        AircraftModel model = aircraftModel();

        return new FlightPlan(
                FlightPlanId.of("FP-US082"),
                route(company),
                aircraft(company, model),
                pilot(company, model),
                LocalDateTime.now().plusDays(5),
                15_000.0
        );
    }

    private WeatherData weatherData() {
        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LIS_AREA"),
                "Lisbon Area",
                500.0,
                new GeoBoundary(39.0, 38.0, -8.0, -9.0)
        );

        return new WeatherData(
                area,
                TimePeriod.of(
                        LocalDateTime.of(2026, 6, 10, 8, 0),
                        LocalDateTime.of(2026, 6, 10, 9, 0)
                ),
                Temperature.of(19.5),
                WindSpeed.of(12.0),
                WindDirection.of(270.0),
                Humidity.of(65.0),
                Pressure.of(1013.0)
        );
    }

    private AirTransportCompany company() {
        return new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
    }

    private FlightRoute route(AirTransportCompany company) {
        return new FlightRoute(
                new RouteDesignator("TP123", "TP"),
                company,
                airport("LIS"),
                airport("OPO")
        );
    }

    private Airport airport(String iata) {
        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of(iata + "_AREA"),
                "Area " + iata,
                500.0,
                new GeoBoundary(42.0, 35.0, -6.0, -12.0)
        );

        return new Airport(
                IataCode.of(iata),
                IcaoCode.of("L" + iata),
                "Airport " + iata,
                "City " + iata,
                "Portugal",
                new Coordinates(38.0, -9.0, 100.0),
                area
        );
    }

    private Aircraft aircraft(AirTransportCompany company, AircraftModel model) {
        return new Aircraft(
                "CS-TKX",
                model,
                company,
                "Portugal",
                new CabinConfiguration(8, 24, 120),
                OperationalStatus.ACTIVE,
                2018
        );
    }

    private Pilot pilot(AirTransportCompany company, AircraftModel model) {
        User user = new User(
                new Email("pilot@aisafe.com"),
                "Pilot123",
                UserName.of("Demo Pilot"),
                PhoneNumber.of("+351910000001"),
                Set.of(Role.PILOT)
        );

        return new Pilot(
                user,
                new Email("pilot@aisafe.com"),
                "Demo Pilot",
                "Captain",
                company,
                Set.of(model)
        );
    }

    private AircraftModel aircraftModel() {
        EngineModel engine = new EngineModel(
                EngineModelId.of("CFM56-7B", "CFM"),
                EngineType.TURBOFAN,
                Thrust.of(121_000),
                Thrust.of(26_000),
                Speed.of(0.78),
                TSFC.of(1.6e-4),
                Coefficient.of(1.0),
                FuelType.JET_A1
        );

        return new AircraftModel(
                AircraftModelId.of("BOEING-737", "Boeing"),
                AircraftType.PASSENGER,
                Weight.of(41_413),
                Weight.of(79_016),
                Weight.of(62_732),
                Weight.of(26_020),
                Altitude.of(41_000),
                Speed.of(0.785),
                Area.of(338),
                35.79,
                9.45,
                Coefficient.of(0.85),
                Coefficient.of(0.021),
                Coefficient.of(0.5),
                2,
                189,
                5_600,
                Set.of(engine)
        );
    }

    private void forceStatus(FlightPlan flightPlan, FlightPlanStatus status) {
        try {
            Field field = FlightPlan.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(flightPlan, status);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}