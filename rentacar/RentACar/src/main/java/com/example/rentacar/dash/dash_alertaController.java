package com.example.rentacar.dash;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Menu.notifi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class dash_alertaController implements Initializable {

    @FXML
    private TableColumn<notifi, String> c_descricao;

    @FXML
    private TableColumn<notifi, String> c_matricula;

    @FXML
    private TableColumn<notifi, String> c_tipo;

    @FXML
    private TableView<notifi> tableAlerta;

    /**

     Initializes the controller by populating the table with notifications.
     @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTableNotificacoes();
    }

    /**

     Retrieves the list of notifications from the database.

     @return The list of notifications.
     */
    public List<notifi> getNotificacoes() {
        List<notifi> notificacoesList = new ArrayList<>();
        String query = "SELECT * FROM notifica";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String matricula = resultSet.getString("matricula");
                String tipo = resultSet.getString("tipo");
                String descricao = resultSet.getString("Descricao");

                Timestamp timestamp = resultSet.getTimestamp("hora_recebimento");
                LocalDateTime hora = timestamp.toLocalDateTime();

                notifi notificacao = new notifi(id, matricula, tipo, descricao, hora);
                notificacoesList.add(notificacao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificacoesList;
    }


    /**

     Populates the table with the list of notifications.
     */
    private void populateTableNotificacoes() {
        List<notifi> notificacoesList = getNotificacoes();
        ObservableList<notifi> observableNotificacoesList = FXCollections.observableArrayList(notificacoesList);

        c_descricao.setCellValueFactory(new PropertyValueFactory<>("Descricao"));
        c_matricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        c_tipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        tableAlerta.setItems(observableNotificacoesList);
    }


}
