package pt.isep.dei.esinf.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.isep.dei.esinf.domain.freight.Freight;
import pt.isep.dei.esinf.domain.freight.FreightRoute;
import pt.isep.dei.esinf.domain.freight.RouteStop;
import pt.isep.dei.esinf.repository.FreightRepository;

/**
 * Service for planning freight routes
 */
public class FreightRoutePlannerService {
    private final FreightRepository freightRepository;

    public FreightRoutePlannerService(FreightRepository freightRepository) {
        this.freightRepository = freightRepository;
    }

    /**
     * Get all pending freights
     */
    public List<Freight> getPendingFreights() throws SQLException {
        return freightRepository.getPendingFreights();
    }

    /**
     * Get freight by ID
     */
    public Freight getFreightById(int freightId) throws SQLException {
        return freightRepository.getFreightById(freightId);
    }

    /**
     * Plan a simple route for a single freight
     */
    public FreightRoute planSimpleRoute(Freight freight) {
        FreightRoute route = new FreightRoute("Route for Freight #" + freight.getFreightId(), "SIMPLE");

        // Create origin stop (load)
        RouteStop originStop = new RouteStop(1, freight.getOrigin());
        originStop.addFreightToLoad(freight);
        route.addStop(originStop);

        // Create destination stop (unload)
        RouteStop destStop = new RouteStop(2, freight.getDestination());
        destStop.addFreightToUnload(freight);
        route.addStop(destStop);

        return route;
    }

    /**
     * Plan a complex route for multiple freights
     * Groups freights by common facilities to optimize stops
     */
    public FreightRoute planComplexRoute(List<Freight> freights) {
        if (freights.isEmpty()) {
            throw new IllegalArgumentException("Cannot plan route with no freights");
        }

        FreightRoute route = new FreightRoute("Complex Route for " + freights.size() + " Freights", "COMPLEX");

        // Map to track facilities and their operations
        Map<Integer, RouteStop> facilityStops = new HashMap<>();
        int stopOrder = 1;

        // First, collect all unique facilities and their operations
        for (Freight freight : freights) {
            // Handle origin
            int originId = freight.getOrigin().getId();
            RouteStop originStop = facilityStops.computeIfAbsent(originId, id -> new RouteStop(stopOrder, freight.getOrigin()));
            originStop.addFreightToLoad(freight);

            // Handle destination
            int destId = freight.getDestination().getId();
            RouteStop destStop = facilityStops.computeIfAbsent(destId, id -> new RouteStop(stopOrder, freight.getDestination()));
            destStop.addFreightToUnload(freight);
        }

        // Order stops: prioritize loading stations first, then unloading
        List<RouteStop> orderedStops = new ArrayList<>(facilityStops.values());
        orderedStops.sort((s1, s2) -> {
            // Stops with loads come before stops with only unloads
            boolean s1HasLoad = s1.hasLoadOperations();
            boolean s2HasLoad = s2.hasLoadOperations();

            if (s1HasLoad && !s2HasLoad) return -1;
            if (!s1HasLoad && s2HasLoad) return 1;

            // If both have loads or both don't, sort by facility ID for consistency
            return Integer.compare(s1.getFacility().getId(), s2.getFacility().getId());
        });

        // Assign proper stop order
        for (int i = 0; i < orderedStops.size(); i++) {
            RouteStop stop = orderedStops.get(i);
            route.addStop(new RouteStop(i + 1, stop.getFacility()));
            RouteStop routeStop = route.getStops().get(i);

            // Copy operations
            for (Freight f : stop.getFreightsToLoad()) {
                routeStop.addFreightToLoad(f);
            }
            for (Freight f : stop.getFreightsToUnload()) {
                routeStop.addFreightToUnload(f);
            }
        }

        return route;
    }


