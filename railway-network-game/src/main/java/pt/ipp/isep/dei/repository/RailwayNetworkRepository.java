package pt.ipp.isep.dei.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import pt.ipp.isep.dei.domain.locomotive.Train;
import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;

/**
 * Repository for loading railway network data from CSV files.
 */
public class RailwayNetworkRepository implements IRailwayNetworkRepository {
    
    /**
     * Loads a railway network from CSV files.
     * 
     * @param stationsFile the name of the stations CSV file
     * @param linesFile the name of the lines CSV file
     * @return the loaded railway network
     * @throws IOException if an I/O error occurs
     */
    public RailwayNetwork loadFromCsv(String stationsFile, String linesFile) throws IOException {
        RailwayNetwork network = new RailwayNetwork();
        
        System.out.println("Loading stations and lines...");
        
        // First try to load stations from stations file
        boolean stationsLoaded = loadStationsFromFile(network, stationsFile);
        
        if (!stationsLoaded) {
            System.out.println("No valid stations found in stations file or file not found. Extracting from lines file...");
            // Extract station names from lines file
            extractStationsFromLinesFile(network, linesFile);
        }
        
        System.out.println("Total stations loaded: " + network.getAllStations().size());
        
        // Print all stations for debugging
/*         System.out.println("Stations in network:");
        for (Station station : network.getAllStations()) {
            System.out.println("  - " + station.getName() + " (" + station.getType() + ")");
        } */
        
        // Now load the lines
        loadLinesFromFile(network, linesFile);
        System.out.println("Total lines loaded: " + network.getAllLines().size());
        
        return network;
    }
    
    /**
     * Loads stations from a file.
     * 
     * @param network the railway network
     * @param stationsFile the name of the stations file
     * @return true if at least one station was loaded, false otherwise
     */
    private boolean loadStationsFromFile(RailwayNetwork network, String stationsFile) {
        boolean anyStationLoaded = false;
        
        try (BufferedReader reader = getReaderForResource(stationsFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains semicolons (multiple stations on one line)
                if (line.contains(";")) {
                    int id = 0; // Default ID for stations without a specific ID

                    String[] stationNames = line.split(";");
                    for (String name : stationNames) {
                        String trimmedName = name.trim();
                        if (!trimmedName.isEmpty()) {
                            Station station = new Station(trimmedName, id++);
                            network.addStation(station);
/*                             System.out.println("Added station from stations file: " + trimmedName);
 */                            anyStationLoaded = true;
                        }
                    }
                } else {
                    int id = 0; // Default ID for stations without a specific ID
                    // Single station per line
                    String trimmedName = line.trim();
                    if (!trimmedName.isEmpty()) {
                        Station station = new Station(trimmedName, id++);
                        network.addStation(station);
/*                         System.out.println("Added station from stations file: " + trimmedName);
 */                        anyStationLoaded = true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading stations file: " + e.getMessage());
            return false;
        }
        
        return anyStationLoaded;
    }
    
    /**
     * Extracts stations from the lines file.
     * 
     * @param network the railway network
     * @param linesFile the name of the lines file
     */
    private void extractStationsFromLinesFile(RailwayNetwork network, String linesFile) {
        Set<String> stationNames = new HashSet<>();
        
        try (BufferedReader reader = getReaderForResource(linesFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String sourceName = parts[0].trim();
                    String destName = parts[1].trim();
                    
                    if (!sourceName.isEmpty()) {
                        stationNames.add(sourceName);
                    }
                    
                    if (!destName.isEmpty()) {
                        stationNames.add(destName);
                    }
                }
            }
            
            // Add all extracted stations to the network
            for (String name : stationNames) {
                int id = 0; // Default ID for stations without a specific ID
                Station station = new Station(name, id++);
                network.addStation(station);
/*                 System.out.println("Added station from lines file: " + name);
 */            }
            
        } catch (IOException e) {
            System.err.println("Error extracting stations from lines file: " + e.getMessage());
        }
    }
    
    /**
     * Loads lines from a file.
     * 
     * @param network the railway network
     * @param linesFile the name of the lines file
     */
    private void loadLinesFromFile(RailwayNetwork network, String linesFile) {
        try (BufferedReader reader = getReaderForResource(linesFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(network, line);
            }
        } catch (IOException e) {
            System.err.println("Error loading lines file: " + e.getMessage());
        }
    }
    
    /**
     * Gets a reader for a resource.
     * 
     * @param resourceName the name of the resource
     * @return a reader for the resource
     * @throws IOException if an I/O error occurs
     */
    private BufferedReader getReaderForResource(String resourceName) throws IOException {
        // Try as a resource from the classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        
        // If not found, try as a system resource
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        }
        
        // If still not found, try as a direct file path
        if (inputStream == null) {
            try {
                inputStream = Files.newInputStream(Paths.get(resourceName));
            } catch (IOException e) {
                // Try one more location
                try {
                    inputStream = Files.newInputStream(Paths.get("src/main/resources", resourceName));
                } catch (IOException e2) {
                    throw new IOException("Resource not found: " + resourceName);
                }
            }
        }
        
        return new BufferedReader(new InputStreamReader(inputStream));
    }
    
    /**
     * Parses a line definition and adds it to the network.
     * 
     * @param network the railway network
     * @param line the line definition
     */
    private void parseLine(RailwayNetwork network, String line) {
        String[] parts = line.split(";");
        if (parts.length == 4) {
            try {
                String sourceName = parts[0].trim();
                String destName = parts[1].trim();
                boolean isElectrified = "1".equals(parts[2].trim());
                int distance = Integer.parseInt(parts[3].trim());
                
                try {
                    Station source = network.getStation(sourceName);
                    Station destination = network.getStation(destName);
                    
                    RailwayLine railwayLine = new RailwayLine(source, destination, isElectrified, distance);
                    network.addLine(railwayLine);
                    /* System.out.println("Added line: " + source.getName() + " -> " + 
                                      destination.getName() + " (electrified: " + 
                                      isElectrified + ", distance: " + distance + ")"); */
                } catch (NoSuchElementException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing distance in line: " + line + " - " + e.getMessage());
            }
        } else {
            System.err.println("Invalid line format: " + line + " - expected 4 parts separated by semicolon");
        }
    }

    @Override
    public boolean addTrainToNetwork(Train train) {
        // TODO: Implement train addition to network
        return false;
    }

    @Override
    public boolean removeTrainFromNetwork(String trainId) {
        // TODO: Implement train removal from network
        return false;
    }

    @Override
    public List<Train> getNetworkTrains() {
        // TODO: Implement network trains retrieval
        return null;
    }

    @Override
    public boolean isTrainInNetwork(String trainId) {
        // TODO: Implement train in network check
        return false;
    }
}