package pt.isep.dei.esinf.service.warehouse;

import java.sql.*;
import java.util.Scanner;

public class TrainMovementService {

    private static final String URL = "jdbc:oracle:thin:@Pinto:1521:orcl";
    private static final String USER = "sys as sysdba";
    private static final String PASS = "database";

    // Constante para CURSOR Oracle (Equivalente a OracleTypes.CURSOR)
    private static final int ORACLE_CURSOR = -10;

    public void showMenu(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            runScheduler(conn, scanner);
        } catch (SQLException e) {
            System.out.println(" Database Connection Error: " + e.getMessage());
        }
    }

    private void runScheduler(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Train ID: ");

        int trainId;
        try {
            trainId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println(" Invalid ID.");
            return;
        }

        System.out.println("\nRoute Definition:");
        System.out.println("1 - Automatic (Database)");
        System.out.println("2 - Manual (Freight Manager Guide)");
        System.out.print("Option: ");
        int mode;
        try {
            mode = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            mode = 1;
        }

        // ALTERAÇÃO: Chama PL/SQL para dados técnicos
        TechnicalData tech = getTechnicalData(conn, trainId);

        if (tech == null) {
            System.out.println(" Train not found or no technical data available.");
            return;
        }

        // ALTERAÇÃO: Chama PL/SQL para caminho e cálculo
        calculateAndPrintPath(conn, trainId, tech, mode, scanner);
    }

    private void calculateAndPrintPath(Connection conn, int trainId, TechnicalData tech, int mode, Scanner scanner) throws SQLException {
        // SQL Call para a Function PL/SQL
        String callSql = "{ ? = call func_get_train_path(?) }";

        try (CallableStatement cstmt = conn.prepareCall(callSql)) {
            // 1. Registar o retorno (CURSOR)
            cstmt.registerOutParameter(1, ORACLE_CURSOR);
            // 2. Definir o input (Train ID)
            cstmt.setInt(2, trainId);

            // 3. Executar
            cstmt.execute();

            // 4. Obter o ResultSet do Cursor
            ResultSet rs = (ResultSet) cstmt.getObject(1);

            System.out.println("\n=======================================================");
            System.out.printf("TRAIN SCHEDULING #%d | Mass: %.1ft | Power: %.0fkW\n", trainId, tech.mass, tech.power);
            System.out.println("=======================================================");

            Timestamp currentTime = tech.departureTime;

            while (rs.next()) {
                String station = rs.getString("facility_name");
                double distMetros = rs.getDouble("line_segment_length");
                double lineLimit = rs.getDouble("speed_limit_kmh");
                int tracks = rs.getInt("number_of_tracks");
                int sidingId = rs.getInt("siding_id");

                System.out.printf("\n %-25s | Arrival: %s\n", station, currentTime);

                if (distMetros > 0) {
                    if (mode == 2) { // MANUAL MODE
                        if (tracks == 1) {
                            System.out.println("   [ALERT] Single line ahead! Crossing operation required.");
                            System.out.println("   What would you like to do?");
                            System.out.println("   1 - Wait for crossing (Add 10 min)");
                            System.out.println("   2 - Authorize immediate departure (Line is clear)");
                            System.out.print("   Choice (1/2): ");

                            String op = scanner.nextLine();
                            if ("1".equals(op)) {
                                currentTime = new Timestamp(currentTime.getTime() + 600000);
                                System.out.println("   [OK] Train is waiting. New departure time: " + currentTime);
                            } else {
                                System.out.println("   [OK] Immediate departure authorized.");
                            }
                        } else {
                            System.out.println("    Double/Complex Line. Press [ENTER] to validate departure...");
                            scanner.nextLine();
                        }
                    }

                    double distKm = distMetros / 1000.0;
                    double actualSpeed = Math.min(lineLimit, tech.maxVehicleSpeed);
                    long travelMillis = (long) ((distKm / actualSpeed) * 3600000);
                    long dwellMillis = 120000;

                    currentTime = new Timestamp(currentTime.getTime() + travelMillis + dwellMillis);
                }
            }
        }
        System.out.println("=======================================================\n");
    }

    private TechnicalData getTechnicalData(Connection conn, int trainId) throws SQLException {
        // SQL Call para a Function PL/SQL
        String callSql = "{ ? = call func_get_train_tech_data(?) }";

        try (CallableStatement cstmt = conn.prepareCall(callSql)) {
            cstmt.registerOutParameter(1, ORACLE_CURSOR);
            cstmt.setInt(2, trainId);
            cstmt.execute();

            ResultSet rs = (ResultSet) cstmt.getObject(1);

            if (rs.next()) {
                return new TechnicalData(
                        rs.getTimestamp("train_departure_date_time"),
                        rs.getDouble("total_mass"),
                        rs.getDouble("total_power"),
                        rs.getDouble("max_speed")
                );
            }
        }
        return null;
    }

    private static class TechnicalData {
        Timestamp departureTime;
        double mass, power, maxVehicleSpeed;
        TechnicalData(Timestamp dt, double m, double p, double ms) {
            this.departureTime = dt; this.mass = m; this.power = p; this.maxVehicleSpeed = ms;
        }
    }
}