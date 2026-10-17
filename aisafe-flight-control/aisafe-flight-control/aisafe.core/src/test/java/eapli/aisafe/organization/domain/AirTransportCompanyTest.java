package eapli.aisafe.organization.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AirTransportCompany} domain entity.
 *
 * <p>
 * These tests validate the domain rules required by US060 - Register an air
 * transport company. They focus only on the entity invariants, such as company
 * name validation, IATA code format, ICAO code format, and code normalization.
 * </p>
 */
class AirTransportCompanyTest {

    /**
     * Ensures that a valid air transport company can be created and that its
     * business attributes are correctly stored.
     */
    @Test
    void createsValidAirTransportCompany() {
        AirTransportCompany company = new AirTransportCompany("Lufthansa", "LH", "DLH");

        assertEquals("Lufthansa", company.companyName());
        assertEquals("LH", company.iataCode());
        assertEquals("DLH", company.icaoCode());
        assertEquals("DLH", company.customerCode());
    }

    /**
     * Ensures that IATA and ICAO codes are normalized to uppercase when the
     * entity is created.
     */
    @Test
    void normalizesCodesToUppercase() {
        AirTransportCompany company = new AirTransportCompany("Tap", "tp", "tap");

        assertEquals("TP", company.iataCode());
        assertEquals("TAP", company.icaoCode());
    }

    /**
     * Ensures that an air transport company cannot be created with a blank
     * company name.
     */
    @Test
    void rejectsBlankCompanyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany(" ", "LH", "DLH"));
    }

    /**
     * Ensures that invalid IATA codes are rejected.
     *
     * <p>
     * A valid IATA code must contain exactly two letters.
     * </p>
     */
    @Test
    void rejectsInvalidIataCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "L", "DLH"));

        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "LHH", "DLH"));

        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "1H", "DLH"));
    }

    /**
     * Ensures that invalid ICAO codes are rejected.
     *
     * <p>
     * A valid ICAO code must contain two or three letters.
     * </p>
     */
    @Test
    void rejectsInvalidIcaoCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "LH", "D"));

        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "LH", "DLHH"));

        assertThrows(IllegalArgumentException.class,
                () -> new AirTransportCompany("Lufthansa", "LH", "D1H"));
    }
}
