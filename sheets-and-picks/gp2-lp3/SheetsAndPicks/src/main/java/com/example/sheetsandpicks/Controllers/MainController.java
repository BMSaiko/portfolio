package com.example.sheetsandpicks.Controllers;

import com.example.sheetsandpicks.Controllers.Stock.AppDashVizualizarStock;
import com.example.sheetsandpicks.Controllers.Users.AdminsController;
import com.example.sheetsandpicks.Controllers.Users.ClientsController;
import com.example.sheetsandpicks.Controllers.Users.OperatorsController;
import com.example.sheetsandpicks.Controllers.Users.SuppliersController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ComboBox<String> comboStock;

    @FXML
    private ComboBox<String> comboUti;

    @FXML
    private AnchorPane pane;

    private String nv = "";
    
    /**
     * Inicia o controlador do FXML, chamando-o automaticamente após o carregar o respectivo arquivo FXML.
     *
     * @param location  O URL de onde o arquivo FXML foi carregado. Pode ser nulo se o arquivo não foi carregado de um URL.
     * @param resources Os recursos locais que foram injetados neste controlador. Pode ser nulo se nenhum recurso for especificado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {



        comboStock.getItems().addAll("- Stock","+ Ver Stock");
        comboUti.setOnAction(event -> {
            String selectedItem = comboUti.getValue();

            if ("+ Fornecedor".equals(selectedItem)) {
                carregarConteudo("com/example/sheetsandpicks/app_suppliers.fxml", "Fornecedor");
            } else if ("+ Operador".equals(selectedItem)) {
                carregarConteudo("com/example/sheetsandpicks/app_operators.fxml", "Operador");
            } else if ("+ Clientes".equals(selectedItem)) {
                carregarConteudo("com/example/sheetsandpicks/app_clients.fxml", "Clientes");
            } else if("+ Admin".equals(selectedItem)){
                carregarConteudo("com/example/sheetsandpicks/app_admins.fxml", "Admin");
            }else {
                CarregarConteudo2("com/example/sheetsandpicks/app_utilizador_movimentos.fxml");
            }

        });

        comboStock.setOnAction(event -> {
            String selectedItem = comboStock.getValue();
            if ("+ Ver Stock".equals(selectedItem) && nv.equals("Fornecedor")) {
                carregarConteudoStock("com/example/sheetsandpicks/app_dash_vizualizarStock.fxml","Fornecedor");
            } else {
                CarregarConteudo2("com/example/sheetsandpicks/app_dash_vizualizarStock.fxml");
            }
        });
    }

    /**
     * Atualiza as opções da combobox "comboUti" com base no nível de permissão fornecido.
     *
     * @param nivelPermissao O nível de permissão do utilizador para determinar as opções disponíveis na combobox.
     */
    public void atualizarComboUti(String nivelPermissao) {
        comboUti.getItems().clear();
        nv=nivelPermissao;
        carregarConteudoStock("com/example/sheetsandpicks/app_dash_vizualizarStock.fxml", nv);

        // Lógica para adicionar itens com base no nível de permissão


        System.out.println(nivelPermissao);
        if ("Admin".equals(nivelPermissao)) {
            comboUti.getItems().addAll("+ Clientes", "+ Fornecedor", "+ Operador", "+ Admin");
        } else if ("Fornecedor".equals(nivelPermissao)){
            comboUti.getItems().addAll("+ Perfil");
        } else if ("Operador".equals(nivelPermissao)) {
            comboUti.getItems().addAll("+ Clientes", "+ Fornecedor");
        }

        comboUti.getSelectionModel().selectFirst();
    }


    /**
     * Carrega o conteúdo do arquivo FXML na janela principal, substituindo o conteúdo existente,consoante o seu tipo de utilizador.
     *
     * @param fxmlFile O caminho do arquivo FXML a ser carregado.
     * @param userType O tipo de usuário que determinará qual o controlador FXML a ser utilizado.
     */
    private void carregarConteudo(String fxmlFile, String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Node conteudo = loader.load();

            // Assuming the controller of the loaded FXML has a method setUserName
            // to receive the user type
            if(userType == "Fornecedor")
            {
                SuppliersController userController = loader.getController();
                userController.setFuncionario(userType);
            }
            else if(userType == "Operador"){
                OperatorsController userController = loader.getController();
                userController.setFuncionario(userType);
            }
            else if(userType == "Admin"){
                AdminsController userController = loader.getController();
                userController.setFuncionario(userType);
            }
            else {
                ClientsController userController = loader.getController();
                userController.setFuncionario(userType);
            }

            pane.getChildren().setAll(conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega o conteúdo de um arquivo FXML na janela principal, substituindo o conteúdo existente.
     *
     * @param fxmlFile O caminho do arquivo FXML a ser carregado.
     * @throws RuntimeException Se ocorrer um erro de IO durante o carregamento do arquivo FXML.
     */

    public void CarregarConteudo2(String fxmlFile){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/"+fxmlFile));
            Node conteudo = loader.load();

            pane.getChildren().setAll(conteudo);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void carregarConteudoStock(String fxmlFile, String userType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
            Node conteudo = loader.load();

            // Assuming the controller of the loaded FXML has a method setUserName
            // to receive the user type
            if(userType == "Fornecedor")
            {
                AppDashVizualizarStock userController = loader.getController();
                userController.setFuncionario(userType);
            }
            else{
                AppDashVizualizarStock userController = loader.getController();
                userController.setFuncionario(userType);
            }


            pane.getChildren().setAll(conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}