    /**
     * Display route details in TUI format
     */
    public void displayRoute(FreightRoute route) {
        System.out.println("\n==========================================================================");
        System.out.println("                       ROUTE PLAN DETAILS                                 ");
        System.out.println("==========================================================================");
        System.out.printf(" Route: %-40s Type: %-15s%n", truncate(route.getRouteName(), 40), route.getRouteType());
        System.out.printf(" Total Freights: %-4d                                                    %n", route.getFreightCount());
        System.out.println("==========================================================================");

        for (RouteStop stop : route.getStops()) {
            System.out.printf("  STOP #%d: %-62s%n", stop.getStopOrder(), truncate(stop.getFacility().getName(), 62));

            if (stop.hasLoadOperations()) {
                System.out.println("  ├─ LOAD:");
                for (Freight f : stop.getFreightsToLoad()) {
                    String destName = truncate(f.getDestination().getName(), 47);
                    System.out.printf("  │  └─ Freight #%-4d → %-47s%n", f.getFreightId(), destName);
                }
            } else {
                System.out.println("  ├─ LOAD: None");
            }

            if (stop.hasUnloadOperations()) {
                System.out.println("  ├─ UNLOAD:");
                for (Freight f : stop.getFreightsToUnload()) {
                    System.out.printf("  │  └─ Freight #%-4d%n", f.getFreightId());
                }
            } else {
                System.out.println("  ├─ UNLOAD: None");
            }

            if (stop != route.getStops().get(route.getStops().size() - 1)) {
                System.out.println("==========================================================================");
            }
        }

        System.out.println("==========================================================================");
    }

    /**
     * Display freights in a table format
     */
    public void displayFreights(List<Freight> freights) {
        if (freights.isEmpty()) {
            System.out.println("\n=== No freights found ===");
            return;
        }

        System.out.println("\n===================================================================================");
        System.out.println("                            FREIGHT LIST                                          ");
        System.out.println("===================================================================================");
        System.out.println("   ID       Date            Origin                  Destination                   ");
        System.out.println("===================================================================================");

        for (Freight freight : freights) {
            System.out.printf(" %-6d   %-10s   %-23s   %-33s%n", freight.getFreightId(), freight.getFreightDate(), truncate(freight.getOrigin().getName(), 23), truncate(freight.getDestination().getName(), 33));
        }

        System.out.println("===================================================================================");
        System.out.printf("\nTotal freights: %d%n", freights.size());
    }

    /**
     * Truncate string to max length with ellipsis
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Assign a route to a train, marking freights as no longer pending
     *
     * @param route   The planned route
     * @param trainId The train ID to assign freights to
     * @return Number of freights successfully assigned
     */
    public int assignRouteToTrain(FreightRoute route, int trainId) throws SQLException {
        List<Integer> freightIds = new ArrayList<>();

        // Collect all freight IDs from the route
        for (RouteStop stop : route.getStops()) {
            for (Freight freight : stop.getFreightsToLoad()) {
                if (!freightIds.contains(freight.getFreightId())) {
                    freightIds.add(freight.getFreightId());
                }
            }
        }

        if (freightIds.isEmpty()) {
            return 0;
        }

        // Assign all freights to the train
        freightRepository.assignMultipleFreightsToTrain(trainId, freightIds);

        return freightIds.size();
    }

    /**
     * Check if a freight is still pending
     */
    public boolean isFreightPending(int freightId) throws SQLException {
        return freightRepository.isFreightPending(freightId);
    }


    /**
     * Validate that all freights in a route are pending before assignment
     *
     * @param route The route to validate
     * @return List of freight IDs that are already assigned (empty if all are pending)
     */
    public List<Integer> validateRoutePendingStatus(FreightRoute route) throws SQLException {
        List<Integer> alreadyAssigned = new ArrayList<>();

        for (RouteStop stop : route.getStops()) {
            for (Freight freight : stop.getFreightsToLoad()) {
                if (!isFreightPending(freight.getFreightId())) {
                    alreadyAssigned.add(freight.getFreightId());
                }
            }
        }

        return alreadyAssigned;
    }
}

