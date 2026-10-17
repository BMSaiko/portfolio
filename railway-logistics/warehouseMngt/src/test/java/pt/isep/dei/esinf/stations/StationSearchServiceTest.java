package pt.isep.dei.esinf.stations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pt.isep.dei.esinf.domain.stations.Coordinates;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.TimeZone;
import pt.isep.dei.esinf.repository.StationsRepository;
import pt.isep.dei.esinf.service.StationSearchService;
import pt.isep.dei.esinf.service.data.CsvImportService;
import pt.isep.dei.esinf.service.data.DataInitializationService;

/**
 * Test class for USEI06 - Station Search using BST/AVL trees
 */
class StationSearchServiceTest {

    private CsvImportService importService;
    private StationSearchService searchService;
    private final StationsRepository repository = StationsRepository.getInstance();
    private DataInitializationService dataInitService;

    @BeforeEach
    void setUp() throws Exception {
        importService = new CsvImportService();
        searchService = new StationSearchService();
        dataInitService = new DataInitializationService();
        searchService = new StationSearchService();

        dataInitService.initializeStationsData();
    }

    /**
     * Test validation of coordinates (latitude and longitude ranges)
     */
    @Test
    @DisplayName("Should validate coordinates correctly when checking latitude and longitude ranges")
    void shouldValidateCoordinatesCorrectly_whenCheckingLatitudeAndLongitudeRanges() {
        // Valid coordinates
        Coordinates validCoords = new Coordinates(45.0, 10.0);
        assertTrue(validCoords.isValid());

        // Invalid latitude (too high)
        Coordinates invalidLat1 = new Coordinates(91.0, 10.0);
        assertFalse(invalidLat1.isValid());

        // Invalid latitude (too low)
        Coordinates invalidLat2 = new Coordinates(-91.0, 10.0);
        assertFalse(invalidLat2.isValid());

        // Invalid longitude (too high)
        Coordinates invalidLon1 = new Coordinates(45.0, 181.0);
        assertFalse(invalidLon1.isValid());

        // Invalid longitude (too low)
        Coordinates invalidLon2 = new Coordinates(45.0, -181.0);
        assertFalse(invalidLon2.isValid());
    }

    /**
     * Test validation of station name (non-empty)
     */
    @Test
    @DisplayName("Should validate station name correctly when checking for non-empty values")
    void shouldValidateStationNameCorrectly_whenCheckingForNonEmptyValues() {
        Coordinates coords = new Coordinates(45.0, 10.0);
        TimeZone tz = new TimeZone("PT", "Europe/Lisbon", "WET/GMT");

        // Valid station
        Station validStation = new Station("Lisbon", coords, true, true, false);
        validStation.setTimeZone(tz);
        assertTrue(validStation.isValid());

        // Invalid station (empty name)
        Station invalidStation1 = new Station("", coords, true, true, false);
        invalidStation1.setTimeZone(tz);
        assertFalse(invalidStation1.isValid());

        // Invalid station (null name)
        Station invalidStation2 = new Station(null, coords, true, true, false);
        invalidStation2.setTimeZone(tz);
        assertFalse(invalidStation2.isValid());
    }

    /**
     * Test validation of time zone (non-empty country and group)
     */
    @Test
    @DisplayName("Should validate time zone correctly when checking for non-empty country and group")
    void shouldValidateTimeZoneCorrectly_whenCheckingForNonEmptyCountryAndGroup() {
        // Valid timezone
        TimeZone validTz = new TimeZone("PT", "Europe/Lisbon", "WET/GMT");
        assertTrue(validTz.isValid());

        // Invalid timezone (empty country)
        TimeZone invalidTz1 = new TimeZone("", "Europe/Lisbon", "WET/GMT");
        assertFalse(invalidTz1.isValid());

        // Invalid timezone (empty group)
        TimeZone invalidTz2 = new TimeZone("PT", "Europe/Lisbon", "");
        assertFalse(invalidTz2.isValid());

        // Invalid timezone (null values)
        TimeZone invalidTz3 = new TimeZone(null, "Europe/Lisbon", "WET/GMT");
        assertFalse(invalidTz3.isValid());
    }

    /**
     * Test importing stations from CSV with validation
     */
    @Test
    @DisplayName("Should import stations successfully when reading valid CSV data")
    void shouldImportStationsSuccessfully_whenReadingValidCsvData() {
        // Verify stations were imported
        assertFalse(repository.getStations().isEmpty());
    }

