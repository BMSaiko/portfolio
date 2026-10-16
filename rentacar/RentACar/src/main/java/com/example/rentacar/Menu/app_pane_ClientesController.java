package com.example.rentacar.Menu;

import com.example.rentacar.Aluguer.Criar_AluguerController;
import com.example.rentacar.Cliente.Clientes;
import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Cliente.infoClienteController;
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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class app_pane_ClientesController implements Initializable {


    @FXML
    private Button butAdicioanarClientes;

    @FXML
    private Button butEditarClientes;

    @FXML
    private Button butProcurarClientes;

    @FXML
    private Button butRetirarClientes;

    @FXML
    private Button butVerClientes;

    @FXML
    private TableView<Clientes> tableClientes;

    @FXML
    private TableColumn<Clientes, Integer> c_NIF;

    @FXML
    private TableColumn<Clientes, Date> c_datacliente;

    @FXML
    private TableColumn<Clientes, String> c_email;

    @FXML
    private TableColumn<Clientes, String> c_morada;

    @FXML
    private TableColumn<Clientes, Date> c_nascimento;

    @FXML
    private TableColumn<Clientes, String> c_nome;

    @FXML
    private TableColumn<Clientes, Integer> c_telefone;

    @FXML
    private TextField txtClientes;

    ArrayList<Clientes> clienteList = Clientes.clientesList;
    ObservableList<Clientes> observableClienteList;


    /**

     Cria uma nova cena para criar um novo cliente e exibe-a em uma nova janela.
     */
    @FXML
    void criarCliente(ActionEvent event) {
        Criarcena("/com/example/rentacar/criar_cliente_info.fxml","Criar Cliente");
    }

    /**

     Realiza uma busca na tabela de clientes com base no texto inserido no campo de pesquisa.
     */
    @FXML
    void textboxPress(){
        procurarCliente();
    }

    /**

     Remove o cliente selecionado da tabela de clientes.
     */
    @FXML
    void procurarCliente() {
        tableClientes.setItems(observableClienteList);

        FilteredList<Clientes> filteredList =  new FilteredList<>(observableClienteList, b -> true);
        txtClientes.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(clientes -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return clientes.getNome().toLowerCase().contains(palavra)
                        || clientes.getEmail().toLowerCase().contains(palavra)
                        || String.valueOf(clientes.getTelefone()).toLowerCase().contains(palavra)
                        || String.valueOf(clientes.getNIF()).toLowerCase().contains(palavra)
                        || String.valueOf(clientes.getDataCliente()).toLowerCase().contains(palavra)
                        || String.valueOf(clientes.getNascimento()).toLowerCase().contains(palavra)
                        ||  clientes.getMorada().toLowerCase().contains(palavra);
            });
        });
        SortedList<Clientes> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableClientes.comparatorProperty());
        tableClientes.setItems(sortedData);
    }

    /**

     Remove o cliente selecionado da tabela de clientes.
     */
    @FXML
    void retirarCliente(ActionEvent event) {
        removerCliente();
    }

    /**

     Abre uma nova janela exibindo as informações detalhadas do cliente selecionado.

     @param id O ID do cliente selecionado.
     */
    @FXML
    void verClientes(int id) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/info_Cliente.fxml"));
            Parent root = loader.load();
            infoClienteController targetController = loader.getController();

            targetController.setReceivedId(id,targetController);
            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle("Perfil Cliente");
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**

     Remove o cliente selecionado da tabela de clientes e do banco de dados.
     */
    private void removerCliente (){
        Clientes selectedItem = tableClientes.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int id = selectedItem.getId();
            int telefone = selectedItem.getTelefone();
            String query = "exec RemoverCliente @id=" + id + ", @telefone = "+telefone+" ;";
            try{
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();
                statement.executeUpdate(query);
            }catch(SQLException e){
                e.printStackTrace();
            }
            tableClientes.getItems().remove(selectedItem);
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

     Obtém uma lista de clientes a partir da base de dados.

     @return A lista de clientes.
     */
    public List<Clientes> getCliente(){
        String query = "SELECT * FROM ListarCliente";

        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String nome = resultSet.getString("nome");
                int telefone = resultSet.getInt("telefone");
                int NIF = resultSet.getInt("NIF");
                LocalDate dataCliente = resultSet.getDate("data_cliente").toLocalDate();
                LocalDate nascimento = resultSet.getDate("nascimento").toLocalDate();
                String morada = resultSet.getString("morada");
                int CConducao = resultSet.getInt("cconducao");
                int CC = resultSet.getInt("cc");

                Clientes cliente1 = new Clientes(id, email, nome, telefone, NIF, dataCliente, nascimento, morada,CConducao,CC);
                observableClienteList = FXCollections.observableList(clienteList);
                clienteList.add(cliente1);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return clienteList;
    }

    /**

     Método chamado durante a inicialização do controlador.
     Remove todos os elementos da lista de clientes e preenche a tabela de clientes com os dados obtidos da base de dados.
     Configura os eventos de seleção de linha na tabela de clientes.
     Define o texto de placeholder da tabela de clientes quando não há resultados.
     Define a lista observável de clientes como a fonte de dados da tabela.
     @param location A localização do arquivo FXML.
     @param resources Os recursos utilizados.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Clientes.clientesList.removeAll(clienteList);
        tablePopulate();
        selectRow();
        tableClientes.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableClientes.setItems(observableClienteList);

    }

    /**

     Preenche a tabela de clientes com os dados da lista de clientes.
     */
    public void tablePopulate(){
        List<Clientes> clienteList = getCliente();

        c_nome.setCellValueFactory(new PropertyValueFactory<Clientes, String>("nome"));
        c_email.setCellValueFactory(new PropertyValueFactory<Clientes, String>("email"));
        c_telefone.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("telefone"));
        c_NIF.setCellValueFactory(new PropertyValueFactory<Clientes, Integer>("NIF"));
        c_datacliente.setCellValueFactory(new PropertyValueFactory<Clientes, Date>("dataCliente"));
        c_nascimento.setCellValueFactory(new PropertyValueFactory<Clientes, Date>("nascimento"));
        c_morada.setCellValueFactory(new PropertyValueFactory<Clientes, String>("morada"));

        tableClientes.setItems(FXCollections.observableList(clienteList));
    }

    /**

     Cria uma nova cena a partir de um arquivo FXML e exibe-a em uma nova janela.

     @param arquivofxml O caminho para o arquivo FXML da cena.

     @param Mensagem O título da janela.
     */
    private void Criarcena(String arquivofxml,String Mensagem){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(arquivofxml));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle(Mensagem);
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**

     Configura o evento de seleção de linha na tabela de clientes.

     Cria um menu de contexto com a opção "Aluguer" que permite abrir a janela de criação de aluguer para o cliente selecionado.

     Quando um cliente é selecionado na tabela, é exibida a opção de aluguer no menu de contexto.

     Ao clicar duas vezes em um cliente, é exibida a informação detalhada do cliente em uma nova janela.

     O evento de clique com o botão direito do mouse também exibe o menu de contexto na posição do clique.
     */
    private void selectRow(){

        ContextMenu contextMenu = new ContextMenu();
        MenuItem aluguerMenu = new MenuItem("Aluguer");

        aluguerMenu.setOnAction(event -> {
            Clientes selectedPerson = tableClientes.getSelectionModel().getSelectedItem();
            if (selectedPerson != null) {
                // Perform delete operation
                System.out.println("Selecionado aluguer btn:" + selectedPerson.getNome());
                int idCliente = selectedPerson.getId();

                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/criar_alugar.fxml"));
                    Parent root = loader.load();
                    Criar_AluguerController targetController = loader.getController();

                    targetController.setReceivedId(idCliente,targetController);
                    Scene scene = new Scene(root);

                    // Configurar o palco (janela)
                    Stage novaJanela = new Stage();
                    novaJanela.setResizable(false);
                    novaJanela.setTitle("Perfil Veiculo");
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
        tableClientes.setContextMenu(contextMenu);

        tableClientes.setOnMouseClicked(event ->{
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Clientes selectedCliente = tableClientes.getSelectionModel().getSelectedItem();
                if (selectedCliente != null) {
                    int id = selectedCliente.getId();

                    System.out.println("Double-clicked: " + id);
                    verClientes(id);

                }
            }
            if (event.getButton().toString().equals("SECONDARY")) {
                contextMenu.show(tableClientes, event.getScreenX(), event.getScreenY());
            }
        });

    }

}