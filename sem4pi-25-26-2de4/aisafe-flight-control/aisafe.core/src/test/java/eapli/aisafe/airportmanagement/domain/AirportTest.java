package eapli.aisafe.airportmanagement.domain;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AirportTest {

    private static final AirControlArea LPPC = new AirControlArea(
            AirControlAreaCode.of("LPPC"),
            "Lisbon Oceanic",
            5000.0,
            new GeoBoundary(47.0, 36.0, -6.0, -20.0)
    );

    private Airport validAirport() {
        return new Airport(
                IataCode.of("LIS"),
                IcaoCode.of("LPPT"),
                "Humberto Delgado Airport",
                "Lisbon",
                "Portugal",
                new Coordinates(38.7742, -9.1342, 113.0),
                LPPC
        );
    }

    @Test
    void ensureValidAirportIsCreated() {
        assertDoesNotThrow(this::validAirport);
    }

    @Test
    void ensureIdentityIsCorrect() {
        assertEquals(IataCode.of("LIS"), validAirport().identity());
    }

    @Test
    void ensureSameIataCodeMeansSameAs() {
        Airport a = validAirport();
        Airport b = new Airport(
                IataCode.of("LIS"),
                IcaoCode.of("LPPR"),
                "Another Airport",
                "Porto", "Portugal",
                new Coordinates(41.2481, -8.6814, 69.0),
                LPPC
        );
        assertTrue(a.sameAs(b));
    }

    @Test
    void ensureDifferentIataCodeMeansNotSameAs() {
        Airport a = validAirport();
        Airport b = new Airport(
                IataCode.of("OPO"),
                IcaoCode.of("LPPR"),
                "Francisco Sa Carneiro Airport",
                "Porto", "Portugal",
                new Coordinates(41.2481, -8.6814, 69.0),
                LPPC
        );
        assertFalse(a.sameAs(b));
    }

    @Test
    void ensureNullIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Airport(null, IcaoCode.of("LPPT"),
                        "Name", "Town", "Country",
                        new Coordinates(38.77, -9.13, 113.0), LPPC));
    }

    @Test
    void ensureNullAirControlAreaIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Airport(IataCode.of("LIS"), IcaoCode.of("LPPT"),
                        "Name", "Town", "Country",
                        new Coordinates(38.77, -9.13, 113.0), null));
    }
}
