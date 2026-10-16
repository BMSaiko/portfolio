package com.example.rentacar.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    /**

     Private constructor to prevent direct instantiation.

     Establishes a connection to the SQL Server database.
     */
    private DatabaseConnection() {
        try {
            // Provide the connection URL
            String connectionUrl = "jdbc:sqlserver://ctespbd.dei.isep.ipp.pt;databaseName=2023_DIAS_G4_ERMESINDE;user=2023_DIAS_G4_ERMESINDE;password=" + System.getenv("RENTACAR_DB_PASSWORD") + ";";
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Establish the connection
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("Connected to the SQL Server database.");
        } catch (SQLException e) {
            System.out.println("Connection to the SQL Server database failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**

     Returns the instance of the DatabaseConnection.
     @return The DatabaseConnection instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**

     Returns the connection object to the SQL Server database.
     @return The database connection.
     */
    public Connection getConnection() {
        return connection;
    }
}

