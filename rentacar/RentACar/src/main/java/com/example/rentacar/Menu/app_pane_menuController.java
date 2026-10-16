package com.example.rentacar.Menu;

import com.example.rentacar.Singleton.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class app_pane_menuController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private Pane paneRent;
    @FXML
    private Button butAluguerQuiz;

    @FXML
    private Button butCompras;

    @FXML
    private Button butmanutencao;

    @FXML
    private Button butrentabilidade;

    private int nivelAcesso;

    public int getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }


    /**

     Método de evento acionado ao selecionar a opção de manutenção.
     Torna o painel visível e carrega o conteúdo do arquivo FXML correspondente.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void SelecionarManutencao(ActionEvent event) {
        pane.setVisible(true);
        carregarConteudo("/com/example/rentacar/app_pane_manutencao.fxml");
    }

    /**

     Método de evento acionado ao abrir o menu de aluguel.
     Torna o painel visível, carrega o conteúdo do arquivo FXML correspondente e cria uma nova janela para criar um aluguel.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void abrirAluguerQuiz(ActionEvent event) {
        pane.setVisible(true);
        carregarConteudo("/com/example/rentacar/app_pane_menu.fxml");
        Criarcena("/com/example/rentacar/criar_alugar.fxml", "Aluguer");
    }

    /**

     Método de evento acionado ao abrir a funcionalidade de rentabilidade.
     Torna o painel visível e carrega o conteúdo do arquivo FXML correspondente.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void abrirRentabilidade(ActionEvent event) {
        pane.setVisible(true);
        carregarConteudo("/com/example/rentacar/dash_rentabilidade.fxml");
    }

    /**

     Método de evento acionado ao ver as compras.
     @param event O evento de ação que acionou o método.
     */
    @FXML
    void verCompras(ActionEvent event) {

    }

    /**

     Exibe o conteúdo do painel ao carregar o arquivo FXML especificado.
     @param fxmlFile O caminho do arquivo FXML que contém o conteúdo a ser exibido no painel.
     */
    @FXML
    private void carregarConteudo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node conteudo = loader.load();
            pane.getChildren().setAll(conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //public void enviaDados(int nvAcesso, app_pane_menuController targetController2){
    //  setNivelAcesso(nvAcesso);
    //  targetController2.initialize(null, null);
    //}

    /**

     Inicializa o controlador.

     Verifica o nível de acesso do usuário e configura a visibilidade do painel de acordo com o nível.

     @param location O localizador de recursos.

     @param resources Os recursos utilizados pelo controlador.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Controller controllerA = new Controller();

        nivelAcesso = controllerA.getNivelAcesso();

        //JOptionPane.showMessageDialog(null, nivelAcesso);
        if(nivelAcesso < 3){
            paneRent.setVisible(false);
        }
        else{
            paneRent.setVisible(true);
        }
    }

    /**

     Cria e exibe uma nova janela com a cena especificada pelo arquivo FXML.

     @param arquivofxml O caminho do arquivo FXML que define a cena a ser exibida.

     @param mensagem O título da janela.
     */
    private void Criarcena(String arquivofxml, String mensagem) {
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
        novaJanela.setTitle(mensagem);
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }
}