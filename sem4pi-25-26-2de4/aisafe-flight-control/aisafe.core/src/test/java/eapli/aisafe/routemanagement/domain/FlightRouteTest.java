package eapli.aisafe.routemanagement.domain;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.organization.domain.AirTransportCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightRouteTest {

    private RouteDesignator tp123;
    private AirTransportCompany tap;
    private Airport lisbon;
    private Airport porto;

    @BeforeEach
    void setUp() {
        tap = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        tp123 = new RouteDesignator("TP123", "TP");

        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LPPC"),
                "Lisbon FIR",
                5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0)
        );

        lisbon = new Airport(
                IataCode.of("LIS"),
                IcaoCode.of("LPPT"),
                "Lisbon Airport",
                "Lisbon",
                "Portugal",
                new Coordinates(38.7742, -9.1342, 114),
                area
        );

        porto = new Airport(
                IataCode.of("OPO"),
                IcaoCode.of("LPPR"),
                "Porto Airport",
                "Porto",
                "Portugal",
                new Coordinates(41.2481, -8.6814, 69),
                area
        );
    }

    @Test
    void validFlightRouteIsCreated() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertEquals(tp123, route.identity());
        assertEquals(tap, route.company());
        assertEquals(lisbon, route.origin());
        assertEquals(porto, route.destination());
        assertTrue(route.isActive());
        assertNull(route.deactivatedFrom());
    }

    @Test
    void identityReturnsRouteDesignator() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertEquals(tp123, route.identity());
    }

    @Test
    void activeRouteIsAvailableOnAnyDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertTrue(route.isAvailableOn(LocalDate.of(2026, 6, 10)));
    }

    @Test
    void routesWithSameDesignatorAreEqual() {
        FlightRoute a = new FlightRoute(tp123, tap, lisbon, porto);
        FlightRoute b = new FlightRoute(new RouteDesignator("TP123", "TP"), tap, lisbon, porto);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void routesWithDifferentDesignatorAreDifferent() {
        FlightRoute a = new FlightRoute(tp123, tap, lisbon, porto);
        FlightRoute b = new FlightRoute(new RouteDesignator("TP124", "TP"), tap, lisbon, porto);

        assertNotEquals(a, b);
    }

    @Test
    void constructorShouldThrowWhenDesignatorIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new FlightRoute(null, tap, lisbon, porto));
    }

    @Test
    void constructorShouldThrowWhenCompanyIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new FlightRoute(tp123, null, lisbon, porto));
    }

    @Test
    void constructorShouldThrowWhenOriginIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new FlightRoute(tp123, tap, null, porto));
    }

    @Test
    void constructorShouldThrowWhenDestinationIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new FlightRoute(tp123, tap, lisbon, null));
    }

    @Test
    void constructorShouldThrowWhenOriginAndDestinationAreTheSame() {
        assertThrows(IllegalArgumentException.class,
                () -> new FlightRoute(tp123, tap, lisbon, lisbon));
    }

    @Test
    void deactivateFromShouldDeactivateRouteAndStoreDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);
        LocalDate date = LocalDate.of(2026, 6, 10);

        route.deactivateFrom(date);

        assertFalse(route.isActive());
        assertEquals(date, route.deactivatedFrom());
    }

    @Test
    void deactivateShouldUseCurrentDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivate();

        assertFalse(route.isActive());
        assertEquals(LocalDate.now(), route.deactivatedFrom());
    }

    @Test
    void deactivateFromShouldThrowWhenDateIsNull() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertThrows(IllegalArgumentException.class,
                () -> route.deactivateFrom(null));
    }

    @Test
    void deactivatingAlreadyInactiveRouteThrows() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivateFrom(LocalDate.of(2026, 6, 10));

        assertThrows(IllegalStateException.class,
                () -> route.deactivateFrom(LocalDate.of(2026, 6, 11)));
    }

    @Test
    void inactiveRouteShouldBeAvailableBeforeDeactivationDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivateFrom(LocalDate.of(2026, 6, 10));

        assertTrue(route.isAvailableOn(LocalDate.of(2026, 6, 9)));
    }

    @Test
    void inactiveRouteShouldNotBeAvailableOnDeactivationDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivateFrom(LocalDate.of(2026, 6, 10));

        assertFalse(route.isAvailableOn(LocalDate.of(2026, 6, 10)));
    }

    @Test
    void inactiveRouteShouldNotBeAvailableAfterDeactivationDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivateFrom(LocalDate.of(2026, 6, 10));

        assertFalse(route.isAvailableOn(LocalDate.of(2026, 6, 11)));
    }

    @Test
    void isAvailableOnShouldThrowWhenDateIsNull() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertThrows(IllegalArgumentException.class,
                () -> route.isAvailableOn(null));
    }

    @Test
    void activeRouteToStringContainsActive() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        assertTrue(route.toString().contains("ACTIVE"));
    }

    @Test
    void inactiveRouteToStringContainsInactiveAndDate() {
        FlightRoute route = new FlightRoute(tp123, tap, lisbon, porto);

        route.deactivateFrom(LocalDate.of(2026, 6, 10));

        assertTrue(route.toString().contains("INACTIVE"));
        assertTrue(route.toString().contains("2026-06-10"));
    }
}