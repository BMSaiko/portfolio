package com.example.rentacar.dash;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Veiculo.Veiculo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class dash_inspecao_revisaoController implements Initializable {

    @FXML
    private Button butConfirmar;

    @FXML
    private TableColumn<revisao, Integer> c_kms;

    @FXML
    private TableColumn<revisao, String> c_descricao;

    @FXML
    private TableColumn<Inspecao, LocalDate> c_data_inspecao;

    @FXML
    private TableColumn<revisao, LocalDate> c_data_revisao;

    @FXML
    private TableColumn<Inspecao, String> c_matricula_inspecao;

    @FXML
    private TableColumn<revisao, String> c_matricula_revisao;

    @FXML
    private TableView<Inspecao> tableInsp;

    @FXML
    private TableView<revisao> tableRev;

    @FXML
    private TextField txtProcurar;

    private ArrayList<Inspecao> Inspecaolist = Inspecao.InspecaoList;

    ObservableList<Inspecao> observableInspecaoList;

    ObservableList<revisao> observablerevisaoList;

    private ArrayList<revisao> revisaolist = revisao.revisaoList;

    /**

     Performs the action when the ConfirmButton is clicked.

     If an Inspecao is selected, updates the database with a new inspection date and removes the corresponding row from the table.

     If a revisao is selected, updates the database with a new revision date and revision kilometers and removes the corresponding row from the table.

     @param event The event that triggered the action.
     */
    @FXML
    void ConfirmButton(ActionEvent event) {
        Inspecao inspecaoSelecionada = tableInsp.getSelectionModel().getSelectedItem();
        revisao revisaoSelecionada = tableRev.getSelectionModel().getSelectedItem();
        if (inspecaoSelecionada != null) {
            LocalDate DataInspecao = inspecaoSelecionada.getInspecao();
            LocalDate novaDataInspecao = DataInspecao.plusYears(1);
            // Crie o código SQL para remover a linha do banco de dados usando a matrícula como critério
            String query = "UPDATE inspecao SET ult _inspecao = '" + novaDataInspecao + "' WHERE matricula = '" + DataInspecao + "'";

            try {
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();
                statement.executeUpdate(query);
                // Remova a linha da lista de dados da TableView
                tableInsp.getItems().remove(inspecaoSelecionada);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (revisaoSelecionada != null) {

            String matricula = revisaoSelecionada.getMatricula();
            LocalDate revisaoAtual = revisaoSelecionada.getData_revisao();
            LocalDate novaDatarevisao = revisaoAtual.plusYears(1);

            // Obtém o valor de "kms" com base na matrícula do veículo
            String getKmsQuery = "SELECT kms FROM Veiculos WHERE matricula = '" + matricula + "'";
            tableRev.getItems().remove(revisaoSelecionada);
            try {
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement getKmsStatement = databaseConnection.getConnection().createStatement();
                ResultSet getKmsResult = getKmsStatement.executeQuery(getKmsQuery);

                if (getKmsResult.next()) {
                    int kms_revisao = getKmsResult.getInt("kms");

                    // Obtém o ID da revisão com base na matrícula do veículo
                    String getIdRevisaoQuery = "SELECT v.id_revisao FROM Veiculos v WHERE v.matricula = '" + matricula + "'";

                    Statement getIdRevisaoStatement = databaseConnection.getConnection().createStatement();
                    ResultSet getIdRevisaoResult = getIdRevisaoStatement.executeQuery(getIdRevisaoQuery);

                    if (getIdRevisaoResult.next()) {
                        int idRevisao = getIdRevisaoResult.getInt("id_revisao");

                        // Atualiza as colunas prox_revisao e ult_revisao_kms na tabela revisao com os novos valores
                        String updateRevisaoQuery = "UPDATE revisao SET prox_revisao = '" + novaDatarevisao + "', ult_revisao_kms = " + kms_revisao + " WHERE id = " + idRevisao;

                        Statement updateRevisaoStatement = databaseConnection.getConnection().createStatement();
                        updateRevisaoStatement.executeUpdate(updateRevisaoQuery);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**

     Retrieves a list of Inspecao objects from the database.

     @return The list of Inspecao objects.
     */
    public List<Inspecao> getInspecao() {
        String query = "SELECT v.matricula AS matricula, DATEADD(YEAR, 1, i.ult_inspecao) AS ult_inspecao\n" +
                "FROM Veiculos v\n" +
                "INNER JOIN inspecao i ON v.id_inspecao = i.id\n" +
                "WHERE DATEDIFF(MONTH, GETDATE(), DATEADD(YEAR, 1, i.ult_inspecao)) <= 1;";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the list before adding elements
            Inspecaolist.clear();

            while (resultSet.next()) {
                String matricula = resultSet.getString("matricula");
                LocalDate inspecao = resultSet.getDate("ult_inspecao").toLocalDate();

                Inspecao inspecao1 = new Inspecao(matricula, inspecao);
                observableInspecaoList = FXCollections.observableList(Inspecaolist);
                Inspecaolist.add(inspecao1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Inspecaolist;
    }

    /**

     Retrieves a list of revisao objects from the database.

     @return The list of revisao objects.
     */
    public List<revisao> getRevisao() {
        String query = "SELECT v.matricula AS matricula, DATEADD(YEAR, 1, r.prox_revisao) AS prox_revisao,(r.ult_revisao_kms + r.revisao_kms) AS prox_revisao_kms ,r.Descricao As descricao\n" +
                "FROM Veiculos v\n" +
                "INNER JOIN revisao r ON v.id_revisao = r.id\n" +
                "WHERE (DATEDIFF(MONTH, DATEADD(MONTH, 1, r.prox_revisao), GETDATE()) <= 1 AND DATEDIFF(YEAR, DATEADD(YEAR, 1, r.prox_revisao), GETDATE()) = 0)\n" +
                "OR ((r.ult_revisao_kms + r.revisao_kms) - v.kms) <= 500;";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Clear the existing revisaolist
            revisaolist.clear();

            while (resultSet.next()) {
                String matricula = resultSet.getString("matricula");
                LocalDate data_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                int kms_revisao = resultSet.getInt("prox_revisao_kms");
                String descricao = resultSet.getString("descricao");

                revisao revisao1 = new revisao(matricula, data_revisao, kms_revisao,descricao);
                observablerevisaoList = FXCollections.observableList(revisaolist);
                revisaolist.add(revisao1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revisaolist;
    }

    /**

     Populates the TableView for Inspecao data.
     */
    public void tablePopulate(){
        List<Inspecao> InspecaoList = getInspecao();

        c_matricula_inspecao.setCellValueFactory(new PropertyValueFactory<Inspecao, String>("matricula"));
        c_data_inspecao.setCellValueFactory(new PropertyValueFactory<Inspecao,LocalDate>("inspecao"));

        tableInsp.setItems(FXCollections.observableList(InspecaoList));
    }

    /**

     Populates the TableView for revisao data.
     */
    public void tablePopulate2() {
        List<revisao> revisaoList = getRevisao();

        c_matricula_revisao.setCellValueFactory(new PropertyValueFactory<revisao, String>("matricula"));
        if (revisaoList.get(0).getData_revisao() != null) {
            c_data_revisao.setCellValueFactory(new PropertyValueFactory<revisao, LocalDate>("data_revisao"));
        }else{
            c_data_revisao.setCellValueFactory(celldata -> new SimpleObjectProperty<>(null));
        }
        if (revisaoList.get(0).getProx_revisao_kms()!= 0) {
            c_kms.setCellValueFactory(new PropertyValueFactory<revisao, Integer>("prox_revisao_kms"));
        }else{
            c_kms.setCellValueFactory(celldata -> new SimpleObjectProperty<>(null));
        }
        c_descricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));


        tableRev.setItems(FXCollections.observableList(revisaoList));
    }

    /**

     Executes when the textbox is pressed.
     */
    public void textboxPress(){
        procurarVeiculo();
    }

    /**

     Searches for vehicles based on the entered text.
     */
    public void procurarVeiculo() {
        observableInspecaoList = FXCollections.observableArrayList(getInspecao());
        observablerevisaoList = FXCollections.observableArrayList(getRevisao());

        tableInsp.setItems(observableInspecaoList);
        tableRev.setItems(observablerevisaoList);

        FilteredList<Inspecao> filteredList = new FilteredList<>(observableInspecaoList, b -> true);
        txtProcurar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(veiculo -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getInspecao()).toLowerCase().contains(palavra);
            });
        });

        SortedList<Inspecao> sortedData1 = new SortedList<>(filteredList);
        sortedData1.comparatorProperty().bind(tableInsp.comparatorProperty());
        tableInsp.setItems(sortedData1);

        FilteredList<revisao> filteredList1 = new FilteredList<>(observablerevisaoList, b -> true);
        txtProcurar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList1.setPredicate(veiculo1 -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String palavra1 = newValue.toLowerCase();

                return veiculo1.getMatricula().toLowerCase().contains(palavra1)
                        || String.valueOf(veiculo1.getData_revisao()).toLowerCase().contains(palavra1)
                        || String.valueOf(veiculo1.getProx_revisao_kms()).toLowerCase().contains(palavra1);
            });
        });

        SortedList<revisao> sortedData2 = new SortedList<>(filteredList1);
        sortedData2.comparatorProperty().bind(tableRev.comparatorProperty());
        tableRev.setItems(sortedData2);
    }

    /**

     Initializes the controller.
     @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        observableInspecaoList = FXCollections.observableArrayList(getInspecao());
        observablerevisaoList = FXCollections.observableArrayList(getRevisao());
        tablePopulate();
        tablePopulate2();
        tableInsp.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableInsp.setItems(observableInspecaoList);
        tableRev.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableRev.setItems(observablerevisaoList);
    }
}
