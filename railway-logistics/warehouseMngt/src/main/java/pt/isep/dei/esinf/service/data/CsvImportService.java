package pt.isep.dei.esinf.service.data;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.stations.Coordinates;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.TimeZone;
import pt.isep.dei.esinf.domain.warehouse.*;
import pt.isep.dei.esinf.repository.StationsRepository;
import pt.isep.dei.esinf.utils.interfaces.Graph;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvImportService {

    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, Bay> bays = new LinkedHashMap<>();
    private final List<Box> boxes = new ArrayList<>();
    private final Set<String> boxIds = new HashSet<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

    public void importItems(InputStream input) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length != 6) continue;

                String sku = parts[0].trim();
                Item item = new Item(
                        sku,
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Double.parseDouble(parts[4].trim()),
                        Double.parseDouble(parts[5].trim())
                );
                items.put(sku, item);
            }
        }
    }

    public void importBays(InputStream input) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(";");
                if (parts.length != 4) continue;

                String warehouseId = parts[0].trim();
                int aisle = Integer.parseInt(parts[1].trim());
                int bay = Integer.parseInt(parts[2].trim());
                int capacity = Integer.parseInt(parts[3].trim());

                Bay bayObj = new Bay(warehouseId, aisle, bay, capacity);
                bays.put(bayObj.getLocationCode(), bayObj);
            }
        }
    }

    /**
     * CRITICAL FIX: Parse expiry date correctly (date only, not datetime)
     */
    public void importWagons(InputStream input) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 6) continue;

                String boxId = parts[1].trim();
                String sku = parts[2].trim();
                int qty = Integer.parseInt(parts[3].trim());
                String expiry = parts[4].trim();
                String received = parts[5].trim();

                if (boxIds.contains(boxId)) {
                    System.err.println("Duplicate boxId: " + boxId);
                    continue;
                }
                if (!items.containsKey(sku)) {
                    System.err.println("Unknown SKU: " + sku);
                    continue;
                }
                if (qty < 0) {
                    System.err.println("Invalid quantity: " + qty);
                    continue;
                }
                if (received.isEmpty()) {
                    System.err.println("Missing receivedAt for boxId: " + boxId);
                    continue;
                }

                LocalDateTime receivedAt = LocalDateTime.parse(received, dateTimeFormatter);

                LocalDateTime expiryDate = null;
                if (!expiry.isEmpty()) {
                    LocalDate expiryLocalDate = LocalDate.parse(expiry, dateFormatter);
                    expiryDate = expiryLocalDate.atStartOfDay();
                }

                Box box = new Box(boxId, sku, qty, expiryDate, receivedAt);
                boxes.add(box);
                boxIds.add(boxId);
            }
        }
    }

    /**
     * Imports orders from CSV file
     * Format: orderId, dueDate, priority
     * dueDate can be either ISO date (2025-10-15) or ISO datetime (2025-10-15T09:00:00)
     */
    public void importOrders(InputStream input) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 3) continue;

                String orderId = parts[0].trim();
                String dueDateStr = parts[1].trim();
                int priority = Integer.parseInt(parts[2].trim());

                LocalDate dueDate;
                if (dueDateStr.contains("T")) {
                    LocalDateTime dateTime = LocalDateTime.parse(dueDateStr, dateTimeFormatter);
                    dueDate = dateTime.toLocalDate();
                } else {
                    dueDate = LocalDate.parse(dueDateStr, dateFormatter);
                }

                if (priority < 0) {
                    System.err.println("Invalid priority for order " + orderId + ": " + priority);
                    continue;
                }

                Order order = new Order(orderId, dueDate, priority);
                orders.put(orderId, order);
            }
        }
    }


    public void importStations(InputStream input) throws Exception {
        StationsRepository stationsRepository = StationsRepository.getInstance();

        stationsRepository.clear();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");

                String country = parts[0].trim();

                String zone = parts[1].trim().split("'")[1];
                String group = parts[3].trim();


                Double latitude = null, longitude = null;
                if (!parts[5].isEmpty() && !parts[6].isEmpty()) {
                    latitude = Double.parseDouble(parts[5].trim());
                    longitude = Double.parseDouble(parts[6].trim());
                }

                String stationName = parts[4].trim();

                boolean isCity = Boolean.parseBoolean(parts[7].trim());
                boolean isMainStation = Boolean.parseBoolean(parts[8].trim());
                boolean isAirport = Boolean.parseBoolean(parts[9].trim());

                TimeZone timeZone = new TimeZone(country, zone, group);
                Coordinates coordinates = new Coordinates(latitude, longitude);
                Station station = new Station(stationName, coordinates, isCity, isMainStation, isAirport);
                station.setTimeZone(timeZone);

                if (station.isValid()) {
                    stationsRepository.addStation(timeZone, station);
                }
            }
        }
    }


    /**
     * Imports order lines from CSV file
     * Format: orderId, lineNo, sku, qty
     */
    public void importOrderLines(InputStream input) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 4) continue;

                String orderId = parts[0].trim();
                int lineNo = Integer.parseInt(parts[1].trim());
                String sku = parts[2].trim();
                int qty = Integer.parseInt(parts[3].trim());

                Order order = orders.get(orderId);
                if (order == null) {
                    System.err.println("Order not found: " + orderId);
                    continue;
                }

                if (!items.containsKey(sku)) {
                    System.err.println("Unknown SKU in order line: " + sku);
                    continue;
                }

                if (qty <= 0) {
                    System.err.println("Invalid quantity in order line: " + qty);
                    continue;
                }

                OrderLine orderLine = new OrderLine(lineNo, sku, qty);
                order.addLine(orderLine);
            }
        }
    }

    /**
     * Load stations from CSV (your format)
     */
    public static Map<Integer, Station> loadStations(InputStream stationsInput) throws IOException {
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
                if (parts.length >= 7) {
                    try {
                        int stationId = Integer.parseInt(parts[0].trim());
                        String stationName = parts[1].trim();
                        double lat = Double.parseDouble(parts[2].trim());
                        double lon = Double.parseDouble(parts[3].trim());
                        double coordX = Double.parseDouble(parts[4].trim());
                        double coordY = Double.parseDouble(parts[5].trim());

                        Station station = new Station(
                                String.valueOf(stationId), stationName, coordX, coordY
                        );

                        stations.put(stationId, station);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing station: " + line);
                    }
                }
            }
        }

        return stations;
    }

    /**
     * Load lines/connections from CSV (your format)
     */
    public static Graph<Station, TrackConnection> loadNetwork(
            InputStream linesInput,
            Map<Integer, Station> stations) throws IOException {

        Graph<Station, TrackConnection> graph = new MapGraph<>(true);

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
                            TrackConnection connection = new TrackConnection(distance, capacity, cost);
                            graph.addEdge(fromStation, toStation, connection);
                        } else {
                            System.err.println("Station not found for connection: " + fromId + " -> " + toId);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Load both stations and lines from resources
     */
    public static Graph<Station, TrackConnection> loadFromResources(
            String stationsResource,
            String linesResource) throws IOException {

        InputStream stationsStream = CsvImportService.class.getResourceAsStream(stationsResource);
        InputStream linesStream = CsvImportService.class.getResourceAsStream(linesResource);

        if (stationsStream == null || linesStream == null) {
            throw new IOException("Resources not found: " + stationsResource + " or " + linesResource);
        }

        Map<Integer, Station> stations = loadStations(stationsStream);
        return loadNetwork(linesStream, stations);
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public Map<String, Bay> getBays() {
        return bays;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public Map<String, Order> getOrders() {
        return orders;
    }

    public List<Order> getOrdersList() {
        return new ArrayList<>(orders.values());
    }
}