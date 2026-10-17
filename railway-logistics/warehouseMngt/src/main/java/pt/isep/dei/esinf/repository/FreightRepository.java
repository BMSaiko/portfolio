package pt.isep.dei.esinf.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleTypes;
import pt.isep.dei.esinf.domain.freight.Freight;
import pt.isep.dei.esinf.domain.railway.Facility;

/**
 * Repository for managing freight data in the database
 * Updated to use PL/SQL functions for USLP08 acceptance criteria
 */
public class FreightRepository {
    private final Connection connection;

    public FreightRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get all pending freights (not yet assigned to a train)
     * Uses PL/SQL function: get_pending_freights()
     */
    public List<Freight> getPendingFreights() throws SQLException {
        List<Freight> freights = new ArrayList<>();

        String sql = "{ ? = call get_pending_freights() }";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    freights.add(extractFreightFromResultSet(rs));
                }
            }
        }

        return freights;
    }


    /**
     * Get all freights (including assigned ones)
     * Uses PL/SQL function: get_all_freights()
     */
    public List<Freight> getAllFreights() throws SQLException {
        List<Freight> freights = new ArrayList<>();

        String sql = "{ ? = call get_all_freights() }";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    freights.add(extractFreightFromResultSet(rs));
                }
            }
        }

        return freights;
    }

    /**
     * Get freight by ID
     * Uses PL/SQL function: get_freight_by_id(freight_id)
     */
    public Freight getFreightById(int freightId) throws SQLException {
        String sql = "{ ? = call get_freight_by_id(?) }";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.setInt(2, freightId);
            stmt.execute();

            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                if (rs.next()) {
                    return extractFreightFromResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Helper method to extract Freight object from ResultSet
     * Reduces code duplication across methods
     */
    private Freight extractFreightFromResultSet(ResultSet rs) throws SQLException {
        Facility origin = new Facility(
            rs.getInt("origin_id"),
            rs.getString("origin_name"),
            "Sim".equalsIgnoreCase(rs.getString("origin_intermodal"))
        );

        Facility destination = new Facility(
            rs.getInt("dest_id"),
            rs.getString("dest_name"),
            "Sim".equalsIgnoreCase(rs.getString("dest_intermodal"))
        );

        return new Freight(
            rs.getInt("freight_id"),
            rs.getDate("freight_date").toLocalDate(),
            origin,
            destination
        );
    }

    /**
     * Check if a freight is pending (not assigned to any train)
     * Uses PL/SQL function: is_freight_pending(freight_id)
     */
    public boolean isFreightPending(int freightId) throws SQLException {
        String sql = "{ ? = call is_freight_pending(?) }";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.INTEGER);
            stmt.setInt(2, freightId);
            stmt.execute();

            int result = stmt.getInt(1);
            return result == 1;
        }
    }

    /**
     * Assign multiple freights to a train in a single transaction
     * Uses PL/SQL procedure: assign_multiple_freights_to_train(train_id, freight_ids)
     */
    public void assignMultipleFreightsToTrain(int trainId, List<Integer> freightIds) throws SQLException {
        // Convert list to comma-separated string
        String freightIdsStr = freightIds.stream()
            .map(String::valueOf)
            .reduce((a, b) -> a + "," + b)
            .orElse("");

        String sql = "{ call assign_multiple_freights_to_train(?, ?) }";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, trainId);
            stmt.setString(2, freightIdsStr);
            stmt.execute();
        }
    }

}

