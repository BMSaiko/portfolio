package pt.isep.dei.esinf.repository;

import pt.isep.dei.esinf.domain.railway.*;
import pt.isep.dei.esinf.domain.train.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrainRepository {

    private Connection connection;

    public TrainRepository(Connection connection) throws SQLException {
        this.connection = connection;
    }




    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ TrainRepository connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }


    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();

        String sql = "SELECT train_id, train_departure_date_time, path_id, operator_id " +
                "FROM Train " +
                "ORDER BY train_departure_date_time";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int trainId = rs.getInt("train_id");
                LocalDateTime departureDateTime = rs.getTimestamp("train_departure_date_time").toLocalDateTime();
                int pathId = rs.getInt("path_id");

                // Load the train path
                TrainPath path = loadTrainPath(pathId);

                // Create train
                Train train = new Train(trainId, departureDateTime, path);

                // Load locomotives
                loadTrainLocomotives(train);

                // Load wagons with cargo status
                loadTrainWagons(train);

                // Load freight IDs
                loadTrainFreights(train);

                trains.add(train);
            }
        }

        return trains;
    }

    /**
     * Loads a complete train path with facilities and segments
     */
    private TrainPath loadTrainPath(int pathId) throws SQLException {
        TrainPath path = new TrainPath(pathId);

        // Load ordered facilities for this path
        String facilitySql = "SELECT pf.path_facility_order, pf.facility_id, " +
                "f.facility_name, f.is_intermodal " +
                "FROM PathFacilities pf " +
                "JOIN Facility f ON pf.facility_id = f.facility_id " +
                "WHERE pf.path_id = ? " +
                "ORDER BY pf.path_facility_order";

        List<Facility> facilities = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(facilitySql)) {
            stmt.setInt(1, pathId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Facility facility = new Facility(
                            rs.getInt("facility_id"),
                            rs.getString("facility_name"),
                            "Sim".equalsIgnoreCase(rs.getString("is_intermodal"))
                    );
                    path.addFacility(facility);
                    facilities.add(facility);
                }
            }
        }

        // Load segments connecting consecutive facilities
        if (facilities.size() >= 2) {
            loadPathSegments(path, facilities);
        }

        return path;
    }

    /**
     * Saves the calculated schedule to the TrainSchedule table.
     */
    public void saveTrainSchedule(TrainSchedule schedule) throws SQLException {
        String deleteSql = "DELETE FROM TrainSchedule WHERE train_id = ?";
        String insertSql = "INSERT INTO TrainSchedule (train_id, facility_id, arrival_time, departure_time, path_facility_order) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Get the ordered list of facilities from the domain object
        List<pt.isep.dei.esinf.domain.railway.Facility> pathFacilities = schedule.getPathFacilities();

        // Use transaction for atomic operation (optional but highly recommended)
        connection.setAutoCommit(false);

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            // 1. Delete any old schedule for this train
            deleteStmt.setInt(1, schedule.getTrain().getId());
            deleteStmt.executeUpdate();
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            // 2. Insert the new passages
            for (SegmentPassage passage : schedule.getPassages()) {

                // The segment passage records the arrival/departure at the END facility.
                pt.isep.dei.esinf.domain.railway.Facility endFacility = passage.getSegment().getEndFacility();

                // Find the order number of this END facility in the path
                int pathFacilityOrder = -1;
                for (int i = 0; i < pathFacilities.size(); i++) {
                    if (pathFacilities.get(i).getId() == endFacility.getId()) {
                        // path_facility_order is 1-based, list index is 0-based.
                        pathFacilityOrder = i + 1;
                        break;
                    }
                }

                if (pathFacilityOrder == -1) {
                    // This should not happen if the data is consistent
                    throw new SQLException("Facility " + endFacility.getId() + " not found in path facilities list.");
                }

                insertStmt.setInt(1, schedule.getTrain().getId());
                insertStmt.setInt(2, endFacility.getId());

                // Convert LocalDateTime to Timestamp for JDBC
                insertStmt.setTimestamp(3, Timestamp.valueOf(passage.getArrivalTime()));
                insertStmt.setTimestamp(4, Timestamp.valueOf(passage.getDepartureTime()));

                // Store the correctly fetched order number
                insertStmt.setInt(5, pathFacilityOrder);

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("✓ Schedule for Train " + schedule.getTrain().getId() + " saved successfully.");

        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
    }

    /**
     * Loads line segments connecting consecutive facilities in the path
     */
    private void loadPathSegments(TrainPath path, List<Facility> facilities) throws SQLException {
        // For each consecutive pair of facilities, find the connecting line segment
        for (int i = 0; i < facilities.size() - 1; i++) {
            Facility start = facilities.get(i);
            Facility end = facilities.get(i + 1);

            // Find line segment(s) connecting these facilities
            String segmentSql = "SELECT ls.line_segment_id, ls.line_id, ls.is_electrified, " +
                    "ls.line_segment_length, ls.number_of_tracks, ls.speed_limit_kmh, " +
                    "ls.siding_id, " +
                    "tl.start_station_id, tl.end_station_id " +
                    "FROM LineSegment ls " +
                    "JOIN TrainLine tl ON ls.line_id = tl.line_id " +
                    "WHERE tl.start_station_id = ? AND tl.end_station_id = ? " +
                    "ORDER BY ls.line_segment_order";

            try (PreparedStatement stmt = connection.prepareStatement(segmentSql)) {
                stmt.setInt(1, start.getId());
                stmt.setInt(2, end.getId());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        LineSegment segment = new LineSegment(
                                rs.getInt("line_segment_id"),
                                rs.getInt("line_id"),
                                start,
                                end,
                                "Sim".equalsIgnoreCase(rs.getString("is_electrified")),
                                rs.getInt("line_segment_length"),
                                rs.getInt("number_of_tracks"),
                                rs.getInt("speed_limit_kmh"),
                                rs.getObject("siding_id") != null ? rs.getInt("siding_id") : null
                        );
                        path.addSegment(segment);
                    }
                }
            }
        }
    }

    /**
     * Loads all locomotives for a train
     */
    private void loadTrainLocomotives(Train train) throws SQLException {
        String sql = "SELECT l.locomotive_number, l.locomotive_name, " +
                "lm.locomotive_model_id, lm.locomotive_model_name, " +
                "lm.locomotive_model_power, lm.locomotive_model_weight, " +
                "lm.maximum_speed, et.engine_type_name " +
                "FROM TrainLocomotive tl " +
                "JOIN Locomotive l ON tl.locomotive_id = l.locomotive_number " +
                "JOIN LocomotiveModel lm ON l.model_id = lm.locomotive_model_id " +
                "JOIN Engine e ON lm.engine_id = e.engine_id " +
                "JOIN EngineType et ON e.engine_type_id = et.engine_type_id " +
                "WHERE tl.train_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, train.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocomotiveModel model = new LocomotiveModel(
                            rs.getInt("locomotive_model_id"),
                            rs.getString("locomotive_model_name"),
                            rs.getInt("locomotive_model_power"),
                            rs.getInt("locomotive_model_weight"),
                            rs.getInt("maximum_speed"),
                            "Electric".equalsIgnoreCase(rs.getString("engine_type_name"))
                    );

                    Locomotive locomotive = new Locomotive(
                            rs.getInt("locomotive_number"),
                            rs.getString("locomotive_name"),
                            model
                    );

                    train.addLocomotive(locomotive);
                }
            }
        }
    }

    /**
     * Loads all wagons for a train with their cargo status
     */
    private void loadTrainWagons(Train train) throws SQLException {
        String sql = "SELECT w.wagon_number, " +
                "wm.wagon_model_id, wm.wagon_model_name, " +
                "wm.wagon_model_weight, wm.wagon_model_payload, wm.max_speed, " +
                "twc.is_loaded, twc.cargo_weight " +
                "FROM FreightWagon fw " +
                "JOIN TrainFreight tf ON fw.freight_id = tf.freight_id " +
                "JOIN Wagon w ON fw.wagon_id = w.wagon_number " +
                "JOIN WagonModel wm ON w.wagon_model_id = wm.wagon_model_id " +
                "LEFT JOIN TrainWagonCargo twc ON tf.train_id = twc.train_id " +
                "                              AND w.wagon_number = twc.wagon_number " +
                "WHERE tf.train_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, train.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WagonModel model = new WagonModel(
                            rs.getInt("wagon_model_id"),
                            rs.getString("wagon_model_name"),
                            rs.getInt("wagon_model_weight"),
                            rs.getInt("wagon_model_payload"),
                            rs.getInt("max_speed")
                    );

                    // Default to not loaded if no cargo data exists
                    boolean isLoaded = false;
                    int cargoWeight = 0;

                    String loadedStr = rs.getString("is_loaded");
                    if (loadedStr != null) {
                        isLoaded = "Sim".equalsIgnoreCase(loadedStr);
                        cargoWeight = rs.getInt("cargo_weight");
                    }

                    Wagon wagon = new Wagon(
                            rs.getString("wagon_number"),
                            model,
                            isLoaded,
                            cargoWeight
                    );

                    train.addWagon(wagon);
                }
            }
        }
    }

    /**
     * Loads freight IDs associated with a train
     */
    private void loadTrainFreights(Train train) throws SQLException {
        String sql = "SELECT freight_id FROM TrainFreight WHERE train_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, train.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    train.addFreightId(rs.getInt("freight_id"));
                }
            }
        }
    }

    /**
     * Gets freight details (for display purposes)
     */
    public Map<Integer, String> getFreightDetails(Train train) throws SQLException {
        Map<Integer, String> freightDetails = new HashMap<>();

        if (train.getFreightIds().isEmpty()) {
            return freightDetails;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT f.freight_id, " +
                        "fo.facility_name as origin_name, " +
                        "fd.facility_name as destination_name " +
                        "FROM Freight f " +
                        "JOIN Facility fo ON f.origin_facility_id = fo.facility_id " +
                        "JOIN Facility fd ON f.destination_facility_id = fd.facility_id " +
                        "WHERE f.freight_id IN ("
        );

        for (int i = 0; i < train.getFreightIds().size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
        }
        sql.append(")");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < train.getFreightIds().size(); i++) {
                stmt.setInt(i + 1, train.getFreightIds().get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int freightId = rs.getInt("freight_id");
                    String detail = rs.getString("origin_name") + " → " +
                            rs.getString("destination_name");
                    freightDetails.put(freightId, detail);
                }
            }
        }

        return freightDetails;
    }
}