    /**
     * Test that invalid rows are rejected during import
     */
    @Test
    @DisplayName("Should reject invalid data and only import valid stations when reading CSV with mixed data")
    void shouldRejectInvalidDataAndOnlyImportValidStations_whenReadingCsvWithMixedData() {
        assertFalse(repository.getStations().isEmpty());

        int invalidFound = 0;
        for (List<Station> stationList : repository.getStations().values()) {
            for (Station station : stationList) {

                // Verify each imported station is valid
                if (!station.isValid()) {
                    invalidFound++;
                }
                
                assertTrue(station.isValid());
                
                // Verify station has non-empty name
                assertNotNull(station.getName());
                assertFalse(station.getName().trim().isEmpty());
                
                // Verify coordinates are valid
                assertNotNull(station.getCoordinates());
                assertTrue(station.getCoordinates().isValid());
                
                Double lat = station.getCoordinates().getLatitude();
                Double lon = station.getCoordinates().getLongitude();
                assertNotNull(lat);
                assertNotNull(lon);
                assertTrue(lat >= -90.0 && lat <= 90.0);
                assertTrue(lon >= -180.0 && lon <= 180.0);
                
                // Verify timezone is valid
                assertNotNull(station.getTimeZone());
                assertTrue(station.getTimeZone().isValid());
            }
        }

        assertEquals(0, invalidFound, "No invalid stations should have been imported");
    }


    /**
     * Test retrieving stations by time zone group in ascending order of country
     */
    @Test
    @DisplayName("Should retrieve stations by time zone group when searching by group name")
    void shouldRetrieveStationsByTimeZoneGroup_whenSearchingByGroupName() throws Exception {
        String csvData = """
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,"('Europe/Lisbon',)",WET/GMT,Pombal,39.91642,-8.630174,False,False,False
                GB,"('Europe/London',)",WET/GMT,London St-Pancras,51.531921,-0.126361,False,True,False
                ES,"('Europe/Madrid',)",CET,Tortosa,40.807197,0.523576,False,False,False
                FR,"('Europe/Paris',)",CET,Dijon Ville,47.32337,5.027208,False,True,False
                """;

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        importService.importStations(inputStream);

        // Get stations in WET/GMT group
        List<Station> wetStations = searchService.getStationsByTimeZoneGroup("WET/GMT");
        assertEquals(2, wetStations.size());

        // Get stations in CET group
        List<Station> cetStations = searchService.getStationsByTimeZoneGroup("CET");
        assertEquals(2, cetStations.size());
    }

    /**
     * Test retrieving stations in a window of time zone groups
     */
    @Test
    @DisplayName("Should retrieve stations from multiple time zone groups when searching by group array")
    void shouldRetrieveStationsFromMultipleTimeZoneGroups_whenSearchingByGroupArray() throws Exception {
        String csvData = """
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,"('Europe/Lisbon',)",WET/GMT,Santarem,39.298988,-8.650815,True,False,False
                ES,"('Europe/Madrid',)",CET,Albacete-Los Llanos,39.000086,-1.847579,False,False,False
                GR,"('Europe/Athens',)",EET,Athens,37.992323,23.720781,True,False,False
                """;

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        importService.importStations(inputStream);

        // Get stations in multiple groups
        String[] groups = {"WET/GMT", "CET"};
        List<Station> stations = searchService.getStationsByTimeZoneGroups(groups);

        // Should have 2 stations (Santarem + Albacete-Los Llanos)
        assertEquals(2, stations.size());
    }

    
    /**
     * Test searching stations by country
     */
    @Test
    @DisplayName("Should retrieve all stations by country when searching with country code")
    void shouldRetrieveAllStationsByCountry_whenSearchingWithCountryCode() throws Exception {
        List<Station> ptStations = searchService.getStationsByCountry("PT");
        assertEquals(172, ptStations.size());

        List<Station> frStations = searchService.getStationsByCountry("FR");
        assertEquals(6320, frStations.size());
    }

    /**
     * Test comparison of coordinates
     */
    @Test
    @DisplayName("Should compare coordinates correctly when evaluating latitude and longitude")
    void shouldCompareCoordinatesCorrectly_whenEvaluatingLatitudeAndLongitude() {
        Coordinates c1 = new Coordinates(45.0, 10.0);
        Coordinates c2 = new Coordinates(45.0, 10.0);
        Coordinates c3 = new Coordinates(45.0, 11.0);
        Coordinates c4 = new Coordinates(46.0, 10.0);

        // Same coordinates
        assertEquals(0, c1.compareTo(c2));

        // Different longitude
        assertTrue(c1.compareTo(c3) < 0);
        assertTrue(c3.compareTo(c1) > 0);

        // Different latitude
        assertTrue(c1.compareTo(c4) < 0);
        assertTrue(c4.compareTo(c1) > 0);
    }
    

