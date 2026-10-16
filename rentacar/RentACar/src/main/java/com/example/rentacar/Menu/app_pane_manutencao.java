package com.example.rentacar.Menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class app_pane_manutencao {

    @FXML
    private Button Limpezabtn;

    @FXML
    private Button insp_revibtn;

    /**

     Abre a cena de limpeza.
     Carrega o arquivo FXML da cena de limpeza e exibe em uma nova janela.
     @param event O evento de clique no botão para abrir a cena de limpeza.
     */
    @FXML
    void abrirLimpeza(ActionEvent event) {Criarcena("/com/example/rentacar/dash_limpeza.fxml","Limpeza");}

    /**

     Abre a cena de inspeção/revisão.
     Carrega o arquivo FXML da cena de inspeção/revisão e exibe em uma nova janela.
     @param event O evento de clique no botão para abrir a cena de inspeção/revisão.
     */
    @FXML
    void abrir_insp_revisao(ActionEvent event) {
        Criarcena("/com/example/rentacar/dash_inspecao_revisao.fxml","Inspeção/Revisão");
    }

    /**

     Cria e exibe uma nova janela com a cena especificada pelo arquivo FXML.

     @param arquivofxml O caminho do arquivo FXML que define a cena a ser exibida.

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

}