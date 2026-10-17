package pt.isep.dei.esinf.repository;

import oracle.jdbc.internal.OracleTypes;
import pt.isep.dei.esinf.domain.train.SelectableVehicle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TrafficManagerRepository {

    // Simulação da conexão à BD
    private final Connection connection;

    public TrafficManagerRepository(Connection connection) {
        this.connection = connection;
    }

    private static final String GET_VEHICLES_SQL = "{ ? = call GET_SELECTABLE_VEHICLES(?) }";

    public List<SelectableVehicle> getSelectableVehicles(int startingFacilityId) {
        List<SelectableVehicle> vehicles = new ArrayList<>();

        try (CallableStatement stmt = connection.prepareCall(GET_VEHICLES_SQL)) {
            // 1. Configurar parâmetros
            stmt.registerOutParameter(1, OracleTypes.CURSOR); // Retorno: SYS_REFCURSOR
            stmt.setInt(2, startingFacilityId);                // Input: starting_facility_id

            // 2. Executar
            stmt.execute();

            // 3. Processar o Cursor de Retorno
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                while (rs.next()) {
                    String id = rs.getString("vehicle_id");
                    String name = rs.getString("vehicle_name");
                    String inTransit = rs.getString("is_in_transit");
                    String location = rs.getString("location_name");
                    double distance = rs.getDouble("distance_to_start");

                    vehicles.add(new SelectableVehicle(id, name, inTransit, location, distance));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções
        }

        // 4. Implementar a ordenação conforme o Critério de Aceitação da USLP09:
        //    - Em trânsito (prioridade máxima/não ordenado por distância)
        //    - Estacionados, ordenados em ordem DESCENDENTE de distância
        vehicles.sort((v1, v2) -> {
            // Se o status for diferente, priorizar "Em Trânsito" (In Transit = true)
            if (v1.isInTransit() != v2.isInTransit()) {
                return v1.isInTransit() ? -1 : 1; // Coloca "Em Trânsito" no topo
            }
            // Se ambos estiverem Estacionados (Parked), ordenar por distância DESCENDENTE
            if (!v1.isInTransit()) {
                // (v2.distance - v1.distance) para ordem descendente (maior distância primeiro)
                return Double.compare(v2.getDistance(), v1.getDistance());
            }
            // Se ambos estiverem em trânsito, a ordem não é crítica (retorna 0)
            return 0;
        });

        return vehicles;
    }
}