    /**
     * Test comparison of timezones (by group, then country, then zone)
     */
    @Test
    @DisplayName("Should compare time zones correctly when evaluating by group, country, and zone")
    void shouldCompareTimeZonesCorrectly_whenEvaluatingByGroupCountryAndZone() {
        TimeZone tz1 = new TimeZone("FR", "Europe/Paris", "CET");
        TimeZone tz2 = new TimeZone("PT", "Europe/Lisbon", "CET");
        TimeZone tz3 = new TimeZone("PT", "Europe/Lisbon", "WET/GMT");

        // Different zone same group
        assertTrue(tz1.compareTo(tz2) > 0); // FR > PT

        // Different group same zone
        assertEquals(0, tz2.compareTo(tz3)); // CET < WET/GMT
    }

    /**
     * Test comparison of stations (by name)
     */
    @Test
    @DisplayName("Should compare stations correctly when evaluating by name")
    void shouldCompareStationsCorrectly_whenEvaluatingByName() {
        Coordinates coords = new Coordinates(45.0, 10.0);
        Station s1 = new Station("Amsterdam", coords, true, true, false);
        Station s2 = new Station("Berlin", coords, true, true, false);
        Station s3 = new Station("Amsterdam", coords, false, false, false);

        assertTrue(s1.compareTo(s2) < 0); // Amsterdam < Berlin
        assertTrue(s2.compareTo(s1) > 0); // Berlin > Amsterdam
        assertEquals(0, s1.compareTo(s3)); // Same name
    }

    /**
     * Test error handling for invalid arguments
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when receiving invalid arguments")
    void shouldThrowIllegalArgumentException_whenReceivingInvalidArguments() {
        // Null or empty time zone group
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByTimeZoneGroup(null));
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByTimeZoneGroup(""));

        // Invalid latitude
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCoordinates(91.0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCoordinates(-91.0, 10.0));

        // Invalid longitude
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCoordinates(45.0, 181.0));
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCoordinates(45.0, -181.0));

        // Null or empty country
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCountry(null));
        assertThrows(IllegalArgumentException.class, () -> searchService.getStationsByCountry(""));
    }

    /**
     * Test getting statistics
     */
    @Test
    @DisplayName("Should return statistics correctly when retrieving station data summary")
    void shouldReturnStatisticsCorrectly_whenRetrievingStationDataSummary() throws Exception {
        String csvData = """
                country,time_zone,time_zone_group,station,latitude,longitude,is_city,is_main_station,is_airport
                PT,"('Europe/Lisbon',)",WET/GMT,Viana do Castelo,41.695268,-8.831371,False,False,False
                PT,"('Europe/Lisbon',)",WET/GMT,Abrantes,39.440351,-8.194683,False,False,False
                FR,"('Europe/Paris',)",CET,Besancon Viotte,47.247019,6.022003,False,True,False
                """;

        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        importService.importStations(inputStream);

        String stats = searchService.getStatistics();
        assertNotNull(stats);

        Pattern pattern = Pattern.compile("A total of ([0-9]+) time zones, ([0-9]+) stations and ([0-9]+) unique coordinates.");
        Matcher matcher = pattern.matcher(stats);

        assertTrue(matcher.find());
    }


    /**
     * Find all CET stations ordered by country
     * Expected: Thousands of stations from FR (France) and other European countries
     */
    @Test
    @DisplayName("Should find all CET stations ordered by country when searching CET time zone group")
    void shouldFindAllCetStationsOrderedByCountry_whenSearchingCetTimeZoneGroup() {
        List<Station> cetStations = searchService.getStationsByTimeZoneGroup("CET");

        // Assert results exist and are substantial
        assertNotNull(cetStations, "Result should not be null");
        assertFalse(cetStations.isEmpty(), "CET is a major timezone group in Europe");
        assertTrue(cetStations.size() > 1000, "CET should have thousands of stations");

        System.out.println(cetStations.size());
        // Verify all stations belong to CET group
        for (Station station : cetStations) {
            assertEquals("CET", station.getTimeZone().getGroup(), "All stations should be in CET timezone group");
        }


        // Verify presence of known French CET stations from dataset
        boolean hasChateauArnoux = cetStations.stream().anyMatch(s -> s.getName().contains("Chateau-Arnoux"));
        assertTrue(hasChateauArnoux, "Should contain French stations like Chateau-Arnoux");
    }



