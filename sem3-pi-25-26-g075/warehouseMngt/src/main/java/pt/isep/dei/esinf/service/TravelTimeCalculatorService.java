package pt.isep.dei.esinf.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.SegmentInfo;
import pt.isep.dei.esinf.domain.results.TravelTimeResult;

public class TravelTimeCalculatorService {


    private static final String DB_URL = "jdbc:oracle:thin:@Pinto:1521:orcl";
    private static final String DB_USER = "sys as sysdba";
    private static final String DB_PASSWORD = "database";

    private Connection connection;


    /**
     * connection to the database
     */
    public void connect() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("\n");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }
    }


    /**
     * closes the database connection
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * lists available stations from the database
     */
    public void listStations() throws SQLException {
        String sql = "SELECT station_id, \"name\" FROM Station ORDER BY \"name\"";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Available Stations ---");
            while (rs.next()) {
                System.out.printf("  %d - %s\n",
                        rs.getInt("station_id"),
                        rs.getString("name"));
            }
            System.out.println();
        }
    }

    /**
     * lists available locomotives from the database
     */
    public void listLocomotives() throws SQLException {
        String sql = "SELECT l.\"number\", l.\"name\", et.\"name\" AS engine_type, lm.maximum_speed " +
                "FROM Locomotive l " +
                "JOIN LocomotiveModel lm ON l.model_id = lm.locomotive_model_id " +
                "JOIN Engine e ON lm.engine_id = e.engine_id " +
                "JOIN EngineType et ON e.engine_type_id = et.engine_type_id " +
                "ORDER BY l.\"number\"";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Available Locomotives ---");
            while (rs.next()) {
                System.out.printf("  %d - %s (%s, max: %d km/h)\n",
                        rs.getInt("number"),
                        rs.getString("name"),
                        rs.getString("engine_type"),
                        rs.getInt("maximum_speed"));
            }
            System.out.println();
        }
    }

    /**
     * Calculates travel time between two stations using SQL query
     */
    public TravelTimeResult calculateTravelTime(String stationA, String stationB, String locomotiveId)
            throws SQLException {

        // Updated SQL query for your database schema
        String sql =
                "SELECT " +
                        "    ls.line_segment_id, " +
                        "    ls.\"order\" AS segment_order, " +
                        "    s1.\"name\" AS start_station, " +
                        "    s2.\"name\" AS end_station, " +
                        "    ls.\"length\" AS length_km, " +
                        "    ls.speed_limit_kmh, " +
                        "    lm.maximum_speed AS locomotive_max_speed, " +
                        "    et.\"name\" AS locomotive_type, " +
                        "    ls.is_electrified, " +
                        "    LEAST(ls.speed_limit_kmh, lm.maximum_speed) AS effective_speed_kmh, " +
                        "    (ls.\"length\" / 1000.0 / LEAST(ls.speed_limit_kmh, lm.maximum_speed)) * 60 AS time_minutes " +
                        "FROM LineSegment ls " +
                        "JOIN Station s1 ON ls.start_station_id = s1.station_id " +
                        "JOIN Station s2 ON ls.end_station_id = s2.station_id " +
                        "JOIN Locomotive l ON l.\"number\" = ? " +
                        "JOIN LocomotiveModel lm ON l.model_id = lm.locomotive_model_id " +
                        "JOIN Engine e ON lm.engine_id = e.engine_id " +
                        "JOIN EngineType et ON e.engine_type_id = et.engine_type_id " +
                        "WHERE ls.line_id IN ( " +
                        "    SELECT DISTINCT ls2.line_id " +
                        "    FROM LineSegment ls2 " +
                        "    WHERE EXISTS ( " +
                        "        SELECT 1 FROM LineSegment ls3 " +
                        "        WHERE ls3.line_id = ls2.line_id " +
                        "        AND (ls3.start_station_id = ? OR ls3.end_station_id = ?) " +
                        "    ) " +
                        "    AND EXISTS ( " +
                        "        SELECT 1 FROM LineSegment ls4 " +
                        "        WHERE ls4.line_id = ls2.line_id " +
                        "        AND (ls4.start_station_id = ? OR ls4.end_station_id = ?) " +
                        "    ) " +
                        ") " +
                        "AND ls.\"order\" BETWEEN " +
                        "    (SELECT MIN(\"order\") FROM LineSegment " +
                        "     WHERE line_id = ls.line_id " +
                        "     AND (start_station_id = ? OR end_station_id = ?)) " +
                        "    AND " +
                        "    (SELECT MAX(\"order\") FROM LineSegment " +
                        "     WHERE line_id = ls.line_id " +
                        "     AND (start_station_id = ? OR end_station_id = ?)) " +
                        "ORDER BY ls.\"order\"";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set parameters (locomotive number is integer, stations are integers)
            stmt.setInt(1, Integer.parseInt(locomotiveId));
            stmt.setInt(2, Integer.parseInt(stationA));
            stmt.setInt(3, Integer.parseInt(stationA));
            stmt.setInt(4, Integer.parseInt(stationB));
            stmt.setInt(5, Integer.parseInt(stationB));
            stmt.setInt(6, Integer.parseInt(stationA));
            stmt.setInt(7, Integer.parseInt(stationA));
            stmt.setInt(8, Integer.parseInt(stationB));
            stmt.setInt(9, Integer.parseInt(stationB));

            ResultSet rs = stmt.executeQuery();

            // Get station names for result
            String originName = getStationName(stationA);
            String destName = getStationName(stationB);

            TravelTimeResult result = new TravelTimeResult(originName, destName, locomotiveId);

            while (rs.next()) {
                SegmentInfo segment = new SegmentInfo(
                        String.valueOf(rs.getInt("line_segment_id")),
                        rs.getString("start_station"),
                        rs.getString("end_station"),
                        rs.getDouble("length_km"),
                        rs.getDouble("speed_limit_kmh"),
                        rs.getDouble("locomotive_max_speed"),
                        rs.getDouble("effective_speed_kmh"),
                        rs.getDouble("time_minutes"),
                        rs.getString("is_electrified").equalsIgnoreCase("YES") ||
                                rs.getString("is_electrified").equalsIgnoreCase("Y") ||
                                rs.getString("is_electrified").equals("1"),
                        rs.getString("locomotive_type")
                );
                result.addSegment(segment);
            }

            return result;
        }
    }
    /**
     * helper method to get station name from station ID
     */
    private String getStationName(String stationId) throws SQLException {
        String sql = "SELECT \"name\" FROM Station WHERE station_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(stationId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            return stationId; // Return ID if name not found
        }
    }

    /**
     * terminal interface
     */
    public void showMenu(Scanner scanner) {
        try {
            connect();

            while (true) {
                System.out.println("\n================================================");
                System.out.println("   TRAVEL TIME CALCULATOR                       ");
                System.out.println("================================================");

                System.out.println("\nOptions:");
                System.out.println("  1. Calculate travel time");
                System.out.println("  2. List stations");
                System.out.println("  3. List locomotives");
                System.out.println("  4. Exit");
                System.out.print("\nChoose option: ");

                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1":
                        calculateTrip(scanner);
                        break;
                    case "2":
                        listStations();
                        break;
                    case "3":
                        listLocomotives();
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnect();
            scanner.close();
        }
    }

    private void calculateTrip(Scanner scanner) throws SQLException {
        System.out.print("\nEnter origin station ID: ");
        String stationA = scanner.nextLine().trim();

        System.out.print("Enter destination station ID: ");
        String stationB = scanner.nextLine().trim();

        System.out.print("Enter locomotive ID: ");
        String locomotiveId = scanner.nextLine().trim();

        try {
            TravelTimeResult result = calculateTravelTime(stationA, stationB, locomotiveId);
            result.display();
        } catch (SQLException e) {
            System.err.println("\nX Error calculating travel time: " + e.getMessage());
        }

        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
