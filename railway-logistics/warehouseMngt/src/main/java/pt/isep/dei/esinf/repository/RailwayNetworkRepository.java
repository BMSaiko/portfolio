package pt.isep.dei.esinf.repository;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.utils.interfaces.Graph;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;

/**
 * Repository for managing railway network data (stations and track connections)
 */
public class RailwayNetworkRepository {

    private static RailwayNetworkRepository instance = null;
    private Graph<Station, TrackConnection> networkGraph;
    private boolean dataLoaded = false;

    private RailwayNetworkRepository() {
        this.networkGraph = new MapGraph<>(true);
    }

    /**
     * Get singleton instance
     */
    public static RailwayNetworkRepository getInstance() {
        if (instance == null) {
            instance = new RailwayNetworkRepository();
        }
        return instance;
    }

    /**
     * Get the railway network graph
     */
    public Graph<Station, TrackConnection> getNetworkGraph() {
        return networkGraph;
    }

    /**
     * Set the railway network graph
     */
    public void setNetworkGraph(Graph<Station, TrackConnection> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        this.networkGraph = graph;
        this.dataLoaded = graph.numVertices() > 0;
    }

    /**
     * Check if data is loaded
     */
    public boolean isDataLoaded() {
        return dataLoaded && networkGraph.numVertices() > 0;
    }

    /**
     * Clear the repository
     */
    public void clear() {
        this.networkGraph = new MapGraph<>(true);
        this.dataLoaded = false;
    }

    /**
     * Add a station to the network
     */
    public void addStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }
        networkGraph.addVertex(station);
    }

    /**
     * Add a track connection between two stations
     */
    public void addConnection(Station from, Station to, TrackConnection connection) {
        if (from == null || to == null || connection == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        if (!networkGraph.validVertex(from)) {
            networkGraph.addVertex(from);
        }
        if (!networkGraph.validVertex(to)) {
            networkGraph.addVertex(to);
        }
        
        networkGraph.addEdge(from, to, connection);
        this.dataLoaded = true;
    }

    /**
     * Get the number of stations in the network
     */
    public int getStationCount() {
        return networkGraph.numVertices();
    }

    /**
     * Get the number of connections in the network
     */
    public int getConnectionCount() {
        return networkGraph.numEdges();
    }

    /**
     * Get all stations in the network
     */
    public Iterable<Station> getAllStations() {
        return networkGraph.vertices();
    }
}