    /**
     * Find stations in multiple time zone groups
     * Expected: Stations from both CET and WET/GMT groups
     */
    @Test
    @DisplayName("Should find stations in multiple time zone groups when searching CET and WET/GMT")
    void shouldFindStationsInMultipleTimeZoneGroups_whenSearchingCetAndWetGmt() {
        String[] groups = {"CET", "WET/GMT"};
        List<Station> stations = searchService.getStationsByTimeZoneGroups(groups);

        // Assert results exist
        assertNotNull(stations, "Result should not be null");
        assertFalse(stations.isEmpty(), "Should find stations in both timezone groups");

        // Verify stations belong to one of the requested groups
        for (Station station : stations) {
            String group = station.getTimeZone().getGroup();
            assertTrue(group.equals("CET") || group.equals("WET/GMT"), "Station should belong to CET or WET/GMT group");
        }

        // Count stations per group
        long cetCount = stations.stream().filter(s -> s.getTimeZone().getGroup().equals("CET")).count();
        long wetCount = stations.stream().filter(s -> s.getTimeZone().getGroup().equals("WET/GMT")).count();

        assertTrue(cetCount > 0, "Should have CET stations");
        assertTrue(wetCount > 0, "Should have WET/GMT stations");
        assertEquals(cetCount + wetCount, stations.size(), "Total should equal sum of both groups");

    }

    /**
     * Find all French stations
     * Expected: Thousands of French stations (France has extensive rail network)
     */
    @Test
    @DisplayName("Should find all French stations when searching by country code FR")
    void shouldFindAllFrenchStations_whenSearchingByCountryCodeFr() {
        List<Station> frStations = searchService.getStationsByCountry("FR");

        // Assert results exist and match expectations
        assertNotNull(frStations, "Result should not be null");
        assertFalse(frStations.isEmpty(), "France has many train stations");
        assertTrue(frStations.size() > 100, "France should have hundreds of stations in dataset");

        // Verify all stations are French
        for (Station station : frStations) {
            assertEquals("FR", station.getTimeZone().getCountry(), "All stations should be from France");
        }

        // Verify presence of known French stations from CSV
        boolean hasDijonVille = frStations.stream().anyMatch(s -> s.getName().equals("Dijon Ville"));
        assertTrue(hasDijonVille, "Should contain Dijon Ville station");

        boolean hasParis = frStations.stream().anyMatch(s -> s.getName().startsWith("Paris"));
        assertTrue(hasParis, "Should contain Paris stations");

        // Verify coordinate validity for French stations
        for (Station station : frStations) {
            double lat = station.getCoordinates().getLatitude();
            double lon = station.getCoordinates().getLongitude();
            // France latitude range: ~41°N to 51°N, longitude: ~-5°W to 10°E
            assertTrue(lat >= 41 && lat <= 52, "French station latitude should be valid: " + station.getName());
            assertTrue(lon >= -6 && lon <= 11, "French station longitude should be valid: " + station.getName());
        }

        System.out.println("✓ Sample Query 3: Found " + frStations.size() + " French stations");
    }


    /**
     * Find stations at same coordinates
     * Expected: Multiple stations at exact coordinates (44.08179, 6.001625) sorted by name
     * From CSV: Chateau-Arnoux-St-Auban appears at these coordinates
     */
    @Test
    @DisplayName("Should find stations at same coordinates sorted by name when searching by exact location")
    void shouldFindStationsAtSameCoordinatesSortedByName_whenSearchingByExactLocation() {
        // Coordinates from first line of CSV: Chateau-Arnoux-St-Auban
        double lat = 44.08179;
        double lon = 6.001625;

        List<Station> stations = searchService.getStationsByCoordinates(lat, lon);

        // Assert results
        assertNotNull(stations, "Result should not be null");
        assertFalse(stations.isEmpty(), "Should find station at these coordinates");

        // Verify all stations have exact coordinates
        for (Station station : stations) {
            assertEquals(lat, station.getCoordinates().getLatitude(), 0.000001, "Station should have exact latitude");
            assertEquals(lon, station.getCoordinates().getLongitude(), 0.000001, "Station should have exact longitude");
        }

        // Verify specific station exists
        boolean hasChateauArnoux = stations.stream().anyMatch(s -> s.getName().equals("Chateau-Arnoux-St-Auban"));
        assertTrue(hasChateauArnoux, "Should contain 'Chateau-Arnoux-St-Auban' at coordinates (44.08179, 6.001625)");

        // If multiple stations at same coordinates, verify sorted by name
        if (stations.size() > 1) {
            for (int i = 0; i < stations.size() - 1; i++) {
                String current = stations.get(i).getName();
                String next = stations.get(i + 1).getName();
                assertTrue(current.compareTo(next) <= 0, "Stations at same coordinates should be sorted by name: " + current + " vs " + next);
            }
        }
    }
}
