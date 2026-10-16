package pt.isep.dei.esinf.domain.stations;

public class Coordinates implements Comparable<Coordinates> {
    private Double latitude;
    private Double longitude;

    public Coordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(latitude);
        result = 31 * result + Double.hashCode(longitude);
        return result;
    }

    @Override
    public String toString() {
        return "(" + latitude + ", " + longitude + ")";
    }

    @Override
    public int compareTo(Coordinates o) {
        int latCompare = Double.compare(this.latitude, o.latitude);
        if (latCompare != 0) return latCompare;
        
        return Double.compare(this.longitude, o.longitude);
    }
    
    /**
     * Validates if coordinates are within valid ranges
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (latitude == null || longitude == null) return false;
        return latitude >= -90.0 && latitude <= 90.0 && longitude >= -180.0 && longitude <= 180.0;
    }
}
