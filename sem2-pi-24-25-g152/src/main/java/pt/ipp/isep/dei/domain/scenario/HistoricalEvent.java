package pt.ipp.isep.dei.domain.scenario;


import java.util.Date;

public class HistoricalEvent {
    private String name;
    private String description;
    private Date date;
    private String impact;

    public HistoricalEvent(String name, String description, Date date, String impact) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.impact = impact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}
