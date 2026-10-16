package pt.isep.dei.esinf.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.railway.Facility;
import pt.isep.dei.esinf.domain.railway.LineSegment;
import pt.isep.dei.esinf.domain.railway.SegmentPassage;
import pt.isep.dei.esinf.domain.railway.TrainPath;
import pt.isep.dei.esinf.domain.train.Locomotive;
import pt.isep.dei.esinf.domain.train.Train;
import pt.isep.dei.esinf.domain.train.TrainSchedule;
import pt.isep.dei.esinf.domain.train.Wagon;
import pt.isep.dei.esinf.repository.TrainRepository;
import pt.isep.dei.esinf.service.TrainSchedulerService;

public class TrainSchedulerMenuController {
    private Scanner scanner;
    private TrainRepository repository;
    private TrainSchedulerService scheduler;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final String DB_URL = "jdbc:oracle:thin:@Pinto:1521:orcl";
    private static final String DB_USER = "sys as sysdba";
    private static final String DB_PASSWORD = "database";

    public TrainSchedulerMenuController(Scanner scanner) throws SQLException{
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            this.scanner = scanner;
            this.repository = new TrainRepository(connection);
            this.scheduler = new TrainSchedulerService();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void show() {
        System.out.println("\n===============================================");
        System.out.println("   RAILWAY TRAIN SCHEDULER - USLP07            ");
        System.out.println("   Freight Manager Dispatch System             ");
        System.out.println("===============================================");

        boolean running = true;
        while (running) {
            System.out.println("1. View All Scheduled Trains                ");
            System.out.println("2. Calculate Schedule for Single Train      ");
            System.out.println("3. Calculate Schedule for All Trains        ");
            System.out.println("4. View Train Details                       ");
            System.out.println("0. Exit                                     ");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllScheduledTrains();
                    break;
                case "2":
                    calculateSingleTrainSchedule();
                    break;
                case "3":
                    calculateAllTrainSchedules();
                    break;
                case "4":
                    viewTrainDetails();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nExiting Train Scheduler. Goodbye!");
                    break;
                default:
                    System.out.println("✗ Invalid option. Please try again.");
            }
        }
    }

