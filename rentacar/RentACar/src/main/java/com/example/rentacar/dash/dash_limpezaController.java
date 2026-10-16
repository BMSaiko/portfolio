package com.example.rentacar.dash;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class dash_limpezaController implements Initializable {

    @FXML
    private Button butLimpar;

    @FXML
    private TableColumn<Limpeza, LocalDate> c_datainic;

    @FXML
    private TableColumn<Limpeza, String> c_limpeza;

    @FXML
    private TableColumn<Limpeza, String> c_descricao;

    @FXML
    private TableColumn<Limpeza, String> c_matricula;


    @FXML
    private TableView<Limpeza> tableLimpeza;

    @FXML
    private TextField txtProcurar;

    ArrayList<Limpeza> Limpezalist = Limpeza.LimpezaList;

    ObservableList<Limpeza> observablelimpezaList;


    /**

     Handles the action when the "Limpa" button is clicked.
     @param event The action event.
     */
    @FXML
    void HandelerLimpa(ActionEvent event) {
        Limpeza selectedItem = tableLimpeza.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String matricula = selectedItem.getMatricula();
            String query = "UPDATE Veiculos\n" +
                    "SET id_limpeza=2\n" +
                    "FROM Veiculos\n" +
                    "WHERE matricula = '"+matricula+"';";
            try{
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();
                statement.executeUpdate(query);
            }catch(SQLException e){
                e.printStackTrace();
            }
            tableLimpeza.getItems().remove(selectedItem);
        }
    }

    /**

     Retrieves a list of Limpeza objects from the database.

     @return The list of Limpeza objects.
     */
    public List<Limpeza> getLimpeza(){
        String query = "SELECT l.stat,C.data_inic,C.matricula,l.Descricao \n" +
                "FROM Veiculos\n" +
                "INNER JOIN Contratos AS C ON C.matricula=Veiculos.matricula\n" +
                "INNER JOIN limpeza AS l ON Veiculos.id_limpeza=l.id\n" +
                "WHERE Veiculos.id_limpeza=1";

        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                String matricula = resultSet.getString("matricula");
                LocalDate data_inic = resultSet.getDate("data_inic").toLocalDate();
                String limpeza = resultSet.getString("stat");
                String Descricao = resultSet.getString("Descricao");

                Limpeza limpeza1 = new Limpeza(matricula,data_inic,limpeza,Descricao);
                observablelimpezaList = FXCollections.observableList(Limpezalist);
                Limpezalist.add(limpeza1);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return Limpezalist;
    }

    /**

     Populates the Limpeza table with data.
     */
    public void tablePopulate(){
        List<Limpeza> LimpezaList = getLimpeza();

        c_matricula.setCellValueFactory(new PropertyValueFactory<Limpeza, String>("matricula"));
        c_datainic.setCellValueFactory(new PropertyValueFactory<Limpeza, LocalDate>("data_inic"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<Limpeza, String>("limpeza"));
        c_descricao.setCellValueFactory(new PropertyValueFactory<>("Descricao"));

        tableLimpeza.setItems(FXCollections.observableList(LimpezaList));
    }

    /**

     Handles the action when the textbox is pressed.
     */
    @FXML
    void textboxpress() {
        procurarLimpeza();
    }

    /**

     Searches and filters the Limpeza table based on the entered text.
     */
    @FXML
    void procurarLimpeza() {
        tableLimpeza.setItems(observablelimpezaList);
        if(observablelimpezaList != null) {
            FilteredList<Limpeza> filteredList = new FilteredList<>(observablelimpezaList, b -> true);
            txtProcurar.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(Limpa -> {
                    if (newValue.isBlank()) {
                        return true;
                    }
                    String palavra = newValue.toLowerCase();

                    return Limpa.getMatricula().contains(palavra)
                            || String.valueOf(Limpa.getData_inic()).contains(palavra)
                            || String.valueOf(Limpa.getLimpeza()).contains(palavra);
                });
            });
            SortedList<Limpeza> sortedData = new SortedList<>(filteredList);
            sortedData.comparatorProperty().bind(tableLimpeza.comparatorProperty());
            tableLimpeza.setItems(sortedData);
        }
    }

    /**

     Handles the action when a row in the Limpeza table is double-clicked.
     */
    private void selectRow() {
        tableLimpeza.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Limpeza selectedLimpeza = tableLimpeza.getSelectionModel().getSelectedItem();
                if (selectedLimpeza != null) {
                    String matricula = selectedLimpeza.getMatricula();
                    System.out.println("Double-clicked: " + matricula);
                }
            }
        });
    }

    /**

     Initializes the controller.
     @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Limpeza.LimpezaList.removeAll(Limpezalist);
        tablePopulate();
        tableLimpeza.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableLimpeza.setItems(observablelimpezaList);
    }
}

