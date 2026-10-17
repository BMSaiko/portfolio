package com.example.sheetsandpicks.Controllers.app_login;

import com.example.sheetsandpicks.Controllers.database.DBConnect;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginService {
    private DBConnect databaseConnection;

    /**
     * Cria uma instância da classe LoginService.
     * Inicializa a conexão com o banco de dados.
     */
    public LoginService() {
        databaseConnection = DBConnect.getInstance();
    }


    /**
     * Verifica se o login é válido para o nome de utilizador e palavra-passe fornecidos.
     *
     * @param username O nome de utilizador inserido.
     * @param password A palavra-passe inserida (ainda está criptografada com SHA-256).
     * @return true se o login for válido, false caso contrário.
     */
    public boolean verifyLogin(String username, String password) {
        try {
            Connection connection = databaseConnection.getConnection();
            String query = "SELECT password FROM acesso WHERE username = '" + username + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");

                // Descriptografar a senha fornecida antes de comparar
                String inputHash = hashPassword(password);

                if (storedHash.equals(inputHash)) {
                    System.out.println("Login bem-sucedido.");
                    return true;
                } else {
                    System.out.println("Senha incorreta.");
                }
            } else {
                System.out.println("Usuário não encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Método para criar o hash SHA-256 da palavra-passe.
     *
     * @param password A senha a ser criptografada.
     * @return O hash SHA-256 da senha.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtém o nível de acesso de um usuário com base no nome de utilizador.
     *
     * @param username O nome de utilizador do qual se deseja obter o nível de acesso.
     * @return O nível de acesso do utilizador ou null se não for encontrado.
     * @throws SQLException Se ocorrer uma exceção ao executar a consulta SQL.
     */
    public String getNivelAcesso(String username) {
        try {
            Connection connection = databaseConnection.getConnection();
            String query = "SELECT p.Tipo AS nivel_permissao " +
                    "FROM acesso a " +
                    "JOIN Funcionarios f ON a.id = f.id_acesso " +
                    "JOIN permissao p ON f.id_permissao = p.id " +
                    "WHERE a.username = '" + username + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("SQL Query: " + query);
            if (resultSet.next()) {
                return resultSet.getString("nivel_permissao");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retornar um valor padrão ou lançar uma exceção, dependendo dos requisitos do seu aplicativo
        return null;
    }
}