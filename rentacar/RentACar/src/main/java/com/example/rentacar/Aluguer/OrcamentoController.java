package com.example.rentacar.Aluguer;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Veiculo.Veiculo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class OrcamentoController {

    @FXML
    private ComboBox<String> comboboxEstadoOrcamento;

    @FXML
    private Button but_continuar;

    @FXML
    private TableColumn<Veiculo, String> c_categoria;

    @FXML
    private TableColumn<Veiculo, String> c_cilindrada;

    @FXML
    private TableColumn<Veiculo, String> c_combustivel;

    @FXML
    private TableColumn<Veiculo, String> c_cv;

    @FXML
    private TableColumn<Veiculo, String> c_disponibilidade;

    @FXML
    private TableColumn<Veiculo, String> c_inspecao;

    @FXML
    private TableColumn<Veiculo, String> c_kms;

    @FXML
    private TableColumn<Veiculo, String> c_limpeza;

    @FXML
    private TableColumn<Veiculo, String> c_manutencao;

    @FXML
    private TableColumn<Veiculo, String> c_marca;

    @FXML
    private TableColumn<Veiculo, String> c_matricula;

    @FXML
    private TableColumn<Veiculo, String> c_modelo;

    @FXML
    private TableColumn<Veiculo, String> c_revisao;

    @FXML
    private TableColumn<Veiculo, String> c_seguro;

    @FXML
    private TableView<Veiculo> tableVeiculos;

    @FXML
    public Label labelCliente;

    @FXML
    private Label labelDesconto;

    @FXML
    public Label labelEstado;

    @FXML
    public Label labelIVA;

    @FXML
    public Label labelNumEmpregado;

    @FXML
    private Label labelTotalliquido;

    @FXML
    Label labelvalorcarro;

    @FXML
    private Label labelvalortotalbruto;

    public int id_orcamentos;

    private static ObservableList<Veiculo> veiculosList = FXCollections.observableArrayList();

    /**

     Adds a Veiculo object to the veiculosList.
     @param veiculo The Veiculo object to add.
     */
    public void adicionarVeiculos(List<Veiculo> veiculo) {
        // Limpar a tabela antes de adicionar os novos veículos
        tableVeiculos.getItems().clear();

        // Adicionar os veículos à tabela
        tableVeiculos.getItems().addAll(veiculo);
    }

    /**

     Returns the ObservableList of Veiculo objects.
     @return The ObservableList of Veiculo objects.
     */
    public static ObservableList<Veiculo> getVeiculosList() {
        return veiculosList;
    }

    /**

     Sets the veiculosList to the provided ObservableList of Veiculo objects.
     @param veiculosList The ObservableList of Veiculo objects to set.
     */
    public static void setVeiculosList(ObservableList<Veiculo> veiculosList) {
        OrcamentoController.veiculosList = veiculosList;
    }

    public void setLabelIdOrcamento(int idOrcamento) {
        id_orcamentos = idOrcamento;
    }

    /**

     Initializes the OrcamentoController.
     */
    @FXML
    void initialize() {

        comboboxEstadoOrcamento.setItems(getEstadosOrcamentoFromDatabase());

        // Configure as colunas da tabela
        c_categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        c_cilindrada.setCellValueFactory(new PropertyValueFactory<>("cilindrada"));
        c_combustivel.setCellValueFactory(new PropertyValueFactory<>("combustivel"));
        c_cv.setCellValueFactory(new PropertyValueFactory<>("cv"));
        c_disponibilidade.setCellValueFactory(new PropertyValueFactory<>("disponibilizado"));
        c_inspecao.setCellValueFactory(cellData -> {
            LocalDate inspecao = cellData.getValue().getUlt_inspecao();
            return inspecao != null ? new SimpleStringProperty(inspecao.toString()) : new SimpleStringProperty("");
        });
        c_kms.setCellValueFactory(new PropertyValueFactory<>("kms"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<>("limpeza"));
        c_manutencao.setCellValueFactory(new PropertyValueFactory<>("id_manutencao"));
        c_marca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        c_matricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        c_revisao.setCellValueFactory(cellData -> {
            LocalDate revisao = cellData.getValue().getProx_revisao();
            return revisao != null ? new SimpleStringProperty(revisao.toString()) : new SimpleStringProperty("");
        });
        c_seguro.setCellValueFactory(new PropertyValueFactory<>("seguro"));

        tableVeiculos.setItems(veiculosList);
    }

    private ObservableList<String> getEstadosOrcamentoFromDatabase() {
        ObservableList<String> estados = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT estado_orcamento from estado_orcamento";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String estado = resultSet.getString("estado_orcamento");
                estados.add(estado);
            }

            // Fechar todos os resultados
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estados;
    }

    /**

     Handles the action when the "Continuar" button is clicked.
     @param event The ActionEvent triggered by the button click.
     */
    public void but_continuar(ActionEvent event) throws SQLException {
        // Obtém o ID do orçamento
        int idOrcamento = id_orcamentos;

        String estadoOrcamentoSelecionado = comboboxEstadoOrcamento.getValue();

        String query = "SELECT id FROM estado_orcamento WHERE estado_orcamento = '" + estadoOrcamentoSelecionado + "'";

        try {
            // Estabelecer uma conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Verifica se há um resultado válido
            if (resultSet.next()) {
                int idEstadoOrcamento = resultSet.getInt("id");
                String updateQuery = "UPDATE Orcamento SET estado_orcamento = " + idEstadoOrcamento + " WHERE id = " + idOrcamento;
                statement.executeUpdate(updateQuery);

                // Fecha a página atual
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public LocalDate dataInicio;
    public LocalDate dataFim;
    public static double valorDiario;

    /**

     Sets the start date for the Orcamento.
     @param dataInicio The start date to set for the Orcamento.
     */
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setLabelCliente(String text) {
        labelCliente.setText(text);
    }

    public void setLabelTotalliquido(String text) {
        labelTotalliquido.setText(text);
    }

    public void setLabelIVA(String text) {
        labelIVA.setText(text);
    }

    public void setLabelvalortotalbruto(String text) {
        labelvalortotalbruto.setText(text);
    }

    public void setLabelDesconto(String text) {
        labelDesconto.setText(text);
    }

    public void setLabelNumEmpregado(String text) {
        labelNumEmpregado.setText(text);
    }

    public void setLabelEstado(String text) {
        labelEstado.setText(text);
    }

    public void setLabelValorCarro(String text) {
        labelvalorcarro.setText(text);
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    // ...

    // ...

    /**

     Sets the daily value for the Orcamento.
     @param valorDiario The daily value to set for the Orcamento.
     */
    public void setValorDiario(double valorDiario) {
        this.valorDiario = valorDiario;
    }
}