    private void viewAllScheduledTrains() {
        try {
            List<Train> trains = repository.getAllTrains();

            if (trains.isEmpty()) {
                System.out.println("\n⚠ No scheduled trains found.");
                return;
            }

            System.out.println("\n===================================================================================");
            System.out.println("                         SCHEDULED TRAINS                                       ");
            System.out.println("===================================================================================");
            System.out.printf(" %-8s   %-16s   %-25s   %-20s%n",
                    "Train ID", "Departure", "Origin", "Destination");
            System.out.println("===================================================================================");

            for (Train train : trains) {
                System.out.printf(" %-8d   %-16s   %-25s   %-20s%n",
                        train.getId(),
                        train.getDepartureDateTime().format(dateFormatter),
                        truncate(train.getPath().getOrigin().getName(), 25),
                        truncate(train.getPath().getDestination().getName(), 20));
            }
            System.out.println("===================================================================================");

        } catch (SQLException e) {
            System.out.println("✗ Error loading trains: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateSingleTrainSchedule() {
        try {
            List<Train> trains = repository.getAllTrains();

            if (trains.isEmpty()) {
                System.out.println("\n⚠ No scheduled trains found.");
                return;
            }

            System.out.println("\n===============================================");
            System.out.println("   CALCULATE SINGLE TRAIN SCHEDULE            ");
            System.out.println("===============================================");

            // Show available trains
            System.out.println("\nAvailable Trains:");
            for (int i = 0; i < trains.size(); i++) {
                Train t = trains.get(i);
                System.out.printf("%d. Train %d - %s → %s (Departs: %s)%n",
                        i + 1,
                        t.getId(),
                        t.getPath().getOrigin().getName(),
                        t.getPath().getDestination().getName(),
                        t.getDepartureDateTime().format(dateFormatter));
            }

            System.out.print("\nSelect train number: ");
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection < 1 || selection > trains.size()) {
                System.out.println("✗ Invalid selection.");
                return;
            }

            Train selectedTrain = trains.get(selection - 1);
            TrainSchedule schedule = scheduler.calculateSchedule(selectedTrain);
            displaySchedule(schedule);

        } catch (SQLException e) {
            System.out.println("✗ Error loading trains: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid input. Please enter a number.");
        }
    }

    private void calculateAllTrainSchedules() {
        try {
            List<Train> trains = repository.getAllTrains();

            if (trains.isEmpty()) {
                System.out.println("\n⚠ No scheduled trains found.");
                return;
            }

            System.out.println("\n===============================================");
            System.out.println("   CALCULATE ALL TRAIN SCHEDULES               ");
            System.out.println("===============================================");

            for (Train train : trains) {
                TrainSchedule schedule = scheduler.calculateSchedule(train);
                displaySchedule(schedule);
                System.out.println("\n" + "=".repeat(90) + "\n");
            }

        } catch (SQLException e) {
            System.out.println("✗ Error loading trains: " + e.getMessage());
        }
    }

    private void displaySchedule(TrainSchedule schedule) {
        Train train = schedule.getTrain();
        TrainPath path = train.getPath();

        System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                            TRAIN SCHEDULE REPORT                                ║");
        System.out.println("╠═════════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Train ID: %-8d                                                                 ║%n",
                train.getId());
        System.out.printf("║ Departure: %-20s   Estimated Arrival: %-20s           ║%n",
                train.getDepartureDateTime().format(dateFormatter),
                schedule.getEstimatedArrival().format(dateFormatter));
        System.out.printf("║ Origin: %-30s   Destination: %-25s  ║%n",
                truncate(path.getOrigin().getName(), 30),
                truncate(path.getDestination().getName(), 25));
        System.out.println("╠═════════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Distance: %6.1f km   Total Power: %,7d kW   Total Weight: %,6d tons   ║%n",
                path.getTotalDistanceKm(),
                train.getTotalPowerKw(),
                train.getTotalWeightTons());
        System.out.printf("║ Locomotives: %-2d   Wagons: %-2d (Loaded: %-2d, Empty: %-2d)   Freights: %-2d            ║%n",
                train.getLocomotives().size(),
                train.getWagons().size(),
                train.getLoadedWagonCount(),
                train.getEmptyWagonCount(),
                train.getFreightIds().size());
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════════╝");

        // Locomotives
        if (!train.getLocomotives().isEmpty()) {
            System.out.println("\n=== LOCOMOTIVES ============================================================================");
            for (Locomotive loco : train.getLocomotives()) {
                System.out.printf(" #%-5d %-18s - %,6d kW, %3d tons, Max: %3d km/h, Type: %-8s %n",
                        loco.getNumber(),
                        truncate(loco.getName(), 18),
                        loco.getModel().getPower(),
                        loco.getModel().getWeight(),
                        loco.getModel().getMaximumSpeed(),
                        loco.getModel().isElectric() ? "Electric" : "Diesel");
            }
            System.out.println("=======================================================================================");
        }

        // Wagons summary
        if (!train.getWagons().isEmpty()) {
            System.out.println("\n=== WAGONS =================================================================================");
            System.out.printf(" Total: %d wagons   Loaded: %d   Empty: %d                                   %n",
                    train.getWagons().size(),
                    train.getLoadedWagonCount(),
                    train.getEmptyWagonCount());
            System.out.println("=======================================================================================");
        }

        // Freights
        if (!train.getFreightIds().isEmpty()) {
            try {
                Map<Integer, String> freightDetails = repository.getFreightDetails(train);
                System.out.println("\n=== FREIGHTS ===============================================================================");
                for (Integer freightId : train.getFreightIds()) {
                    String detail = freightDetails.getOrDefault(freightId, "Unknown");
                    System.out.printf(" Freight #%-5d: %-60s%n", freightId, detail);
                }
                System.out.println("=======================================================================================");
            } catch (SQLException e) {
                System.out.println("⚠ Could not load freight details");
            }
        }

        // Detailed schedule
        System.out.println("\n=== DETAILED SCHEDULE ======================================================================");
        System.out.printf(" %-4s   %-35s   %-8s   %-6s   %-10s%n",
                "#", "Segment", "Arrival", "Speed", "Track");
        System.out.println("=======================================================================================");

        int segmentNum = 1;
        for (SegmentPassage passage : schedule.getPassages()) {
            LineSegment seg = passage.getSegment();
            String segmentDesc = seg.getStartFacility().getName() + " → " + seg.getEndFacility().getName();

            System.out.printf(" %-4d   %-35s   %-8s   %3d %s   %-10s%n",
                    segmentNum++,
                    truncate(segmentDesc, 35),
                    passage.getArrivalTime().format(timeFormatter),
                    passage.getTravelSpeedKmh(),
                    "km/h",
                    seg.isSingleTrack() ? "⚠ Single" : "✓ Double");

            // Show stop time if any
            long stopMinutes = Duration.between(passage.getArrivalTime(), passage.getDepartureTime()).toMinutes();
            if (stopMinutes > 0) {
                System.out.printf("        └─ Stop: %2d min                                                          %n",
                        stopMinutes);
            }
        }
        System.out.println("=======================================================================================");

        // Summary
        TrainSchedulerService.TrainPerformanceAnalysis analysis =
                scheduler.analyzePerformance(train, schedule);

        System.out.println("\n=== PERFORMANCE SUMMARY ====================================================================");
        System.out.printf(" Total travel time: %d minutes (%.1f hours)                                   %n",
                schedule.getTotalTravelTimeMinutes(),
                schedule.getTotalTravelTimeMinutes() / 60.0);
        System.out.printf(" Average speed: %.1f km/h                                                     %n",
                analysis.getAverageSpeed());
        System.out.printf(" Power/Weight ratio: %.2f kW/ton                                              %n",
                analysis.getPowerToWeightRatio());

        if (analysis.getSingleTrackSegmentCount() > 0) {
            System.out.printf(" ⚠ Warning: %d single-track segment(s) - crossing coordination required      %n",
                    analysis.getSingleTrackSegmentCount());
        }
        System.out.println("=======================================================================================");
    }

    private void viewTrainDetails() {
        try {
            List<Train> trains = repository.getAllTrains();

            if (trains.isEmpty()) {
                System.out.println("\n⚠ No scheduled trains found.");
                return;
            }

            System.out.println("\n===============================================");
            System.out.println("   VIEW TRAIN DETAILS                          ");
            System.out.println("===============================================");

            // Show available trains
            System.out.println("\nAvailable Trains:");
            for (int i = 0; i < trains.size(); i++) {
                Train t = trains.get(i);
                System.out.printf("%d. Train %d - %s → %s%n",
                        i + 1,
                        t.getId(),
                        t.getPath().getOrigin().getName(),
                        t.getPath().getDestination().getName());
            }

            System.out.print("\nSelect train number: ");
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection < 1 || selection > trains.size()) {
                System.out.println("✗ Invalid selection.");
                return;
            }

            Train train = trains.get(selection - 1);
            displayTrainDetails(train);

        } catch (SQLException e) {
            System.out.println("✗ Error loading trains: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid input. Please enter a number.");
        }
    }

