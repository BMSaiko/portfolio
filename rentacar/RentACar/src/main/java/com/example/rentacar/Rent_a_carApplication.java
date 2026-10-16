package com.example.rentacar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principal da aplicação Rent-a-car.
 */
public class Rent_a_carApplication extends Application {

    /**
     * Método de inicialização da aplicação JavaFX.
     *
     * @param stage O palco principal da aplicação.
     * @throws IOException Caso ocorra um erro ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args Os argumentos de linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
