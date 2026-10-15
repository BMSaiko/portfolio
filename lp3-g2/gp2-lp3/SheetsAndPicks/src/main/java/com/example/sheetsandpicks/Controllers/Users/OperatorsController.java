package com.example.sheetsandpicks.Controllers.Users;

import com.example.sheetsandpicks.BL.Database.DBstatements;
import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_updateutilizadoresController;
import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_utilizadoresController;
import com.example.sheetsandpicks.Models.Operators;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OperatorsController {

    @FXML
    private Button butAdicionar;

    @FXML
    private Button butAlterar;

    @FXML
    private Button butRemover;

    @FXML
    private Label labelFuncionario;

    @FXML
    private String tipo;

    @FXML
    private TableView<Operators> tableView;

    @FXML
    private TextField pesquisaroperadores;

    @FXML
    private TableColumn<Operators, String> nomeColumn;

    @FXML
    private TableColumn<Operators, String> emailColumn;

    @FXML
    private TableColumn<Operators, String> enderecoColumn;

    @FXML
    private TableColumn<Operators, String> telefoneColumn;

    @FXML
    private TableColumn<Operators, String> cod_postalColumn;
    ObservableList<Operators> observableOperadorList = FXCollections.observableArrayList();

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
     * Manipula o evento de pressionar a tecla Enter na caixa de texto.
     * Chama o método `Enter` para realizar a ação associada à tecla Enter neste contexto.
     */
    @FXML
    void textboxPress() {
        Enter();
    }

    /**
     * Método chamado automaticamente durante a inicialização do controlador FXML.
     * Inicializa a tabela de Operadores, configura as colunas da tabela, busca os Operadores da base de dados,
     * e popula a tabela com os dados dos Operadores. Também configura eventos de clique na tabela.
     */
    @FXML
    public void initialize() {

        // Inicializar a tabela
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        cod_postalColumn.setCellValueFactory(new PropertyValueFactory<>("cod_postal"));
        enderecoColumn.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        telefoneColumn.setCellValueFactory(new PropertyValueFactory<>("Telefone"));

        List<Operators> operadores = DBstatements.buscarOperadores();
        if (operadores == null) {
            operadores = DBstatements.buscarOperadores();

            if (operadores != null) {
                tableView.setItems(FXCollections.observableArrayList(operadores));

            }       else {
                System.out.println("Nenhum Operador encontrado ou ocorreu um erro ao buscar os Operadores.");
            }
        } else {
            // Se os dados já foram buscados anteriormente, apenas atualize a tabela
            tableView.setItems(FXCollections.observableArrayList(operadores));
        }
        selectrow();
    }


    /**
     * Abre a janela do painel de controle de utilizadores.
     *
     * @param userType O tipo de utilizador para o qual o painel de controle será aberto.
     *                 Pode ser "Admin", "Fornecedor", "Operador", ou qualquer outro valor desejado.
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
     * Manipula o evento de alteração de Operador.
     * Obtém os detalhes do Operador selecionado na tabela, incluindo o ID do Operador, e chama o método para abrir
     * a janela de atualização de Operadores com esses detalhes.
     *
     * @param event O evento de ação associado ao clique no botão de alteração de Operador.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter dados do Operador.
     */
    @FXML
    void alterar_uti(ActionEvent event) throws SQLException {
        Operators operators = tableView.getSelectionModel().getSelectedItem();

        Integer id = operators.getId();
        String pais = String.valueOf(DBstatements.getPaisid(id));
        String nome = operators.getNome();
        String endereco = operators.getEndereco();
        String endereco2 = operators.getEndereco2();
        String cidade = operators.getCidade();
        Integer telefone = Integer.valueOf(operators.getTelefone());
        String email = operators.getEmail();
        String cod_postal = operators.getCod_postal();
        Date data_nascimento = operators.getData_nascimento();

        openAppDashUpdateUtilizadores(labelFuncionario.getText(),id,nome,endereco,pais ,endereco2,cidade,telefone,email,cod_postal, String.valueOf(data_nascimento));
    }

    /**
     * Abre a janela de atualização de utilizadores/Operadores.
     *
     * @param text        O texto associado ao tipo de funcionário (Admin, Fornecedor, Operador).
     * @param id          O ID do utilizador/Operador a ser atualizado.
     * @param nome        O nome do utilizador/Operador.
     * @param endereco    O endereço do utilizador/Operador.
     * @param pais        O país do utilizador/Operador.
     * @param endereco2   O segundo endereço do utilizador/Operador.
     * @param cidade      A cidade do utilizador/Operador.
     * @param telefone    O número de telefone do utilizador/Operador.
     * @param email       O endereço de e-mail do utilizador/Operador.
     * @param cod_postal  O código postal do utilizador/Operador.
     * @param data        A data de nascimento do utilizador/Operador no formato de string.
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
     * Manipula o evento de criação de um novo utilizador/operador.
     * Chama o método para abrir a janela de criação de utilizadores/operador, passando o tipo de funcionário associado.
     *
     * @param event O evento de ação associado ao clique no botão de criação de utilizadores/Operador.
     */
    @FXML
    void criar_uti(ActionEvent event) {
        openAppDashUtilizadores(labelFuncionario.getText());
    }

    /**
     * Manipula o evento de remoção de um utilizador/operador.
     * Obtém o operador selecionado na tabela, extrai o ID do operador e chama o método para exibir uma pergunta de confirmação.
     *
     * @param event O evento de ação associado ao clique no botão de remoção de utilizador/operador.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter o ID do operador.
     */
    @FXML
    void remover_uti(ActionEvent event) throws SQLException {

        Operators operators = tableView.getSelectionModel().getSelectedItem();
        Integer id = operators.getId();
        HandlerPergunta(id);

    }

    /**
     * Manipula o evento de pergunta de confirmação para remover um operador.
     * Exibe um diálogo de confirmação perguntando se o utilizador deseja remover o Operador com base no ID fornecido.
     * Se o utilizador confirmar, chama o método para remover o Operador da base de dados e exibe uma mensagem de confirmação.
     *
     * @param id O ID do Operador a ser removido.
     * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao remover o operador.
     */
    private void HandlerPergunta(Integer id) throws SQLException {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Tem a certeza?");
        a.setContentText("QUER REMOVER o OPERADOR?");
        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                DBstatements.Eliminar_Funcionario(id);
                HandlerConfirmado();
            } else {

            }
        }
    }

    /**
     * Manipula o evento de confirmação bem-sucedida após a remoção de um Operador.
     * Exibe um diálogo de informação indicando que o Operador foi removido com sucesso.
     */
    public void HandlerConfirmado() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("SUCESSO");
        a.setContentText("O OPERADOR foi removido com sucesso");
        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {

            }
        }
    }

    /**
     * Configura um evento de tecla para a tabela de Operadores.
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
     * Configura a funcionalidade de pesquisa dinâmica na tabela de Operadores.
     * Filtra os Operadores com base no texto inserido na caixa de pesquisa, considerando várias colunas.
     * Os resultados filtrados são exibidos na tabela.
     */
    public void Enter(){
        observableOperadorList = tableView.getItems();

        FilteredList<Operators> filteredList = new FilteredList<>(observableOperadorList, b -> true);
        pesquisaroperadores.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(operators -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return operators.getCod_postal().toLowerCase().contains(palavra)
                        || operators.getNome().toLowerCase().contains(palavra)
                        || String.valueOf(operators.getEndereco()).toLowerCase().contains(palavra)
                        || String.valueOf(operators.getEndereco2()).toLowerCase().contains(palavra)
                        || String.valueOf(operators.getCidade()).toLowerCase().contains(palavra)
                        || operators.getTelefone().toString().toLowerCase().contains(palavra)
                        || operators.getEmail().toLowerCase().contains(palavra)
                        || operators.getData_nascimento().toString().toLowerCase().contains(palavra);
            });

        });
        SortedList<Operators> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
}
