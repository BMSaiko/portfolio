package pt.isep.dei.esinf.service.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;
import pt.isep.dei.esinf.utils.interfaces.Graph;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;

/**
 * Service responsible for initializing the application data from CSV files
 */
public class DataInitializationService {

    private final CsvImportService csvImportService;
    private final WarehouseAllocator warehouseAllocator;
    private final RailwayNetworkRepository railwayRepository;

    public DataInitializationService() {
        this.csvImportService = new CsvImportService();
        this.warehouseAllocator = new WarehouseAllocator();
        this.railwayRepository = RailwayNetworkRepository.getInstance();
    }

    /**
     * Initializes the warehouse with data from CSV files
     *
     * @return initialized warehouse
     * @throws Exception if initialization fails
     */
    public Warehouse initializeWarehouse() throws Exception {
        Warehouse warehouse = new Warehouse();


        InputStream itemsIS = getClass().getResourceAsStream("/data/items.csv");
        InputStream baysIs = getClass().getResourceAsStream("/data/bays.csv");
        InputStream wagonsIs = getClass().getResourceAsStream("/data/wagons.csv");
        InputStream orderIs = getClass().getResourceAsStream("/data/orders.csv");
        InputStream orderLinesIs = getClass().getResourceAsStream("/data/order_lines.csv");


        csvImportService.importItems(itemsIS);

        csvImportService.importBays(baysIs);
        csvImportService.getBays().values().stream().forEach(warehouse::addBay);

        csvImportService.importWagons(wagonsIs);

        warehouseAllocator.allocateBoxesToWarehouse(csvImportService.getBoxes(), warehouse);

        csvImportService.importOrders(orderIs);
        csvImportService.importOrderLines(orderLinesIs);

        return warehouse;
    }

    /**
     * Initializes the Belgian railway network from CSV files as a directed graph
     *
     * @return Graph representing the Belgian railway network
     * @throws Exception if initialization fails
     */
    public Graph<Station, TrackConnection> initializeBelgianNetwork() throws Exception {
        return initializeBelgianNetwork(true);
    }

    /**
     * Initializes the Belgian railway network from CSV files
     *
     * @param directed whether to create a directed or undirected graph
     * @return Graph representing the Belgian railway network
     * @throws Exception if initialization fails
     */
    public Graph<Station, TrackConnection> initializeBelgianNetwork(boolean directed) throws Exception {
        try {
            InputStream stationsIS = getClass().getResourceAsStream("/data/stations.csv");
            InputStream linesIS = getClass().getResourceAsStream("/data/lines.csv");

            if (stationsIS == null) {
                throw new IOException("stations.csv not found in resources");
            }
            if (linesIS == null) {
                throw new IOException("lines.csv not found in resources");
            }

            return loadBelgianNetworkFromStreams(stationsIS, linesIS, directed);
        } catch (Exception e) {
            System.err.println("Error loading Belgian network: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Load Belgian network from InputStreams
     */
    private Graph<Station, TrackConnection> loadBelgianNetworkFromStreams(
            InputStream stationsInput,
            InputStream linesInput,
            boolean directed) throws IOException {

        Map<Integer, Station> stations = loadStations(stationsInput);
        Graph<Station, TrackConnection> graph = loadNetwork(linesInput, stations, directed);


        return graph;
    }

    /**
     * Load stations from CSV
     */
    private Map<Integer, Station> loadStations(InputStream stationsInput) throws IOException {
        Map<Integer, Station> stations = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stationsInput))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    try {
                        int stationId = Integer.parseInt(parts[0].trim());
                        String stationName = parts[1].trim();
                        double lat = Double.parseDouble(parts[2].trim());
                        double lon = Double.parseDouble(parts[3].trim());
                        double coordX = Double.parseDouble(parts[4].trim());
                        double coordY = Double.parseDouble(parts[5].trim());

                        Station station = new Station(
                                String.valueOf(stationId),
                                stationName,
                                coordX,
                                coordY,
                                lat,
                                lon
                        );

                        stations.put(stationId, station);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing station: " + line + " - " + e.getMessage());
                    }
                }
            }
        }

        return stations;
    }

    /**
     * Load network connections from CSV
     */
    private Graph<Station, TrackConnection> loadNetwork(
            InputStream linesInput,
            Map<Integer, Station> stations, boolean directed) throws IOException {

        Graph<Station, TrackConnection> graph = new MapGraph<>(directed);

        for (Station station : stations.values()) {
            graph.addVertex(station);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(linesInput))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        int fromId = Integer.parseInt(parts[0].trim());
                        int toId = Integer.parseInt(parts[1].trim());
                        double distance = Double.parseDouble(parts[2].trim());
                        int capacity = Integer.parseInt(parts[3].trim());
                        double cost = Double.parseDouble(parts[4].trim());

                        Station fromStation = stations.get(fromId);
                        Station toStation = stations.get(toId);

                        if (fromStation != null && toStation != null) {
                            // For undirected graphs, skip reverse edges to avoid duplicates
                            if (!directed && fromId > toId) {
                                continue;
                            }
                            TrackConnection connection = new TrackConnection(distance, capacity, cost);
                            graph.addEdge(fromStation, toStation, connection);
                        } else {
                            System.err.println("Station not found for connection: " + fromId + " -> " + toId);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }

        return graph;
    }

    public void initializeStationsData() throws Exception {
        InputStream stationsIs = getClass().getResourceAsStream("/data/train_stations_europe.csv");
        csvImportService.importStations(stationsIs);
    }

    public CsvImportService getCsvImportService() {
        return csvImportService;
    }
}