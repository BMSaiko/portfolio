package com.example.rentacar.Menu;

import com.example.rentacar.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginService {
    private DatabaseConnection databaseConnection;

    /**
     * Cria uma instância da classe LoginService.
     * Inicializa a conexão com o banco de dados.
     */
    public LoginService() {
        databaseConnection = DatabaseConnection.getInstance();
    }


    /**
     * Verifica se o login é válido para o nome de usuário e senha fornecidos.
     *
     * @param username O nome de usuário inserido.
     * @param password A senha inserida.
     * @return true se o login for válido, false caso contrário.
     */
    public boolean verifyLogin(String username, String password) {
        try {
            Connection connection = databaseConnection.getConnection();
            String query = "SELECT dbo.verify_login('" + username + "','" + password + "')";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("Result set has more than 0 rows.");
                    return true;
                }
            } else {
                System.out.println("Result set is empty.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
