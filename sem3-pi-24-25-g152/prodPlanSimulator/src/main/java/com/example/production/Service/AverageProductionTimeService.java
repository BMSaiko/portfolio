package com.example.production.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

public class AverageProductionTimeService {
    private static final Logger logger = LogManager.getLogger(AverageProductionTimeService.class);
    private static final String CONFIG_FILE = "config.properties";

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbInternalLogon;

    public AverageProductionTimeService() {
        loadDBConfig();
    }

    /**
     * Carrega a configuração do banco de dados a partir do arquivo config.properties.
     */
    private void loadDBConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.error("Não foi possível encontrar {}", CONFIG_FILE);
                throw new FileNotFoundException("Arquivo de configuração não encontrado.");
            }
            properties.load(input);
            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            dbInternalLogon = properties.getProperty("db.internal_logon");
            logger.info("Configuração do banco de dados carregada com sucesso.");
        } catch (IOException e) {
            logger.error("Erro ao carregar a configuração do banco de dados: ", e);
            throw new RuntimeException("Falha ao carregar a configuração do banco de dados.", e);
        }
    }

    /**
     * Estabelece uma conexão com o banco de dados Oracle com o papel SYSDBA.
     *
     * @return Objeto Connection
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", dbUsername);
        props.setProperty("password", dbPassword);
        if (dbInternalLogon != null && !dbInternalLogon.isEmpty()) {
            props.setProperty("internal_logon", dbInternalLogon);
        }
        return DriverManager.getConnection(dbUrl, props);
    }

    /**
     * Calcula e atualiza os tempos médios de produção no banco de dados.
     *
     * @param operationTimeTracker Mapa contendo o tempo total por operação.
     */
    public void updateAverageProductionTimes(Map<String, Integer> operationTimeTracker) {
        String insertSQL = "INSERT INTO AverageProductionTime (OperationID, AverageTime, CalculationDate) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            conn.setAutoCommit(false); // Controle de transações

            for (Map.Entry<String, Integer> entry : operationTimeTracker.entrySet()) {
                String operationId = entry.getKey(); // Agora é String, como "POLISH"
                int totalTime = entry.getValue();

                double averageTime = (double) totalTime / getNumberOfArticles(conn);

                pstmt.setString(1, operationId); // Alterado para setString
                pstmt.setDouble(2, averageTime);
                pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            logger.info("Tempos médios de produção atualizados com sucesso no banco de dados.");

        } catch (SQLException e) {
            logger.error("Erro ao atualizar os tempos médios de produção: ", e);
            // A transação será revertida automaticamente pelo try-with-resources
        }
    }

    /**
     * Obtém o número total de artigos processados.
     *
     * @param conn Objeto Connection
     * @return Número total de artigos
     * @throws SQLException
     */
    private int getNumberOfArticles(Connection conn) throws SQLException {
        String countArticlesSQL = "SELECT COUNT(DISTINCT PrOrID) AS ArticleCount FROM ProductionOrder";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countArticlesSQL)) {
            if (rs.next()) {
                return rs.getInt("ArticleCount");
            }
        }
        return 1; // Evitar divisão por zero
    }
}
