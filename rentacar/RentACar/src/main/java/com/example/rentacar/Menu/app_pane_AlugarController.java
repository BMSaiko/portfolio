package com.example.rentacar.Menu;

import com.example.rentacar.Aluguer.Aluguer;
import com.example.rentacar.Aluguer.Edit_AluguerController;
import com.example.rentacar.Aluguer.infoOrcamentoController;
import com.example.rentacar.Cliente.Editar_ClienteController;
import com.example.rentacar.Cliente.infoClienteController;
import com.example.rentacar.Database.DatabaseConnection;
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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class app_pane_AlugarController implements Initializable {

    @FXML
    private Button butAdicioanarAluguer;

    @FXML
    private Button butEditarAluguer;

    @FXML
    private Button butProcurarAluguer;

    @FXML
    private Button butRetirarAluguer;

    @FXML
    private Button butVerAluguer;

    @FXML
    private TableColumn<Aluguer, Integer> c_NCarros;

    @FXML
    private TableColumn<Aluguer, String> c_cliente_nome;

    @FXML
    private TableColumn<Aluguer, LocalDate> c_data_fim;

    @FXML
    private TableColumn<Aluguer, LocalDate> c_data_inic;

    @FXML
    private TableColumn<Aluguer, String> c_estado;

    @FXML
    private TableColumn<Aluguer, String> c_estado_orcamento;

    @FXML
    private TableColumn<Aluguer, String> c_funcionario_nome;

    @FXML
    private TableColumn<Aluguer, Integer> c_totalvalor;

    @FXML
    private TableView<Aluguer> tableAluguer;

    @FXML
    private TextField txtAluguer;

    ObservableList<Aluguer> observableAluguerList = FXCollections.observableArrayList();

    /**

     Opens the "Criar Aluguer" screen.
     @param event The action event triggered by clicking the "Criar Aluguer" button.
     */
    @FXML
    void criarAluguer(ActionEvent event) {
        Criarcena("/com/example/rentacar/criar_alugar.fxml", "Criar Aluguer");
    }

    /**

     Executes a search for aluguers based on the entered text in the search field.
     */
    @FXML
    void textboxPress() {
        procurarAluguer();
    }

    /**

     Executes the search for aluguers based on the entered text in the search field.
     */
    @FXML
    void procurarAluguer() {
        tableAluguer.setItems(observableAluguerList);

        FilteredList<Aluguer> filteredList = new FilteredList<>(observableAluguerList, b -> true);
        txtAluguer.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(alugueres -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return alugueres.getNome_cliente().contains(palavra)
                        || alugueres.getNome_funcionario().toLowerCase().contains(palavra)
                        || String.valueOf(alugueres.getN_carros()).toLowerCase().contains(palavra)
                        || String.valueOf(alugueres.getData_inic()).toLowerCase().contains(palavra)
                        || String.valueOf(alugueres.getData_fim()).toLowerCase().contains(palavra)
                        || alugueres.getEstado().toLowerCase().contains(palavra)
                        || alugueres.getEstado_orcamento().toLowerCase().contains(palavra);
            });

        });
        SortedList<Aluguer> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableAluguer.comparatorProperty());
        tableAluguer.setItems(sortedData);
    }

    /**

     Opens the "Editar Aluguer" screen for the selected aluguer.
     @param event The action event triggered by clicking the "Editar Aluguer" button.
     */
    @FXML
    void EditarAluguer(ActionEvent event) {
        Aluguer selectedItem = tableAluguer.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Criarcena("/com/example/rentacar/editar_aluguer.fxml", "Editar Aluguer");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERRO 404.5");
            alert.setHeaderText("Erro na seleção");
            alert.setContentText("Não foi selecionado nenhum aluguer");
            alert.showAndWait();
        }
    }

    /**

     Opens the "Informação Orçamento" screen for the specified aluguer ID.

     @param id The ID of the aluguer to display information for.
     */
    @FXML
    void VerAluguer(int id) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/info_Aluguer.fxml"));
            Parent root = loader.load();
            infoOrcamentoController targetController = loader.getController();

            targetController.setReceivedID(id,targetController);
            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle("Informação Orçamento");
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**

     Remove um aluguer selecionado da tabela de aluguer.
     @param event O evento de ação acionado ao clicar no botão "Retirar Aluguer".
     */
    @FXML
    void RetirarAluguer(ActionEvent event) {
        Aluguer selectedItem = tableAluguer.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int id = selectedItem.getId();
            String query = "DELETE FROM Aluguer WHERE id=" + id + ";";
            try {
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();
                statement.executeUpdate(query);
                System.out.println("Aluguer removido com sucesso.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            tableAluguer.getItems().remove(selectedItem);
        }
    }

    /**

     Obtém uma lista de alugueres da base de dados.

     @return A lista de alugueres obtida da base de dados.
     */
    public List<Aluguer> getAluguer() {
        List<Aluguer> aluguerList = new ArrayList<>();
        String query = "SELECT * FROM ConsultarAluguer";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("oid_orcamento");
                String cliente_nome = resultSet.getString("Cliente_nome");
                String funcionario_nome = resultSet.getString("Funcionario_nome");
                int n_carros = resultSet.getInt("n_carros");
                LocalDate data_inic = resultSet.getDate("data_inic").toLocalDate();
                LocalDate data_final = resultSet.getDate("data_fim").toLocalDate();
                String estado_orcamento = resultSet.getString("estado_orcamento");
                String estado = resultSet.getString("estado");
                int valor_total = resultSet.getInt("v_orcamento");
                String matricula = resultSet.getString("matricula");

                Aluguer aluguer1 = new Aluguer(id, valor_total, estado_orcamento, cliente_nome, funcionario_nome, matricula, data_inic, data_final, n_carros, estado);
                aluguerList.add(aluguer1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluguerList;
    }

    /**

     Preenche a tabela de aluguer com os dados obtidos da base de dados.
     */
    private void tablePopulate() {
        List<Aluguer> aluguerList = getAluguer();
        observableAluguerList.setAll(aluguerList);

        c_data_inic.setCellValueFactory(new PropertyValueFactory<>("data_inic"));
        c_data_fim.setCellValueFactory(new PropertyValueFactory<>("data_fim"));
        c_estado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        c_totalvalor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        c_estado_orcamento.setCellValueFactory(new PropertyValueFactory<>("estado_orcamento"));
        c_cliente_nome.setCellValueFactory(new PropertyValueFactory<>("nome_cliente"));
        c_funcionario_nome.setCellValueFactory(new PropertyValueFactory<>("nome_funcionario"));
        c_NCarros.setCellValueFactory(new PropertyValueFactory<>("n_carros"));

        tableAluguer.setItems(observableAluguerList);
    }

    /**

     Inicializa a tabela de aluguer, preenchendo-a com os dados da base de dados.
     @param location A localização usada para resolver caminhos relativos para o objeto raiz, ou null se a localização não for conhecida.
     @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablePopulate();
        selectRow();
        tableAluguer.setPlaceholder(new Label("Não foram encontrados resultados."));
    }

    /**

     Cria uma nova cena com o arquivo FXML especificado e exibe-a em uma nova janela.

     @param arquivofxml O caminho para o arquivo FXML da cena.

     @param mensagem A mensagem para exibir como título da janela.
     */
    private void Criarcena(String arquivofxml, String mensagem) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivofxml));
        Parent root = null;
        try {
            root = loader.load();
            if (arquivofxml == "/com/example/rentacar/editar_aluguer.fxml") {
                Edit_AluguerController targetController = loader.getController();
                Aluguer selectedAluguer = tableAluguer.getSelectionModel().getSelectedItem();
                if (selectedAluguer != null) {
                    int id = selectedAluguer.getId();
                    System.out.println("Double-clicked: " + id);
                    targetController.setReceiveid(id);
                }
            }
        }catch(IOException e ){
            e.printStackTrace();
        }
        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle(mensagem);
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**

     Seleciona uma linha da tabela de aluguer quando ocorrer um duplo clique.
     */
    private void selectRow() {
        tableAluguer.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Aluguer selectedAluguer = tableAluguer.getSelectionModel().getSelectedItem();
                if (selectedAluguer != null) {
                    int id = selectedAluguer.getId();
                    System.out.println("Double-clicked: " + id);
                    VerAluguer(id);
                }
            }
        });
    }
}
