package com.example.sheetsandpicks.Controllers.Users;

import com.example.sheetsandpicks.BL.Database.DBstatements;
import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_updateutilizadoresController;
import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_utilizadoresController;
import com.example.sheetsandpicks.Models.Clients;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientsController {

    @FXML
    private Button butAlterar;

    @FXML
    private Button butCriar;

    @FXML
    private Button butRemover;

    @FXML
    private TableColumn<Clients, String> cidadeColumn;

    @FXML
    private TableColumn<Clients, String> cod_postalColumn;

    @FXML
    private TableColumn<Clients, String> datanascimentoColumn;

    @FXML
    private TableColumn<Clients, String> emailColumn;

    @FXML
    private TableColumn<Clients, String> endereco2Column;

    @FXML
    private TableColumn<Clients, String> enderecoColumn;

    @FXML
    private TableColumn<Clients, String> nomeColumn;

    @FXML
    private TableView<Clients> tableView;

    @FXML
    private TableColumn<Clients, Integer> telefoneColumn;

    @FXML
    private Label labelFuncionario;

    @FXML
    private TextField pesquisarclientes;
    @FXML
    private String tipo;

    ObservableList<Clients> observableClientsList = FXCollections.observableArrayList();

    /**
     * Define o tipo de funcionário associado a este controlador.
     *
     * @param Funcionario O tipo de funcionário a ser associado a este controlador.
     */
    public void setFuncionario(String Funcionario) {
        labelFuncionario.setText(Funcionario);
        tipo = Funcionario;
    }

    /**
     * Método chamado automaticamente durante a inicialização do controlador FXML.
     * Inicializa a tabela de Clientes, configura as colunas da tabela, busca os Clientes da base de dados,
     * e popula a tabela com os dados dos Clientes. Também configura eventos de clique na tabela.
     */
    @FXML
    public void initialize() {
        // Inicializar a tabela
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cod_postalColumn.setCellValueFactory(new PropertyValueFactory<>("cod_postal"));
        cidadeColumn.setCellValueFactory(new PropertyValueFactory<>("cidade"));
        datanascimentoColumn.setCellValueFactory(new PropertyValueFactory<>("data_nascimento"));
        enderecoColumn.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        endereco2Column.setCellValueFactory(new PropertyValueFactory<>("endereco2"));
        telefoneColumn.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        List<Clients> clientes = DBstatements.buscarClientesAtivos();

        if (clientes != null) {
            tableView.setItems(FXCollections.observableArrayList(clientes));
        } else {
            System.out.println("Nenhum cliente encontrado ou ocorreu um erro ao buscar os clientes.");
        }
        selectrow();
    }

    /**
     * Abre a janela do painel de controle de utilizadores.
     *
     * @param userType O tipo de utilizador para o qual o painel de controle será aberto.
     *                 Pode ser "Admin", "Client", "Operador", ou qualquer outro valor desejado.
     *                 Este tipo de utilizador será passado para o controlador do painel de controlo.
     * @throws RuntimeException Se ocorrer uma exceção SQLException durante a execução.
     */
    private void openAppDashUtilizadores(String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sheetsandpicks/app_dash_utilizadores.fxml"));
            Parent root = loader.load();
            app_dash_utilizadoresController controller = loader.getController();
            controller.setFuncionario(userType);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Manipula o evento de alteração de Cliente.
     * Obtém os detalhes do Cliente selecionado na tabela, incluindo o ID do Cliente, e chama o método para abrir
     * a janela de atualização de Clientes com esses detalhes.
     *
     * @param event O evento de ação associado ao clique no botão de alteração de Cliente.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter dados do Cliente.
     */
    @FXML
    void alterar_uti(ActionEvent event) throws IOException, SQLException {
        Clients clients = tableView.getSelectionModel().getSelectedItem();

        Integer id = clients.getId();
        String pais = String.valueOf(DBstatements.getPaisid(id));
        String nome = clients.getNome();
        String endereco = clients.getEndereco();
        String endereco2 = clients.getEndereco2();
        String cidade = clients.getCidade();
        Integer telefone = clients.getTelefone();
        String email = clients.getEmail();
        String cod_postal = clients.getCod_postal();
        String data_nascimento = clients.getData_nascimento();

        openAppDashUpdateUtilizadores(labelFuncionario.getText(),id,nome,endereco,pais ,endereco2,cidade,telefone,email,cod_postal,data_nascimento);
    }

    /**
     * Abre a janela de atualização de utilizadores/Clientes.
     *
     * @param text        O texto associado ao tipo de funcionário (Admin, Fornecedor, Operador).
     * @param id          O ID do utilizador/Cliente a ser atualizado.
     * @param nome        O nome do utilizador/Cliente.
     * @param endereco    O endereço do utilizador/Cliente.
     * @param pais        O país do utilizador/Cliente.
     * @param endereco2   O segundo endereço do utilizador/Cliente.
     * @param cidade      A cidade do utilizador/Cliente.
     * @param telefone    O número de telefone do utilizador/Cliente.
     * @param email       O endereço de e-mail do utilizador/Cliente.
     * @param cod_postal  O código postal do utilizador/Cliente.
     * @param data        A data de nascimento do utilizador/Cliente no formato de string.
     * @throws RuntimeException Se ocorrer uma exceção IOException ou SQLException durante a execução.
     */
    private void openAppDashUpdateUtilizadores(String text, Integer id, String nome, String endereco, String pais,String endereco2, String cidade, Integer telefone,String email, String cod_postal, String data ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sheetsandpicks/app_dash_updateutilizadores.fxml"));
            Parent root = loader.load();
            app_dash_updateutilizadoresController controller = loader.getController();
            controller.setFuncionario(text);
            controller.setdata(id,nome,endereco, pais,endereco2,cidade,telefone,email,cod_postal,data);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Manipula o evento de criação de um novo utilizador/Cliente.
     * Chama o método para abrir a janela de criação de utilizadores/Clientes, passando o tipo de funcionário associado.
     *
     * @param event O evento de ação associado ao clique no botão de criação de utilizadores/Clientes.
     */
    @FXML
    void criar_uti(ActionEvent event) {
        openAppDashUtilizadores(labelFuncionario.getText());
    }

    /**
     * Manipula o evento de remoção de um utilizador/Cliente.
     * Obtém o Cliente selecionado na tabela, extrai o ID do Cliente e chama o método para exibir uma pergunta de confirmação.
     *
     * @param event O evento de ação associado ao clique no botão de remoção de utilizador/Cliente.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter o ID do Cliente.
     */
    @FXML
    void remover_uti(ActionEvent event) throws SQLException {

        Clients clients = tableView.getSelectionModel().getSelectedItem();
        Integer id = clients.getId();
        HandlerPergunta(id);
    }

    /**
     * Manipula o evento de pergunta de confirmação para remover um Cliente.
     * Exibe um diálogo de confirmação perguntando se o utilizador deseja remover o Cliente com base no ID fornecido.
     * Se o utilizador confirmar, chama o método para remover o Cliente da base de dados e exibe uma mensagem de confirmação.
     *
     * @param id O ID do Cliente a ser removido.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao remover o Cliente.
     */
    private void HandlerPergunta(Integer id) throws SQLException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Tem a certeza?");
        a.setContentText("QUER REMOVER o CLIENTE?");
        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                DBstatements.EliminarCliente(id);
                HandlerConfirmado();
            } else {

            }
        }
    }

    /**
     * Manipula o evento de confirmação bem-sucedida após a remoção de um Cliente.
     * Exibe um diálogo de informação indicando que o Cliente foi removido com sucesso.
     */
    public void HandlerConfirmado() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("SUCESSO");
        a.setContentText("O CLIENTE foi removido com sucesso");
        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {

            }
        }
    }

    /**
     * Configura um evento de tecla para a tabela de Clientes.
     * Ativa a recarga da tabela quando a tecla F5 é pressionada.
     * O método `initialize()` é chamado para recarregar os dados da tabela.
     */
    public void selectrow(){
        tableView.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.F5){
                initialize();
            }
        });
    }

    /**
     * Manipula o evento de pressionar a tecla Enter na caixa de texto de pesquisa.
     * Chama o método para iniciar a funcionalidade de pesquisa dinâmica na tabela de Clientes.
     */
    public void textboxPress() {
        Enter();
    }

    /**
     * Configura a funcionalidade de pesquisa dinâmica na tabela de Clientes.
     * Filtra os Clientes com base no texto inserido na caixa de pesquisa, considerando várias colunas.
     * Os resultados filtrados são exibidos na tabela.
     */
    private void Enter() {
        observableClientsList = tableView.getItems();

        FilteredList<Clients> filteredList = new FilteredList<>(observableClientsList, b -> true);
        pesquisarclientes.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(clients -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return clients.getCod_postal().toLowerCase().contains(palavra)
                        || clients.getNome().toLowerCase().contains(palavra)
                        || String.valueOf(clients.getEndereco()).toLowerCase().contains(palavra)
                        || String.valueOf(clients.getEndereco2()).toLowerCase().contains(palavra)
                        || String.valueOf(clients.getCidade()).toLowerCase().contains(palavra)
                        || clients.getTelefone().toString().toLowerCase().contains(palavra)
                        || clients.getEmail().toLowerCase().contains(palavra)
                        || clients.getData_nascimento().toString().toLowerCase().contains(palavra);
            });

        });
        SortedList<Clients> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
}
