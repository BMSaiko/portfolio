package com.example.production.Service;

import com.example.production.Domain.BooEntry;
import com.example.production.Domain.BooSubItem;
import com.example.production.Domain.BooSubOperation;
import com.example.production.Utils.RepPathUtils;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

/**
 * Service class to import data from Oracle DB and generate CSV files.
 */
public class DataImportService {
    private static final Logger logger = LogManager.getLogger(DataImportService.class);
    private static final String CONFIG_FILE = "config.properties";

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbInternalLogon;

    // Define the relative output directory path
    private static final String OUTPUT_RELATIVE_PATH = "documentation/ESINF/Sprint03/BDcsv";

    public DataImportService() {
        loadDBConfig();
    }

    /**
     * Loads database configuration from config.properties.
     */
    private void loadDBConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.error("Could not find {}", CONFIG_FILE);
                throw new FileNotFoundException("Configuration file not found.");
            }
            properties.load(input);
            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            dbInternalLogon = properties.getProperty("db.internal_logon");
            logger.info("Database configuration loaded successfully.");
        } catch (IOException e) {
            logger.error("Error loading database configuration: ", e);
            throw new RuntimeException("Failed to load database configuration.", e);
        }
    }

    /**
     * Establishes a connection to the Oracle database with SYSDBA role.
     *
     * @return Connection object
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
     * Imports data from Oracle DB and generates CSV files.
          * @throws SQLException 
               * @throws IOException 
                    */
                   public void importFromOracleAndGenerateCSV() throws SQLException, IOException {
        List<BooEntry> booEntries = new ArrayList<>();
        Map<String, String> operationsMap = new HashMap<>();
        Map<String, String> itemsMap = new HashMap<>();

        try (Connection conn = getConnection()) {
            logger.info("Connected to the database.");

            // Carregar Operações
            String operationsQuery = "SELECT OPID, Name FROM Operation";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(operationsQuery)) {
                while (rs.next()) {
                    String opId = String.valueOf(rs.getInt("OPID"));
                    String opName = rs.getString("Name");
                    operationsMap.put(opId, opName);
                }
                logger.info("Loaded {} operations.", operationsMap.size());
            }

            // Carregar Itens (Partes)
            String itemsQuery = "SELECT PartID, Description FROM Part";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(itemsQuery)) {
                while (rs.next()) {
                    String itemId = rs.getString("PartID");
                    String itemName = rs.getString("Description");
                    itemsMap.put(itemId, itemName);
                }
                logger.info("Loaded {} items.", itemsMap.size());
            }

            // Carregar BOO e BOO_Operation
            String booQuery = "SELECT BOO.BOOID, BOO.PartID FROM BOO";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(booQuery)) {
                while (rs.next()) {
                    String booId = rs.getString("BOOID");
                    String partId = rs.getString("PartID");
                    List<BooSubOperation> subOperations = new ArrayList<>();
                    List<BooSubItem> subItems = new ArrayList<>();
                    int itemQuantity = 0; // Será definido a partir de PartOut

                    // Buscar operações associadas
                    String booOpQuery = "SELECT OPID, NextOPID, NextBOOID FROM BOO_Operation WHERE BOOID = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(booOpQuery)) {
                        pstmt.setString(1, booId);
                        try (ResultSet opRs = pstmt.executeQuery()) {
                            while (opRs.next()) {
                                String opId = String.valueOf(opRs.getInt("OPID"));
                                int nextOpIdInt = opRs.getInt("NextOPID");
                                String nextOpId = opRs.wasNull() ? null : String.valueOf(nextOpIdInt);
                                String nextBooId = opRs.getString("NextBOOID");

                                if (nextOpId != null && nextBooId != null) {
                                    // Assumindo que opQuantity é 1 por padrão; ajuste conforme necessário
                                    subOperations.add(new BooSubOperation(nextOpId, 1));
                                }
                            }
                        }
                    }

                    // Buscar sub-itens (entradas)
                    String partInQuery = "SELECT PartID, QuantityIn FROM PartIN WHERE BOOID = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(partInQuery)) {
                        pstmt.setString(1, booId);
                        try (ResultSet partInRs = pstmt.executeQuery()) {
                            while (partInRs.next()) {
                                String subItemId = partInRs.getString("PartID");
                                int subItemQty = partInRs.getInt("QuantityIn");
                                subItems.add(new BooSubItem(subItemId, subItemQty));
                            }
                        }
                    }

                    // Buscar item_quantity a partir de PartOut (assumindo uma operação principal por BOO)
                    String partOutQuery = "SELECT Quantity FROM PartOut WHERE BOOID = ? AND OPID = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(partOutQuery)) {
                        pstmt.setString(1, booId);
                        // Extrair parte numérica de BOOID para mapeamento de OPID (ajuste conforme necessário)
                        String numericBooId = booId.replaceAll("[^0-9]", "");
                        if (numericBooId.isEmpty()) {
                            numericBooId = "0"; // Padrão se não houver parte numérica
                        }
                        pstmt.setInt(2, Integer.parseInt(numericBooId));
                        try (ResultSet partOutRs = pstmt.executeQuery()) {
                            if (partOutRs.next()) {
                                itemQuantity = partOutRs.getInt("Quantity");
                            }
                        }
                    }

                    // Criar instância de BooEntry
                    BooEntry entry = new BooEntry(booId, partId, itemQuantity, subOperations, subItems);
                    booEntries.add(entry);
                }
                logger.info("Loaded {} BOO entries.", booEntries.size());
            }

            // Determinar o diretório de saída usando RepPathUtils
            Path outputDir = RepPathUtils.getPathRelativeToRep(OUTPUT_RELATIVE_PATH);
            File outputDirectory = outputDir.toFile();

            // Criar o diretório se não existir
            if (!outputDirectory.exists()) {
                boolean dirsCreated = outputDirectory.mkdirs();
                if (dirsCreated) {
                    logger.info("Created directory: {}", outputDir.toString());
                } else {
                    logger.error("Failed to create directory: {}", outputDir.toString());
                    throw new IOException("Could not create output directory.");
                }
            }

            // Gerar arquivos CSV no diretório especificado
            generateOperationsCSV(operationsMap, outputDir);
            generateItemsCSV(itemsMap, outputDir);
            generateBooCSV(booEntries, outputDir);
        }
        }

    /**
     * Generates operations.csv in the specified directory.
     *
     * @param operationsMap Map of operation ID to operation name
     * @param outputDir     Directory where the CSV will be saved
     * @throws IOException
     */
    private void generateOperationsCSV(Map<String, String> operationsMap, Path outputDir) throws IOException {
        String fileName = "operations.csv";
        Path filePath = outputDir.resolve(fileName);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()), ';',
                CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            // Escrever cabeçalho
            String[] header = {"op_id", "op_name"};
            writer.writeNext(header);

            // Escrever dados
            for (Map.Entry<String, String> entry : operationsMap.entrySet()) {
                String[] line = {entry.getKey(), entry.getValue()};
                writer.writeNext(line);
            }

            logger.info("Generated {}", filePath.toString());
        }
    }

    /**
     * Generates items.csv in the specified directory.
     *
     * @param itemsMap  Map of item ID to item name
     * @param outputDir Directory where the CSV will be saved
     * @throws IOException
     */
    private void generateItemsCSV(Map<String, String> itemsMap, Path outputDir) throws IOException {
        String fileName = "items.csv";
        Path filePath = outputDir.resolve(fileName);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()), ';',
                CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            // Escrever cabeçalho
            String[] header = {"id_item", "item_name"};
            writer.writeNext(header);

            // Escrever dados
            for (Map.Entry<String, String> entry : itemsMap.entrySet()) {
                String[] line = {entry.getKey(), entry.getValue()};
                writer.writeNext(line);
            }

            logger.info("Generated {}", filePath.toString());
        }
    }

    /**
     * Generates boo.csv based on BOO entries in the specified directory.
     *
     * @param booEntries List of BOO entries
     * @param outputDir  Directory where the CSV will be saved
     * @throws IOException
     */
    private void generateBooCSV(List<BooEntry> booEntries, Path outputDir) throws IOException {
        String fileName = "boo.csv";
        Path filePath = outputDir.resolve(fileName);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toFile()), ';',
                CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            // Escrever cabeçalho
            String[] header = {"op_id", "item_id", "item_qtd",
                    "(;op1;op_qtd1;op2;op_qtd2;op3;op_qtd3;)",
                    "(;item_id1;item_qtd1;item_id2;item_qtd2;item_id3;item_qtd3;)"};
            writer.writeNext(header);

            // Escrever dados
            for (BooEntry entry : booEntries) {
                String subOps = formatSubOperations(entry.getSubOperations());
                String subItems = formatSubItems(entry.getSubItems());
                String itemQtyStr = String.valueOf(entry.getItemQuantity());

                String[] line = {
                        entry.getOpId(),
                        entry.getItemId(),
                        itemQtyStr,
                        subOps,
                        subItems
                };
                writer.writeNext(line);
            }

            logger.info("Generated {}", filePath.toString());
        }
    }

    /**
     * Formats sub-operations para o CSV.
     *
     * @param subOperations Lista de sub-operações
     * @return String formatada
     */
    private String formatSubOperations(List<BooSubOperation> subOperations) {
        if (subOperations.isEmpty()) {
            return "(;;;;;)";
        }
        StringBuilder sb = new StringBuilder("(;");
        int count = 0;
        for (BooSubOperation subOp : subOperations) {
            sb.append(subOp.getOpId()).append(";")
              .append(subOp.getOpQuantity()).append(";");
            count++;
            if (count >= 3) { // Limitar a 3 sub-operações
                break;
            }
        }
        // Preencher campos restantes se houver menos de 3 sub-operações
        while (count < 3) {
            sb.append(";;");
            count++;
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Formats sub-items para o CSV.
     *
     * @param subItems Lista de sub-itens
     * @return String formatada
     */
    private String formatSubItems(List<BooSubItem> subItems) {
        if (subItems.isEmpty()) {
            return "(;;;;;)";
        }
        StringBuilder sb = new StringBuilder("(;");
        int count = 0;
        for (BooSubItem subItem : subItems) {
            sb.append(subItem.getItemId()).append(";")
              .append(subItem.getItemQuantity()).append(";");
            count++;
            if (count >= 3) { // Limitar a 3 sub-itens
                break;
            }
        }
        // Preencher campos restantes se houver menos de 3 sub-itens
        while (count < 3) {
            sb.append(";;");
            count++;
        }
        sb.append(")");
        return sb.toString();
    }
}
