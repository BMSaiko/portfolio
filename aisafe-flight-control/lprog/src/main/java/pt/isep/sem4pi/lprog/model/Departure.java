package pt.isep.sem4pi.lprog.model;

import java.time.LocalDateTime;

public class Departure {
    private String airportCode;
    private LocalDateTime time;
    private int line;

    public Departure(String airportCode, LocalDateTime time, int line) {
        this.airportCode = airportCode;
        this.time = time;
        this.line = line;
    }

    public String getAirportCode() { return airportCode; }
    public LocalDateTime getTime() { return time; }
    public int getLine() { return line; }
}