package pt.ipp.isep.dei.domain.scenario;

import java.util.Date;
import java.util.List;

import pt.ipp.isep.dei.domain.locomotive.LocomotiveType;

public class Scenario {
    private String name;
    private Date startTime;
    private Date endTime;
    private List<LocomotiveType> allowedLocomotiveTypes;
    private List<IndustryType> allowedIndustryTypes;
    private List<HistoricalEvent> historicalEvents;

    public Scenario(String name, Date startTime, Date endTime,
                    List<LocomotiveType> allowedLocomotiveTypes, List<IndustryType> allowedIndustryTypes,
                    List<HistoricalEvent> historicalEvents) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allowedLocomotiveTypes = allowedLocomotiveTypes;
        this.allowedIndustryTypes = allowedIndustryTypes;
        this.historicalEvents = historicalEvents;
    }

    public String getName() {
        return name;
    }

    public boolean validateData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<LocomotiveType> getAllowedLocomotiveTypes() {
        return allowedLocomotiveTypes;
    }

    public void setAllowedLocomotiveTypes(List<LocomotiveType> allowedLocomotiveTypes) {
        this.allowedLocomotiveTypes = allowedLocomotiveTypes;
    }

    public List<IndustryType> getAllowedIndustryTypes() {
        return allowedIndustryTypes;
    }

    public void setAllowedIndustryTypes(List<IndustryType> allowedIndustryTypes) {
        this.allowedIndustryTypes = allowedIndustryTypes;
    }

    public List<HistoricalEvent> getHistoricalEvents() {
        return historicalEvents;
    }

    public void setHistoricalEvents(List<HistoricalEvent> historicalEvents) {
        this.historicalEvents = historicalEvents;
    }
}
