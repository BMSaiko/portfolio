package pt.isep.dei.esinf.service;

import java.util.ArrayList;
import java.util.List;

import pt.isep.dei.esinf.domain.stations.CoordinateStationKey;
import pt.isep.dei.esinf.domain.stations.Coordinates;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.StationFilter;
import pt.isep.dei.esinf.domain.stations.TimeZone;
import pt.isep.dei.esinf.repository.StationsRepository;


public class StationSearchService {

    private final StationsRepository repository;

    public StationSearchService() {
        this.repository = StationsRepository.getInstance();
    }

    /**
     * Retrieves all stations for a specific time zone group, ordered by country ascending.
     *
     * @param group the time zone group (e.g., "CET", "WET/GMT")
     * @return list of stations in ascending order of country
     */
    public List<Station> getStationsByTimeZoneGroup(String group) {
        if (group == null || group.trim().isEmpty()) {
            throw new IllegalArgumentException("Time zone group cannot be null or empty");
        }

        List<Station> result = new ArrayList<>();

        for (TimeZone timeZone : repository.getStationsBST().inOrder()) {
            if (timeZone.getGroup().equalsIgnoreCase(group)) {
                List<Station> stations = repository
                        .getStations()
                        .get(timeZone);
                        
                result.addAll(stations);
            }
        }
        
        result.sort(Station::compareTo);
        return result;
    }

    /**
     * Retrieves all stations within a window of time zone groups, ordered by country ascending.
     * Example: ['CET', 'WET/GMT']
     *
     * @param groups array of time zone groups
     * @return list of stations in these groups, ordered by country
     */
    public List<Station> getStationsByTimeZoneGroups(String[] groups) {
        if (groups == null || groups.length == 0) {
            throw new IllegalArgumentException("Groups array cannot be null or empty");
        }

        List<Station> result = new ArrayList<>();

        for (String group : groups) {
            result.addAll(getStationsByTimeZoneGroup(group));
        }

        return result;
    }

    /**
     * Retrieves all stations at specific coordinates, sorted by name in ascending order.
     * Handles cases where multiple stations share the same coordinates
     * (e.g., Lisbon Santa Apolónia and Lisbon Oriente).
     * 
     * Uses BST lookup for O(log n) performance instead of O(n) iteration.
     *
     * @param latitude  the latitude (-90 to 90)
     * @param longitude the longitude (-180 to 180)
     * @return list of stations at these coordinates, sorted by name
     */
    public List<Station> getStationsByCoordinates(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be in range [-90, 90], got: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be in range [-180, 180], got: " + longitude);
        }

        List<Station> result = new ArrayList<>();
        Coordinates givenCoordinates = new Coordinates(latitude, longitude);

        for (CoordinateStationKey key : repository.getCoordinatesBST().inOrder()) {
            if (key.getCoordinates().equals(givenCoordinates)) {
                result.add(key.getStation());
            }
        }

        result.sort(Station::compareTo);
        return result;
    }

    /**
     * Retrieves all stations in a specific country, sorted by name in ascending order.
     * 
     * Uses BST in-order traversal for efficient access.
     *
     * @param country the country code or name
     * @return list of stations in that country, sorted by name
     */
    public List<Station> getStationsByCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        List<Station> result = new ArrayList<>();

        for (TimeZone timeZone : repository.getStationsBST().inOrder()) {
            if (timeZone.getCountry().equalsIgnoreCase(country)) {
                List<Station> stations = repository
                        .getStations()
                        .get(timeZone);

                result.addAll(stations);
            }
        }

        result.sort(Station::compareTo);
        return result;
    }


    /**
     * Gets statistics about the stations in the repository.
     *
     * @return statistics string
     */
    public String getStatistics() {
        int totalTimeZones = repository
                .getStations()
                .size();

        int totalStations = repository
                .getStations()
                .values()
                .stream()
                .mapToInt(List::size)
                .sum();

        long uniqueCoordinates = repository
                .getStations()
                .values()
                .stream()
                .flatMap(List::stream)
                .map(Station::getCoordinates)
                .distinct()
                .count();

        return String.format("A total of %d time zones, %d stations and %d unique coordinates.", totalTimeZones, totalStations, uniqueCoordinates);
    }

    /**
     * Performs a range query on the KD-tree to find all stations within a specified
     * latitude/longitude bounding box with optional filters.
     *
     * @param latMin  minimum latitude (inclusive)
     * @param latMax  maximum latitude (inclusive)
     * @param lonMin  minimum longitude (inclusive)
     * @param lonMax  maximum longitude (inclusive)
     * @param filter  optional filter criteria (can be null)
     * @return list of stations within the range that match the filter criteria
     */
    public List<Station> getStationsByRange(double latMin, double latMax, double lonMin, double lonMax, StationFilter filter) {
        if (latMin < -90.0 || latMin > 90.0) {
            throw new IllegalArgumentException("Min latitude must be in range [-90, 90], got: " + latMin);
        }
        if (latMax < -90.0 || latMax > 90.0) {
            throw new IllegalArgumentException("Max latitude must be in range [-90, 90], got: " + latMax);
        }
        if (lonMin < -180.0 || lonMin > 180.0) {
            throw new IllegalArgumentException("Min longitude must be in range [-180, 180], got: " + lonMin);
        }
        if (lonMax < -180.0 || lonMax > 180.0) {
            throw new IllegalArgumentException("Max longitude must be in range [-180, 180], got: " + lonMax);
        }
        if (latMin > latMax) {
            throw new IllegalArgumentException("Min latitude must be less or equal than max latitude");
        }
        if (lonMin > lonMax) {
            throw new IllegalArgumentException("Min longitude must be less or equal than max longitude");
        }

        List<Station> results;

        if (filter != null && filter.hasActiveFilters()) {
            results = repository.getKdTree().rangeSearch(latMin, latMax, lonMin, lonMax, filter::matches);
        } else {
            results = repository.getKdTree().rangeSearch(latMin, latMax, lonMin, lonMax);
        }

        results.sort(Station::compareTo);
        return results;
    }
}
