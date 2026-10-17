package com.example.sheetsandpicks.BL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class util {

    /**
     * Cria e exibe uma nova cena com base no arquivo FXML fornecido.
     *
     * @param fxmlPath O caminho do arquivo FXML.
     * @param title     O título da janela.
     */
    public static void criarCena(String fxmlPath, String title) {
        try {
            // Carregar o arquivo FXML
            FXMLLoader loader = new FXMLLoader(util.class.getResource("/"+fxmlPath));
            Parent root = loader.load();


            // Criar uma nova cena com o layout carregado do FXML
            Scene scene = new Scene(root);

            // Configurar o palco (Stage) e exibir a cena
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Lida com exceções de carregamento do FXML
        }
    }
}
