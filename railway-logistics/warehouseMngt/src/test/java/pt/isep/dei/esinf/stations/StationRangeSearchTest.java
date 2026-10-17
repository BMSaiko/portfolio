package pt.isep.dei.esinf.stations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.StationFilter;
import pt.isep.dei.esinf.service.StationSearchService;
import pt.isep.dei.esinf.service.data.DataInitializationService;

/**
 * Test class for USEI08 - KD-Tree range search functionality
 * Uses actual data from train_stations_europe.csv dataset
 */
class StationRangeSearchTest {

    private static StationSearchService service;

    @BeforeAll
    static void setUp() throws Exception {
        // Load actual data from CSV file
        DataInitializationService dataService = new DataInitializationService();
        dataService.initializeStationsData();
        
        service = new StationSearchService();
    }

    // ========== RANGE SEARCH TESTS WITH ACTUAL DATASET ==========

    @Test
    @DisplayName("Range search: Central Europe (lat 48-53, lon 5-10) - Main stations only - Should return 101 stations")
    void testRangeSearch_CentralEurope_MainStationsOnly() {
        StationFilter filter = new StationFilter();
        filter.setIsMainStation(true);
        
        List<Station> results = service.getStationsByRange(48.0, 53.0, 5.0, 10.0, filter);

        assertEquals(101, results.size(), "Should find exactly 101 main stations in Central Europe");
        
        // Verify all results are main stations
        for (Station station : results) {
            assertTrue(station.isMainStation(), "All results should be main stations");
        }
        
        // Verify coordinates are within range
        for (Station station : results) {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 48.0 && lat <= 53.0, "Latitude should be in range [48, 53]");
            assertTrue(lon >= 5.0 && lon <= 10.0, "Longitude should be in range [5, 10]");
        }
    }

    @Test
    @DisplayName("Range search: France region (lat 43-51, lon -5-8) - No filter - Should find multiple stations")
    void testRangeSearch_FranceRegion_NoFilter() {
        List<Station> results = service.getStationsByRange(43.0, 51.0, -5.0, 8.0, null);

        assertFalse(results.isEmpty(), "Should find stations in France region");
        
        // Verify coordinates are within range
        for (Station station : results) {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 43.0 && lat <= 51.0, "Latitude should be in range");
            assertTrue(lon >= -5.0 && lon <= 8.0, "Longitude should be in range");
        }
        
        // Verify results are sorted by name
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getName().compareTo(results.get(i + 1).getName()) <= 0,
                "Results should be sorted by name");
        }
    }

    @Test
    @DisplayName("Range search: Germany-France border (lat 48-50, lon 7-8) - City stations only")
    void testRangeSearch_GermanyFranceBorder_CityStationsOnly() {
        StationFilter filter = new StationFilter();
        filter.setIsCity(true);
        
        List<Station> results = service.getStationsByRange(48.0, 50.0, 7.0, 8.0, filter);

        assertFalse(results.isEmpty(), "Should find city stations in Germany-France border region");
        
        // Verify all results are city stations
        for (Station station : results) {
            assertTrue(station.isCity(), "All results should be city stations");
        }
    }

    @Test
    @DisplayName("Range search: Northern France (lat 48-52, lon 2-7) - Country filter FR")
    void testRangeSearch_NorthernFrance_CountryFilter() {
        StationFilter filter = new StationFilter();
        filter.setCountry("FR");
        
        List<Station> results = service.getStationsByRange(48.0, 52.0, 2.0, 7.0, filter);

        assertFalse(results.isEmpty(), "Should find French stations");
        
        // Verify all results are from France
        for (Station station : results) {
            assertEquals("FR", station.getTimeZone().getCountry(), "All stations should be from France");
        }
    }

    @Test
    @DisplayName("Range search: Small region around Strasbourg (lat 48.5-48.6, lon 7.7-7.8)")
    void testRangeSearch_SmallRegion_Strasbourg() {
        List<Station> results = service.getStationsByRange(48.5, 48.6, 7.7, 7.8, null);

        // Should find at least Strasbourg station
        assertTrue(results.stream().anyMatch(s -> s.getName().contains("Strasbourg")),
            "Should find Strasbourg station");
        
        // Verify coordinates are within tight range
        for (Station station : results) {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            assertTrue(lat >= 48.5 && lat <= 48.6, "Latitude should be in range [48.5, 48.6]");
            assertTrue(lon >= 7.7 && lon <= 7.8, "Longitude should be in range [7.7, 7.8]");
        }
    }

    @Test
    @DisplayName("Range search: Atlantic Ocean region - Should return empty list")
    void testRangeSearch_EmptyRegion_AtlanticOcean() {
        List<Station> results = service.getStationsByRange(30.0, 35.0, -30.0, -20.0, null);

        assertTrue(results.isEmpty(), "Should find no stations in Atlantic Ocean");
    }

    @Test
    @DisplayName("Range search: Single point at exact coordinates - Boundary inclusive test")
    void testRangeSearch_SinglePoint_BoundaryInclusive() {
        // Dijon Ville is at 47.32337, 5.027208
        List<Station> results = service.getStationsByRange(47.32337, 47.32337, 5.027208, 5.027208, null);

        assertFalse(results.isEmpty(), "Should find station at exact coordinates");
        assertTrue(results.stream().anyMatch(s -> s.getName().contains("Dijon")),
            "Should find Dijon station");
    }

    @Test
    @DisplayName("Range search: Large Europe region (lat 40-55, lon -10-20) - No filter")
    void testRangeSearch_LargeRegion_AllEurope() {
        List<Station> results = service.getStationsByRange(40.0, 55.0, -10.0, 20.0, null);

        assertFalse(results.isEmpty(), "Should find many stations across Europe");
        
        // Verify results are sorted
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getName().compareTo(results.get(i + 1).getName()) <= 0,
                "Results should be sorted by name");
        }
    }

    // ========== VALIDATION AND EXCEPTION TESTS ==========

    @Test
    @DisplayName("Validation: Invalid minimum latitude below -90 - Should throw IllegalArgumentException")
    void testValidation_InvalidMinLatitudeBelowRange() {
        StationFilter filter = new StationFilter();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(-100.0, 50.0, -10.0, 10.0, filter)
        );
        
        assertTrue(exception.getMessage().contains("Min latitude"),
            "Exception message should mention min latitude");
    }

    @Test
    @DisplayName("Validation: Invalid maximum latitude above 90 - Should throw IllegalArgumentException")
    void testValidation_InvalidMaxLatitudeAboveRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(40.0, 100.0, -10.0, 10.0, null)
        );
        
        assertTrue(exception.getMessage().contains("Max latitude"),
            "Exception message should mention max latitude");
    }

    @Test
    @DisplayName("Validation: Invalid minimum longitude below -180 - Should throw IllegalArgumentException")
    void testValidation_InvalidMinLongitudeBelowRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(40.0, 50.0, -200.0, 10.0, null)
        );
        
        assertTrue(exception.getMessage().contains("Min longitude"),
            "Exception message should mention min longitude");
    }

    @Test
    @DisplayName("Validation: Invalid maximum longitude above 180 - Should throw IllegalArgumentException")
    void testValidation_InvalidMaxLongitudeAboveRange() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(40.0, 50.0, -10.0, 200.0, null)
        );
        
        assertTrue(exception.getMessage().contains("Max longitude"),
            "Exception message should mention max longitude");
    }

    @Test
    @DisplayName("Validation: Minimum latitude greater than maximum - Should throw IllegalArgumentException")
    void testValidation_MinLatitudeGreaterThanMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(60.0, 40.0, -10.0, 10.0, null)
        );
        
        assertTrue(exception.getMessage().contains("Min latitude must be less or equal"),
            "Exception message should mention latitude range issue");
    }

    @Test
    @DisplayName("Validation: Minimum longitude greater than maximum - Should throw IllegalArgumentException")
    void testValidation_MinLongitudeGreaterThanMax() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            service.getStationsByRange(40.0, 50.0, 20.0, 10.0, null)
        );
        
        assertTrue(exception.getMessage().contains("Min longitude must be less or equal"),
            "Exception message should mention longitude range issue");
    }

    @Test
    @DisplayName("Validation: Valid edge case latitude exactly -90 - Should not throw exception")
    void testValidation_ValidEdgeCase_MinLatitude() {
        assertDoesNotThrow(() ->
            service.getStationsByRange(-90.0, 0.0, -10.0, 10.0, null)
        );
    }

    @Test
    @DisplayName("Validation: Valid edge case latitude exactly 90 - Should not throw exception")
    void testValidation_ValidEdgeCase_MaxLatitude() {
        assertDoesNotThrow(() ->
            service.getStationsByRange(0.0, 90.0, -10.0, 10.0, null)
        );
    }

    @Test
    @DisplayName("Validation: Valid edge case longitude exactly -180 - Should not throw exception")
    void testValidation_ValidEdgeCase_MinLongitude() {
        assertDoesNotThrow(() ->
            service.getStationsByRange(40.0, 50.0, -180.0, 10.0, null)
        );
    }

    @Test
    @DisplayName("Validation: Valid edge case longitude exactly 180 - Should not throw exception")
    void testValidation_ValidEdgeCase_MaxLongitude() {
        assertDoesNotThrow(() ->
            service.getStationsByRange(40.0, 50.0, -10.0, 180.0, null)
        );
    }

    @Test
    @DisplayName("Filter: Null filter should work - Returns all stations in range")
    void testFilter_NullFilter_ReturnsAllStations() {
        List<Station> results = service.getStationsByRange(48.0, 49.0, 5.0, 6.0, null);

        assertNotNull(results, "Result should not be null even with null filter");
    }

    @Test
    @DisplayName("Filter: Empty filter should work - Returns all stations in range")
    void testFilter_EmptyFilter_ReturnsAllStations() {
        StationFilter emptyFilter = new StationFilter();
        List<Station> results = service.getStationsByRange(48.0, 49.0, 5.0, 6.0, emptyFilter);

        assertNotNull(results, "Result should not be null with empty filter");
    }

    @Test
    @DisplayName("Results: Should always be sorted by station name in ascending order")
    void testResults_AlwaysSortedByName() {
        List<Station> results = service.getStationsByRange(45.0, 50.0, 0.0, 10.0, null);

        assertFalse(results.isEmpty(), "Should have results to verify sorting");
        
        for (int i = 0; i < results.size() - 1; i++) {
            String currentName = results.get(i).getName();
            String nextName = results.get(i + 1).getName();
            assertTrue(currentName.compareTo(nextName) <= 0,
                String.format("Results should be sorted: '%s' should come before or equal to '%s'",
                    currentName, nextName));
        }
    }
}
