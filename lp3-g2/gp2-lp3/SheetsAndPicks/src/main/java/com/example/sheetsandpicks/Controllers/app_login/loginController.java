package com.example.sheetsandpicks.Controllers.app_login;

import com.example.sheetsandpicks.Controllers.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    @FXML
    private Button butLogin;

    @FXML
    private PasswordField passField;

    @FXML
    private TextField txtUtilizador;

    @FXML
    private HBox pane;

    /**
     * Manipula o evento de pressionar o botão. Fecha a janela atual e abre o menu principal se a tecla Enter for pressionada.
     */
    @FXML
    void onPress() {
        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER ) {
                fecharJanelaAtual();
                LoginService loginService = new LoginService();
                boolean isValidLogin = loginService.verifyLogin("admin", "admin");
                String  nivelPermissao = loginService.getNivelAcesso("admin");
                mostrarMainViewComNivelPermissao(nivelPermissao);
                    //util.criarCena("/com/example/sheetsandpicks/main-view.fxml", "Welcome!");
            }
        });
    }

    /**
     * Manipula o evento de clique no botão de login.
     * Obtém as informações de login do utilizador, verifica a validade das credenciais e,
     * se válidas, exibe a tela principal com base no nível de permissão do utilizador.
     *
     * @param event O evento de clique no botão de login.
     * @throws SQLException Se ocorrer uma exceção ao acessar a base de dados.
     */
    @FXML
    void fazerLogin(ActionEvent event) throws SQLException {
        // Obter a referência da janela de login atual
        Stage janelaLogin = (Stage) butLogin.getScene().getWindow();

        String username = txtUtilizador.getText();
        String password = passField.getText();
        LoginService loginService = new LoginService();
        boolean isValidLogin = loginService.verifyLogin(username, password);
        if (isValidLogin) {
            String  nivelPermissao = loginService.getNivelAcesso(username);
            mostrarMainViewComNivelPermissao(nivelPermissao);
        } else {
            txtUtilizador.setText("");
            passField.setText("");
        }
    }

    /**
     * Fecha a janela atual.
     * Obtém a referência da janela atual através de um controlo na cena
     * e fecha a janela ao utilizar o método close().
     */
    private void fecharJanelaAtual() {
        // Obter a referência da janela de login atual
        Stage janelaLogin = (Stage) butLogin.getScene().getWindow();

        // Fechar a janela atual
        janelaLogin.close();
    }

    /**
     * Exibe a página principal com base no nível de permissão do utilizador.
     * Carrega o arquivo FXML da página principal, obtém o controlador da página principal,
     * passa o nível de permissão para o controlador e configura e exibe o palco (Stage).
     * Por fim, fecha a janela de login atual.
     *
     * @param nivelPermissao O nível de permissão do utilizador.
     */
    private void mostrarMainViewComNivelPermissao(String  nivelPermissao) {
        // Carregar o arquivo FXML da página principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sheetsandpicks/main-view.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Obter o controlador da página principal
        MainController mainController = loader.getController();

        // Passar o nível de permissão para o controlador da página principal
        mainController.atualizarComboUti(nivelPermissao);
        System.out.println(nivelPermissao);

        // Configurar o palco (Stage) e exibir a cena
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Feira&Office - APP");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        // Fechar a janela de login atual
        fecharJanelaAtual();
    }

    /**
     * Método chamado automaticamente durante a inicialização do controlador.
     * Inicializa a lógica necessária quando a interface gráfica associada a este controlador é carregada.
     * Neste caso, chama o método onPress() para realizar ações específicas na inicialização.
     *
     * @param location   A localização relativa do FXML especificado no método FXMLLoader.load().
     * @param resources  Os recursos específicos do local que podem ser usados para internacionalização.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        onPress();
    }
}
