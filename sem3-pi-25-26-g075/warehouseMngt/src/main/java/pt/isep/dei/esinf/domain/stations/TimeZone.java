package pt.isep.dei.esinf.domain.stations;

public class TimeZone implements Comparable<TimeZone> {
    private String country;
    private String zone;
    private String group;


    public TimeZone(String country, String zone, String group) {
        this.country = country;
        this.zone = zone;
        this.group = group;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TimeZone timeZone = (TimeZone) o;
        return country.equals(timeZone.country) && zone.equals(timeZone.zone) && group.equals(timeZone.group);
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + zone.hashCode();
        result = 31 * result + group.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TimeZone{" +
                "country='" + country + '\'' +
                ", zone='" + zone + '\'' +
                ", group='" + group + '\'' +
                '}';
    }

    @Override
    public int compareTo(TimeZone other) {
        return this.zone.compareTo(other.zone);
    }
    
    /**
     * Validates if timezone has valid data
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return country != null &&
                !country.trim().isEmpty() &&
                zone != null &&
                !zone.trim().isEmpty() &&
                group != null &&
                !group.trim().isEmpty();
    }
}
