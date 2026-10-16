package com.example.rentacar.Menu;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Staff.funcionario;
import com.example.rentacar.Staff.infoFuncionarioController;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class app_pane_staffController implements Initializable {

    @FXML
    private Button butAdicioanarStaff;

    @FXML
    private Button butEditarStaff;

    @FXML
    private Button butProcurarStaff;

    @FXML
    private Button butRetirarStaff;

    @FXML
    private Button butVerStaff;

    @FXML
    private TextField txtStaff;

    @FXML
    private TableView<funcionario> tableFuncionarios;

    @FXML
    private TableColumn<funcionario, Integer> c_NivelAcesso;

    @FXML
    private TableColumn<funcionario, String> c_email;

    @FXML
    private TableColumn<funcionario, String> c_nome;

    @FXML
    private TableColumn<funcionario, Integer> c_telefone;

    ArrayList<funcionario> funcionariosList = funcionario.funcionariosList;
    ObservableList<funcionario> observableFuncionarioList;

    /**

     Método de evento acionado ao pressionar a tecla Enter na caixa de texto.
     Chama o método de procurar funcionários.
     */
    @FXML
    void textboxPress(){
        procurarStaff();
    }

    /**

     Método de evento acionado ao clicar no botão de adicionar funcionário.
     Abre uma nova janela para criar um novo funcionário.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void adicionarStaff(ActionEvent event) {Criarcena("/com/example/rentacar/criar_staff_info.fxml","Funcionario");}

    /**

     Método para procurar funcionários com base no texto inserido na caixa de pesquisa.

     Filtra a lista de funcionários com base no texto fornecido e exibe os resultados na tabela.
     */
    @FXML
    void procurarStaff() {
            tableFuncionarios.setItems(observableFuncionarioList);
            if(observableFuncionarioList != null) {
                FilteredList<funcionario> filteredList = new FilteredList<>(observableFuncionarioList, b -> true);
                txtStaff.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(Funcionario -> {
                        if (newValue.isBlank()) {
                            return true;
                        }
                        String palavra = newValue.toLowerCase();

                        return Funcionario.getNome().toLowerCase().contains(palavra)
                                || Funcionario.getEmail().toLowerCase().contains(palavra)
                                || String.valueOf(Funcionario.getTelefone()).toLowerCase().contains(palavra)
                                || String.valueOf(Funcionario.getNv_acesso()).toLowerCase().contains(palavra)
                                || Funcionario.getPassword().toLowerCase().contains(palavra);
                    });
                });
                SortedList<funcionario> sortedData = new SortedList<>(filteredList);
                sortedData.comparatorProperty().bind(tableFuncionarios.comparatorProperty());
                tableFuncionarios.setItems(sortedData);
            }
    }

    /**

     Método de evento acionado ao clicar no botão de retirar funcionário.
     Remove o funcionário selecionado da tabela e do banco de dados.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void retirarStaff(ActionEvent event) {
        funcionario selectedItem = tableFuncionarios.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int id = selectedItem.getId();
            String query = "DELETE FROM Funcionario WHERE id="+id+";";
            try{
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();
                statement.executeUpdate(query);
            }catch(SQLException e){
                e.printStackTrace();
            }
            tableFuncionarios.getItems().remove(selectedItem);
        }
    }

    /**

     Método para exibir o perfil de um funcionário.

     Abre uma nova janela para exibir as informações detalhadas de um funcionário.

     @param id O ID do funcionário cujo perfil será exibido.

     @throws IOException Se ocorrer um erro ao carregar o arquivo FXML.
     */
    @FXML
    void verStaff(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/info_Funcionario.fxml"));
        Parent root = loader.load();
        infoFuncionarioController targetController = loader.getController();

        targetController.loadLabelFuncionario(id);
        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle("Perfil Funcionario");
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**

     Cria uma nova cena e exibe em uma nova janela.

     @param arquivofxml O caminho do arquivo FXML que define a interface da cena.

     @param Mensagem O título da janela.
     */
    private void Criarcena(String arquivofxml, String Mensagem){
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

     Obtém a lista de funcionários da base de dados.

     @return A lista de funcionários.
     */
    public List<funcionario> getFuncionario(){
        String query = "SELECT * FROM Listarfuncionario";
        try {

            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String nome = resultSet.getString("nome");
                int telefone = resultSet.getInt("telefone");
                String Password = resultSet.getString("password");
                int Nivel_Acesso = resultSet.getInt("nivel_acesso");

                funcionario funcionarios1 = new funcionario(id, nome, email, Password, telefone, Nivel_Acesso);
                observableFuncionarioList = FXCollections.observableList(funcionariosList);
                funcionariosList.add(funcionarios1);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return funcionariosList;
    }

    /**

     Inicializa o controlador da interface.
     @param location O URL de localização do FXML, não utilizado neste caso.
     @param resources Os recursos, não utilizados neste caso.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        funcionario.funcionariosList.removeAll(funcionariosList);
        tablePopulate();
        selectRow();
        tableFuncionarios.setPlaceholder(new Label("Não foram encontrados resultados."));
        tableFuncionarios.setItems(observableFuncionarioList);
    }

    private void tablePopulate(){
        List<funcionario> funcionarioList = getFuncionario();

        c_nome.setCellValueFactory(new PropertyValueFactory<funcionario, String>("nome"));
        c_email.setCellValueFactory(new PropertyValueFactory<funcionario, String>("email"));
        c_telefone.setCellValueFactory(new PropertyValueFactory<funcionario, Integer>("telefone"));
        c_NivelAcesso.setCellValueFactory(new PropertyValueFactory<funcionario, Integer>("nv_acesso"));

        tableFuncionarios.setItems(FXCollections.observableList(funcionarioList));
    }
    /**

     Preenche a tabela de funcionários com os dados obtidos da base de dados.
     */
    private void selectRow(){
        tableFuncionarios.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                funcionario selectedfuncionario = tableFuncionarios.getSelectionModel().getSelectedItem();
                if (selectedfuncionario != null) {
                    int id = selectedfuncionario.getId();

                    System.out.println("Double-clicked: " + id);
                    try {
                        verStaff(id);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**

     Seleciona uma linha na tabela de funcionários ao clicar duas vezes.

     Abre a janela de perfil do funcionário correspondente ao ID selecionado.
     */
    @FXML
    private void selectFuncionario(){
//        Object id = tableFuncionarios.getSelectionModel().getSelectedItem().toString();
//        System.out.println(id);

    }


}
