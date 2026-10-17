package pt.isep.dei.esinf.domain.freight;

import pt.isep.dei.esinf.domain.railway.Facility;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stop in a freight route with load/unload operations
 */
public class RouteStop {
    private int stopOrder;
    private Facility facility;
    private List<Freight> freightsToLoad;
    private List<Freight> freightsToUnload;
    private int cumulativeDistance;

    public RouteStop(int stopOrder, Facility facility) {
        this.stopOrder = stopOrder;
        this.facility = facility;
        this.freightsToLoad = new ArrayList<>();
        this.freightsToUnload = new ArrayList<>();
        this.cumulativeDistance = 0;
    }

    public void addFreightToLoad(Freight freight) {
        freightsToLoad.add(freight);
    }

    public void addFreightToUnload(Freight freight) {
        freightsToUnload.add(freight);
    }

    // Getters
    public int getStopOrder() {
        return stopOrder;
    }

    public Facility getFacility() {
        return facility;
    }

    public List<Freight> getFreightsToLoad() {
        return freightsToLoad;
    }

    public List<Freight> getFreightsToUnload() {
        return freightsToUnload;
    }

    public int getCumulativeDistance() {
        return cumulativeDistance;
    }

    public void setCumulativeDistance(int distance) {
        this.cumulativeDistance = distance;
    }

    public boolean hasLoadOperations() {
        return !freightsToLoad.isEmpty();
    }

    public boolean hasUnloadOperations() {
        return !freightsToUnload.isEmpty();
    }
}

