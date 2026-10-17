    package com.example.sheetsandpicks.Controllers.Users;

    import com.example.sheetsandpicks.BL.Database.DBstatements;
    import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_updateutilizadoresController;
    import com.example.sheetsandpicks.Controllers.app_dashboard.app_dash_utilizadoresController;
    import com.example.sheetsandpicks.Models.Suppliers;
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

    public class SuppliersController {

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
    private TextField pesquisarfornecedores;

    @FXML
    private TableView<Suppliers> tableView;

    @FXML
    private TableColumn<Suppliers, String> nomeColumn;

        @FXML
        private TableColumn<Suppliers, String> emailColumn;

        @FXML
        private TableColumn<Suppliers, String> enderecoColumn;

        @FXML
        private TableColumn<Suppliers, String> telefoneColumn;

        @FXML
        private TableColumn<Suppliers, String> cod_postalColumn;

        ObservableList<Suppliers> observableSuppliersList = FXCollections.observableArrayList();

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
         * Inicializa a tabela de fornecedores, configura as colunas da tabela, busca os fornecedores da base de dados,
         * e popula a tabela com os dados dos fornecedores. Também configura eventos de clique na tabela.
         */
    @FXML
    public void initialize() {

        // Inicializar a tabela
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        cod_postalColumn.setCellValueFactory(new PropertyValueFactory<>("cod_postal"));
        enderecoColumn.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        telefoneColumn.setCellValueFactory(new PropertyValueFactory<>("Telefone"));

        List<Suppliers> fornecedores = DBstatements.buscarFornecedores();

        // Verificar se a lista de fornecedores não está vazia antes de adicionar à TableView
            if (fornecedores == null) {
                fornecedores = DBstatements.buscarFornecedores();

                if (fornecedores != null) {
                    tableView.setItems(FXCollections.observableArrayList(fornecedores));
                }       else {
                    System.out.println("Nenhum fornecedor encontrado ou ocorreu um erro ao buscar os fornecedores.");
                }
            } else {
                // Se os dados já foram buscados anteriormente, apenas atualize a tabela
                tableView.setItems(FXCollections.observableArrayList(fornecedores));
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
         * Manipula o evento de alteração de fornecedor.
         * Obtém os detalhes do fornecedor selecionado na tabela, incluindo o ID do fornecedor, e chama o método para abrir
         * a janela de atualização de fornecedores com esses detalhes.
         *
         * @param event O evento de ação associado ao clique no botão de alteração de fornecedor.
         * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter dados do fornecedor.
         */
    @FXML
    void alterar_uti(ActionEvent event) throws SQLException {
        Suppliers suppliers = tableView.getSelectionModel().getSelectedItem();

        Integer id = suppliers.getId();
        String pais = String.valueOf(DBstatements.getPaisid(id));
        String nome = suppliers.getNome();
        String endereco = suppliers.getEndereco();
        String endereco2 = suppliers.getEndereco2();
        String cidade = suppliers.getCidade();
        Integer telefone = Integer.valueOf(suppliers.getTelefone());
        String email = suppliers.getEmail();
        String cod_postal = suppliers.getCod_postal();
        Date data_nascimento = suppliers.getData_nascimento();

        openAppDashUpdateUtilizadores(labelFuncionario.getText(),id,nome,endereco,pais ,endereco2,cidade,telefone,email,cod_postal, String.valueOf(data_nascimento));
    }

        /**
         * Abre a janela de atualização de utilizadores/fornecedores.
         *
         * @param text        O texto associado ao tipo de funcionário (Admin, Fornecedor, Operador).
         * @param id          O ID do utilizador/fornecedor a ser atualizado.
         * @param nome        O nome do utilizador/fornecedor.
         * @param endereco    O endereço do utilizador/fornecedor.
         * @param pais        O país do utilizador/fornecedor.
         * @param endereco2   O segundo endereço do utilizador/fornecedor.
         * @param cidade      A cidade do utilizador/fornecedor.
         * @param telefone    O número de telefone do utilizador/fornecedor.
         * @param email       O endereço de e-mail do utilizador/fornecedor.
         * @param cod_postal  O código postal do utilizador/fornecedor.
         * @param data        A data de nascimento do utilizador/fornecedor no formato de string.
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
         * Manipula o evento de criação de um novo utilizador/fornecedor.
         * Chama o método para abrir a janela de criação de utilizadores/fornecedores, passando o tipo de funcionário associado.
         *
         * @param event O evento de ação associado ao clique no botão de criação de utilizadores/fornecedores.
         */
    @FXML
    void criar_uti(ActionEvent event) {
        openAppDashUtilizadores(labelFuncionario.getText());
    }

        /**
         * Manipula o evento de remoção de um utilizador/fornecedor.
         * Obtém o fornecedor selecionado na tabela, extrai o ID do fornecedor e chama o método para exibir uma pergunta de confirmação.
         *
         * @param event O evento de ação associado ao clique no botão de remoção de utilizador/fornecedor.
         * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao obter o ID do fornecedor.
         */
    @FXML
    void remover_uti(ActionEvent event) throws SQLException {
            Suppliers suppliers = tableView.getSelectionModel().getSelectedItem();
            Integer id = suppliers.getId();
            HandlerPergunta(id);
    }

        /**
         * Manipula o evento de pergunta de confirmação para remover um fornecedor.
         * Exibe um diálogo de confirmação perguntando se o utilizador deseja remover o fornecedor com base no ID fornecido.
         * Se o utilizador confirmar, chama o método para remover o fornecedor da base de dados e exibe uma mensagem de confirmação.
         *
         * @param id O ID do fornecedor a ser removido.
         * @throws SQLException Se ocorrer uma exceção durante a execução de operações SQL ao remover o fornecedor.
         */
        private void HandlerPergunta(Integer id) throws SQLException {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setHeaderText("Tem a certeza?");
            a.setContentText("QUER REMOVER o FORNECEDOR?");
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
         * Manipula o evento de confirmação bem-sucedida após a remoção de um fornecedor.
         * Exibe um diálogo de informação indicando que o fornecedor foi removido com sucesso.
         */
        public void HandlerConfirmado() {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("SUCESSO");
            a.setContentText("O FORNECEDOR foi removido com sucesso");
            Optional<ButtonType> option = a.showAndWait();
            if (option.isPresent()) {
                if (option.get() == ButtonType.OK) {

                }
            }
        }

        /**
         * Configura um evento de tecla para a tabela de fornecedores.
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
         * Configura a funcionalidade de pesquisa dinâmica na tabela de fornecedores.
         * Filtra os fornecedores com base no texto inserido na caixa de pesquisa, considerando várias colunas.
         * Os resultados filtrados são exibidos na tabela.
         */
        public void Enter(){
            observableSuppliersList = tableView.getItems();

            FilteredList<Suppliers> filteredList = new FilteredList<>(observableSuppliersList, b -> true);
            pesquisarfornecedores.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(suppliers -> {
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }
                    String palavra = newValue.toLowerCase();

                    return suppliers.getCod_postal().toLowerCase().contains(palavra)
                            || suppliers.getNome().toLowerCase().contains(palavra)
                            || String.valueOf(suppliers.getEndereco()).toLowerCase().contains(palavra)
                            || String.valueOf(suppliers.getEndereco2()).toLowerCase().contains(palavra)
                            || String.valueOf(suppliers.getCidade()).toLowerCase().contains(palavra)
                            || suppliers.getTelefone().toString().toLowerCase().contains(palavra)
                            || suppliers.getEmail().toLowerCase().contains(palavra)
                            || suppliers.getData_nascimento().toString().toLowerCase().contains(palavra);
                });

            });
            SortedList<Suppliers> sortedData = new SortedList<>(filteredList);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
        }
}
