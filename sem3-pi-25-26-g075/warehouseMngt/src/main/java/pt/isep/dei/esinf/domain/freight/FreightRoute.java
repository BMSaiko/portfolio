package pt.isep.dei.esinf.domain.freight;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a freight route with multiple stops
 */
public class FreightRoute {
    private String routeName;
    private String routeType;
    private List<RouteStop> stops;
    private int totalDistance;

    public FreightRoute(String routeName, String routeType) {
        this.routeName = routeName;
        this.routeType = routeType;
        this.stops = new ArrayList<>();
        this.totalDistance = 0;
    }

    public void addStop(RouteStop stop) {
        stops.add(stop);
    }

    public int getFreightCount() {
        return stops.stream()
                .mapToInt(stop -> stop.getFreightsToLoad().size())
                .sum();
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteType() {
        return routeType;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int distance) {
        this.totalDistance = distance;
    }

    public boolean isSimple() {
        return "SIMPLE".equalsIgnoreCase(routeType);
    }

    public boolean isComplex() {
        return "COMPLEX".equalsIgnoreCase(routeType);
    }
}

