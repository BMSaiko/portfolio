package pt.isep.dei.esinf.domain.railway.upgrade;

import pt.isep.dei.esinf.domain.stations.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of upgrade plan computation
 */
public class UpgradePlanResult {
    private boolean hasCycles;
    private List<Station> topologicalOrder;
    private List<Station> cycleStations;
    private List<String> cycleEdges;
    private int verticesCount;
    private int edgesCount;

    public UpgradePlanResult() {
        this.topologicalOrder = new ArrayList<>();
        this.cycleStations = new ArrayList<>();
        this.cycleEdges = new ArrayList<>();
    }

    public boolean isHasCycles() { return hasCycles; }
    public void setHasCycles(boolean hasCycles) { this.hasCycles = hasCycles; }

    public List<Station> getTopologicalOrder() { return topologicalOrder; }
    public void setTopologicalOrder(List<Station> topologicalOrder) {
        this.topologicalOrder = topologicalOrder;
    }

    public List<Station> getCycleStations() { return cycleStations; }
    public void setCycleStations(List<Station> cycleStations) {
        this.cycleStations = cycleStations;
    }

    public List<String> getCycleEdges() { return cycleEdges; }
    public void setCycleEdges(List<String> cycleEdges) {
        this.cycleEdges = cycleEdges;
    }

    public int getVerticesCount() { return verticesCount; }
    public void setVerticesCount(int verticesCount) { this.verticesCount = verticesCount; }

    public int getEdgesCount() { return edgesCount; }
    public void setEdgesCount(int edgesCount) { this.edgesCount = edgesCount; }

    @Override
    public String toString() {
        if (hasCycles) {
            return String.format("UpgradePlanResult{hasCycles=true, cycles=%d, vertices=%d, edges=%d}",
                    cycleStations.size(), verticesCount, edgesCount);
        } else {
            return String.format("UpgradePlanResult{hasCycles=false, orderSize=%d, vertices=%d, edges=%d}",
                    topologicalOrder.size(), verticesCount, edgesCount);
        }
    }
}