    private void displayTrainDetails(Train train) {
        TrainPath path = train.getPath();

        System.out.println("\n===================================================================================");
        System.out.println("                            TRAIN DETAILS                                        ");
        System.out.println("===================================================================================");
        System.out.printf(" Train ID: %-8d   Departure: %-20s                             %n",
                train.getId(),
                train.getDepartureDateTime().format(dateFormatter));
        System.out.println("===================================================================================");

        // Path information
        System.out.println("\n=== PATH INFORMATION =======================================================================");
        System.out.printf(" Origin: %-30s Destination: %-25s %n",
                truncate(path.getOrigin().getName(), 30),
                truncate(path.getDestination().getName(), 25));
        System.out.printf(" Total Distance: %.1f km   Segments: %d   Facilities: %d                      %n",
                path.getTotalDistanceKm(),
                path.getSegments().size(),
                path.getFacilities().size());
        System.out.println("=======================================================================================");

        // Complete path
        System.out.println("\n=== COMPLETE PATH ==========================================================================");
        for (int i = 0; i < path.getFacilities().size(); i++) {
            Facility f = path.getFacilities().get(i);
            System.out.printf(" %2d. %-50s %s              %n",
                    i + 1,
                    f.getName(),
                    f.isIntermodal() ? "[Intermodal]" : "");
        }
        System.out.println("=======================================================================================");

        // Locomotives
        System.out.println("\n┌─── LOCOMOTIVES ──────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Total: %d locomotive(s)                                                        │%n",
                train.getLocomotives().size());
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        for (Locomotive loco : train.getLocomotives()) {
            System.out.printf("│ #%-6d %-20s %-30s                     │%n",
                    loco.getNumber(),
                    truncate(loco.getName(), 20),
                    truncate(loco.getModel().getName(), 30));
            System.out.printf("│         Power: %,d kW   Weight: %d tons   Max Speed: %d km/h   Type: %-8s│%n",
                    loco.getModel().getPower(),
                    loco.getModel().getWeight(),
                    loco.getModel().getMaximumSpeed(),
                    loco.getModel().isElectric() ? "Electric" : "Diesel");
        }
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");

        // Wagons
        System.out.println("\n┌─── WAGONS ───────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Total: %d wagon(s)   Loaded: %d   Empty: %d                                  │%n",
                train.getWagons().size(),
                train.getLoadedWagonCount(),
                train.getEmptyWagonCount());
        System.out.println("├──────────────────────────────────────────────────────────────────────────────┤");
        for (Wagon wagon : train.getWagons()) {
            System.out.printf("│ %-12s %-40s %-8s            │%n",
                    wagon.getNumber(),
                    truncate(wagon.getModel().getName(), 40),
                    wagon.isLoaded() ? "LOADED" : "EMPTY");
            System.out.printf("│              Tare: %d tons   Cargo: %d tons   Total: %d tons              │%n",
                    wagon.getModel().getTare(),
                    wagon.getCargoWeight(),
                    wagon.getTotalWeight());
        }
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");

        // Weight summary
        System.out.println("\n┌─── WEIGHT & POWER SUMMARY ───────────────────────────────────────────────────┐");
        System.out.printf("│ Locomotives: %,d tons                                                          │%n",
                train.getLocomotives().stream().mapToInt(l -> l.getModel().getWeight()).sum());
        System.out.printf("│ Wagons:      %,d tons                                                          │%n",
                train.getWagons().stream().mapToInt(Wagon::getTotalWeight).sum());
        System.out.printf("│ TOTAL:       %,d tons                                                          │%n",
                train.getTotalWeightTons());
        System.out.printf("│ Total Power: %,d kW                                                            │%n",
                train.getTotalPowerKw());
        System.out.printf("│ Power/Weight ratio: %.2f kW/ton                                                │%n",
                (double) train.getTotalPowerKw() / train.getTotalWeightTons());
        System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");
    }

    /**
     * Truncates a string to a maximum length
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Cleanup method to close resources
     */
    public void cleanup() {
        repository.disconnect();
        scanner.close();
    }
}