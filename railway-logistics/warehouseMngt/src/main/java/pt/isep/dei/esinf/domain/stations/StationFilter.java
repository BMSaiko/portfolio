package pt.isep.dei.esinf.domain.stations;

/**
 * Filter criteria for station searches
 */
public class StationFilter {
    private Boolean isCity;
    private Boolean isMainStation;
    private String country;
    
    public StationFilter() {
        this.isCity = null;
        this.isMainStation = null;
        this.country = "all";
    }
    
    public Boolean getIsCity() {
        return isCity;
    }
    
    public void setIsCity(Boolean isCity) {
        this.isCity = isCity;
    }
    
    public Boolean getIsMainStation() {
        return isMainStation;
    }
    
    public void setIsMainStation(Boolean isMainStation) {
        this.isMainStation = isMainStation;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    /**
     * Checks if a station matches all active filter criteria
     * @param station the station to check
     * @return true if the station matches all filters, false otherwise
     */
    public boolean matches(Station station) {
        if (station == null) {
            return false;
        }

        if (isCity != null && station.isCity() != isCity) {
            return false;
        }
        
        if (isMainStation != null && station.isMainStation() != isMainStation) {
            return false;
        }
        
        if (country != null && !country.equalsIgnoreCase("all")) {
            if (station.getTimeZone() == null) {
                return false;
            }
            return station.getTimeZone().getCountry().equalsIgnoreCase(country);
        }
        
        return true;
    }
    
    /**
     * Checks if any filters are active
     * @return true if at least one filter is set, false otherwise
     */
    public boolean hasActiveFilters() {
        return isCity != null || isMainStation != null || (country != null && !country.equalsIgnoreCase("all"));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (isCity != null) {
            sb.append("Is city: ").append(isCity ? "Yes" : "No").append(", ");
        }
        if (isMainStation != null) {
            sb.append("Is Main Station: ").append(isMainStation ? "Yes" : "No").append(", ");
        }
        if (country != null && !country.equalsIgnoreCase("all")) {
            sb.append("Country: ").append(country);
        }

        return sb.toString();
    }
}
