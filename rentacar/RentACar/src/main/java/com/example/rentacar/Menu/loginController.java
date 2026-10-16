package com.example.rentacar.Menu;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;

public class loginController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Button butLogin;

    @FXML
    private TextField username_txt;

    @FXML
    private TextField password_txt;
    @FXML
    private VBox vbox;


    private String password;
    private String username;

    /**
     * Manipula o evento de pressionar o botão. Fecha a janela atual e abre o menu principal se a tecla Enter for pressionada.
     */
    @FXML
    void onPress() {
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER ) {
                fecharJanelaAtual();
                criarCena("/com/example/rentacar/app_menu.fxml", "Menu");
            }
        });
    }

    /**
     * Realiza o processo de login quando o botão de login é clicado. Verifica o nome de usuário e senha inseridos e abre o menu principal se o login for válido.
     *
     * @param event O ActionEvent acionado pelo clique no botão de login.
     * @throws SQLException Se ocorrer uma exceção SQL durante o acesso ao banco de dados.
     */
    @FXML
    void fazerLogin(ActionEvent event) throws SQLException {
        // Obter a referência da janela de login atual
        Stage janelaLogin = (Stage) butLogin.getScene().getWindow();

        username = username_txt.getText();
        password = password_txt.getText();
        LoginService loginService = new LoginService();
        boolean isValidLogin = loginService.verifyLogin(username, password);
        if (isValidLogin) {
            fecharJanelaAtual();
            criarCena("/com/example/rentacar/app_menu.fxml", "Menu");
        } else {
            username_txt.setText("");
            password_txt.setText("");
        }
    }

    /**
     * Fecha a janela atual.
     */
    private void fecharJanelaAtual() {
        // Obter a referência da janela de login atual
        Stage janelaLogin = (Stage) butLogin.getScene().getWindow();

        // Fechar a janela atual
        janelaLogin.close();
    }

    /**
     * Cria uma nova cena e abre uma nova janela com base no arquivo FXML especificado e título da janela.
     *
     * @param arquivofxml O caminho do arquivo FXML.
     * @param Mensagem    O título da nova janela.
     */
    private void criarCena(String arquivofxml, String Mensagem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivofxml));
            Parent root = loader.load();
            MenuController targetController = loader.getController();

            if (username != null) {
                targetController.enviardados(buscarnv_acesso(username), username, targetController);
            } else {
                targetController.enviardados(3, "administrador/gerente", targetController);
            }

            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar a janela
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.initStyle(StageStyle.UNDECORATED); // Configurar a janela como estilo UNDECORATED
            novaJanela.setTitle(Mensagem);
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera o nível de acesso de um usuário com base no nome de usuário fornecido.
     *
     * @param username O nome de usuário do usuário.
     * @return O nível de acesso do usuário.
     * @throws SQLException Se ocorrer uma exceção SQL durante o acesso ao banco de dados.
     */
    private int buscarnv_acesso(String username) throws SQLException {
        String query = "SELECT nivel_acesso FROM Funcionario WHERE nome='" + username + "';";
        int nv_acesso = -1;
        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
        Statement statement = databaseConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            nv_acesso = resultSet.getInt("nivel_acesso");
        }
        return nv_acesso;
    }

}
