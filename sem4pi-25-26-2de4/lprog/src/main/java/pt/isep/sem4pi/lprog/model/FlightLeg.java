package pt.isep.sem4pi.lprog.model;

import java.util.ArrayList;
import java.util.List;

public class FlightLeg {
    private Departure departure;
    private Arrival arrival;
    private Route route;
    private List<FlightSegment> segments = new ArrayList<>();
    private Fuel fuel;

    public Departure getDeparture() { return departure; }
    public void setDeparture(Departure departure) { this.departure = departure; }

    public Arrival getArrival() { return arrival; }
    public void setArrival(Arrival arrival) { this.arrival = arrival; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public List<FlightSegment> getSegments() { return segments; }
    public void addSegment(FlightSegment segment) { this.segments.add(segment); }

    public Fuel getFuel() { return fuel; }
    public void setFuel(Fuel fuel) { this.fuel = fuel; }
}