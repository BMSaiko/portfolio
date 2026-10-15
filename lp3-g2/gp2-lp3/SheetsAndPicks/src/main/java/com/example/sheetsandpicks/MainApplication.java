package com.example.sheetsandpicks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    /**
     * Inicia a aplicação JavaFX, carregando a interface do utilizador a partir do arquivo FXML e exibindo-a em uma janela de palco.
     *
     * @param stage O palco (Stage) principal onde a interface do utilizador irá aparecer.
     * @throws IOException Se ocorrer um erro durante o carregamento do arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("app_login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280,720 );
        stage.setTitle("Login - Feira&Office");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * O método principal de inicialização da aplicação JavaFX.
     *
     * @param args Os parametros da linha de comando, dos quais não é utilizados.
     */

    public static void main(String[] args) {
        launch();
    }
}