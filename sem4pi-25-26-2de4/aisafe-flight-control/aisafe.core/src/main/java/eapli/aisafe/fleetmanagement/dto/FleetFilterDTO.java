package eapli.aisafe.fleetmanagement.dto;

import java.io.Serializable;

/**
 * DTO for filtering the fleet list via TCP.
 * US072 — List an air transport company's fleet with optional filters.
 */
public class FleetFilterDTO implements Serializable {
    public enum FilterType { ALL, BY_MODEL, BY_MAKER, BY_CAPACITY, BY_AGE }

    public final FilterType filterType;
    public final String filterValue; // maker name, model id, etc.
    public final int minCapacity;
    public final int maxAge;

    private FleetFilterDTO(FilterType filterType, String filterValue, int minCapacity, int maxAge) {
        this.filterType = filterType;
        this.filterValue = filterValue;
        this.minCapacity = minCapacity;
        this.maxAge = maxAge;
    }

    public static FleetFilterDTO all() {
        return new FleetFilterDTO(FilterType.ALL, null, 0, 0);
    }

    public static FleetFilterDTO byModel(String modelId) {
        return new FleetFilterDTO(FilterType.BY_MODEL, modelId, 0, 0);
    }

    public static FleetFilterDTO byMaker(String makerName) {
        return new FleetFilterDTO(FilterType.BY_MAKER, makerName, 0, 0);
    }

    public static FleetFilterDTO byCapacity(int minCapacity) {
        return new FleetFilterDTO(FilterType.BY_CAPACITY, null, minCapacity, 0);
    }

    public static FleetFilterDTO byAge(int maxAge) {
        return new FleetFilterDTO(FilterType.BY_AGE, null, 0, maxAge);
    }
}
