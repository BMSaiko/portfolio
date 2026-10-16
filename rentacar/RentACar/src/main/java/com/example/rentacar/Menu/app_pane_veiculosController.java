package com.example.rentacar.Menu;


import com.example.rentacar.Aluguer.Criar_AluguerController;
import com.example.rentacar.Aluguer.Edit_AluguerController;
import com.example.rentacar.Cliente.Clientes;
import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Veiculo.Editar_VeiculoController;
import com.example.rentacar.Veiculo.Veiculo;
import com.example.rentacar.Veiculo.infoVeiculoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class app_pane_veiculosController implements Initializable {

    @FXML
    private Button butAdicioanarVeiculos;

    @FXML
    private Button butEditarVeiculos;

    @FXML
    private Button butProcurarVeiculos;

    @FXML
    private Button butRetirarVeiculos;

    @FXML
    private Button butVerVeiculos;

    @FXML
    private TableView<Veiculo> tableVeiculos;
    @FXML
    private TableColumn<Veiculo, String> c_matricula;
    @FXML
    private TableColumn<Veiculo, String> c_marca;
    @FXML
    private TableColumn<Veiculo, Integer> c_kms;
    @FXML
    private TableColumn<Veiculo, String> c_seguro;
    @FXML
    private TableColumn<Veiculo, Date> c_revisao;
    @FXML
    private TableColumn<Veiculo, Date> c_inspecao;
    @FXML
    private TableColumn<Veiculo, String> c_limpeza;
    @FXML
    private TableColumn<Veiculo, String > c_categoria;
    @FXML
    private TableColumn<Veiculo, String> c_modelo;
    @FXML
    private TableColumn<Veiculo, Integer> c_cv;
    @FXML
    private TableColumn<Veiculo, Integer> c_cilindrada;
    @FXML
    private TableColumn<Veiculo, String> c_combustivel;
    @FXML
    private TableColumn<Veiculo, Integer> c_manutencao;
    @FXML
    private TableColumn<Veiculo, String> c_disponibilidade;

    @FXML
    private TextField txtVeiculos;

    private int idSpecs;
    private int idMM;

    private int idRevisao;
    ArrayList<Veiculo> veiculoList = Veiculo.veiculoList;
    ObservableList<Veiculo> observableVeiculoList;

    /**
     * Abre uma nova janela para adicionar um novo veículo.
     *
     * @param event O evento do clique no botão.
     */
    @FXML
    void adicionarVeiculos(ActionEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/rentacar/criar_veiculo_info.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle("Criar Veiculo");
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**
     * Método chamado quando o texto da caixa de pesquisa de veículos é alterado.
     * Filtra os veículos com base no texto digitado.
     */
    @FXML
    void textboxPress(){
        procurarVeiculos();
    }

    /**
     * Procura veículos com base no texto digitado na caixa de pesquisa.
     * Atualiza a tabela de veículos com os resultados filtrados.
     */
    @FXML
    void procurarVeiculos() {
        tableVeiculos.setItems(observableVeiculoList);

        FilteredList<Veiculo> filteredList = new FilteredList<>(observableVeiculoList, b -> true);
        txtVeiculos.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(veiculo -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().toLowerCase().contains(palavra)
                        || veiculo.getMarca().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getKms()).contains(palavra)
                        || veiculo.getSeguro().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getProx_revisao()).toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getUlt_inspecao()).toLowerCase().contains(palavra)
                        || veiculo.getLimpeza().toLowerCase().contains(palavra)
                        || veiculo.getCategoria().toLowerCase().contains(palavra)
                        || veiculo.getModelo().toLowerCase().contains(palavra);
            });

        });
        SortedList<Veiculo> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableVeiculos.comparatorProperty());
        tableVeiculos.setItems(sortedData);
    }

    /**
     * Remove um veículo da base de dados e da tabela de veículos.
     */
    @FXML
    void retirarVeiculos(ActionEvent event) {
        removeVehicles();
    }

    /**
     * Remove o veículo selecionado da base de dados e da tabela de veículos.
     */
    void removeVehicles(){
        Veiculo selectedItem = tableVeiculos.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem);
        if (selectedItem != null) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setHeaderText("Confirmação de ação");
            a.setContentText("Deseja mesmo eliminar o veiculo?");

            Optional<ButtonType> option = a.showAndWait();
            if (option.isPresent()) {

                if (option.get() == ButtonType.OK) {
                    try{
                        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                        Statement statement = databaseConnection.getConnection().createStatement();
                        String matricula = selectedItem.getMatricula();

                        String query1 = "SELECT id_specs from Veiculos where matricula= '"+ matricula + "'";
                        ResultSet rsSpecs = statement.executeQuery(query1);
                        while (rsSpecs.next()) {
                            idSpecs = rsSpecs.getInt("id_specs");
                        }

                        String query2 = "SELECT id_marca_modelo from Veiculos where matricula= '"+ matricula + "'";
                        ResultSet rsMM = statement.executeQuery(query2);
                        while (rsMM.next()) {
                            idMM = rsMM.getInt("id_marca_modelo");
                        }
                        String query3 = "SELECT id_revisao from Veiculos where matricula= '"+ matricula + "'";
                        ResultSet rsRevisao = statement.executeQuery(query3);
                        while (rsRevisao.next()) {
                            idRevisao = rsRevisao.getInt("id_revisao");
                        }

                        String query = "exec RemoverVeiculo @matricula='" + matricula + "', @idSpecs = "+idSpecs+",@idRevisao = "+idRevisao+",@idMM="+idMM+" ;";
                        statement.executeUpdate(query);

                    } catch(SQLException e){
                        e.printStackTrace();
                    }
                    tableVeiculos.getItems().remove(selectedItem);
                }
            }

        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO 404.5");
            alert.setHeaderText("Erro na seleção");
            alert.setContentText("Não foi selecionado nenhum cliente");
            alert.showAndWait();
        }
    }

    /**
     * Abre uma nova janela para visualizar as informações de um veículo específico.
     *
     * @param matricula A matrícula do veículo.
     */
    @FXML
    void verVeiculos(String matricula) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/info_Veiculo.fxml"));
            Parent root = loader.load();
            infoVeiculoController targetController = loader.getController();

            targetController.setReceivedID(matricula);

            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle("Perfil Veiculo");
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtém a lista de veículos da base de dados.
     *
     * @return A lista de veículos.
     */
    public List<Veiculo> getVeiculo() {
        //List<Veiculo> veiculo = new ArrayList<>();
        String query = "SELECT * FROM ListarVeiculo";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String matricula = resultSet.getString("matricula");
                String marca = resultSet.getString("marca");
                String modelo = resultSet.getString("modelo");
                int cilindrada = resultSet.getInt("cilindrada");
                int cv = resultSet.getInt("cavalos");
                String combustivel = resultSet.getString("combustivel");
                int kms = resultSet.getInt("kms");
                LocalDate prox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                LocalDate ult_inspecao = resultSet.getDate("ult_inspecao").toLocalDate();
                String limpeza = resultSet.getString("stat");
                String categoria = resultSet.getString("categoria");
                int id_manutencao = resultSet.getInt("id_manutencao");
                String disponibilizado = resultSet.getString("disponibilidade");
                String seguro = resultSet.getString("nome");
                String Imagem = resultSet.getString("Imagem");
                int valor = resultSet.getInt("valor_dia");

                Veiculo veiculo1 = new Veiculo(Imagem, valor, matricula, marca, modelo, cilindrada, cv, combustivel, kms, prox_revisao, ult_inspecao, limpeza, categoria, id_manutencao, disponibilizado, seguro);
                observableVeiculoList = FXCollections.observableList(veiculoList);
                veiculoList.add(veiculo1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculoList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Veiculo.veiculoList.removeAll(veiculoList);
        butRetirarVeiculos.setDisable(true);
        tablePopulate();
        selectRow();
        tableVeiculos.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableVeiculos.setItems(observableVeiculoList);
    }

    /**
     * Popula a tabela de veículos com os dados da lista de veículos.
     */
    public void tablePopulate() {
        List<Veiculo> veiculoList = getVeiculo();

        c_matricula.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("matricula"));
        c_marca.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("marca"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("modelo"));
        c_cilindrada.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cilindrada"));
        c_cv.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cv"));
        c_combustivel.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("combustivel"));
        c_kms.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("kms"));
        c_revisao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("prox_revisao"));
        c_inspecao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("ult_inspecao"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("limpeza"));
        c_categoria.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("categoria"));
        c_manutencao.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("id_manutencao"));
        c_disponibilidade.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("disponibilizado"));
        c_seguro.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("seguro"));

        tableVeiculos.setItems(FXCollections.observableList(veiculoList));
    }

    /**
     * Seleciona uma linha na tabela de veículos.
     */
    private void selectRow() {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem aluguerMenu = new MenuItem("Aluguer");

        aluguerMenu.setOnAction(event -> {
            Veiculo selectedVeiculo = tableVeiculos.getSelectionModel().getSelectedItem();
            if (selectedVeiculo != null) {
                // Perform delete operation
                System.out.println("Selecionado aluguer btn:" + selectedVeiculo.getMatricula());
                String idCliente = selectedVeiculo.getMatricula();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/editar_veiculo_info.fxml"));
                    Parent root = loader.load();
                    Editar_VeiculoController targetController = loader.getController();

                    targetController.setReceivedId(idCliente, targetController);
                    Scene scene = new Scene(root);

                    // Configurar o palco (janela)
                    Stage novaJanela = new Stage();
                    novaJanela.setResizable(false);
                    novaJanela.setTitle("Editar Veiculo");
                    novaJanela.setScene(scene);

                    // Exibir a nova janela
                    novaJanela.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        contextMenu.getItems().addAll(aluguerMenu);

        // Attach the context menu to the TableView
        tableVeiculos.setContextMenu(contextMenu);
        tableVeiculos.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Veiculo selectedVeiculo = tableVeiculos.getSelectionModel().getSelectedItem();
                if (selectedVeiculo != null) {
                    String matricula = selectedVeiculo.getMatricula();

                    System.out.println("Double-clicked: " + matricula);
                    verVeiculos(matricula);

                }
            }
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                Veiculo selectedVeiculo = tableVeiculos.getSelectionModel().getSelectedItem();
                if (selectedVeiculo != null) {
                    String matricula = selectedVeiculo.getMatricula();
                    butRetirarVeiculos.setDisable(false);
                }
            }
            if (event.getButton().toString().equals("SECONDARY")) {
                contextMenu.show(tableVeiculos, event.getScreenX(), event.getScreenY());
            }
        });

    }

}

