package com.example.sheetsandpicks.Controllers.Stock;

import com.example.sheetsandpicks.Models.Stock;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PopUp_ConfirmarStockController {

    @FXML
    private Button btn_ok;

    @FXML
    private ComboBox<String> cmb_aceept_reject;

    private Stock produto;

    @FXML
    private Label lb_produto;

    private StockConfirmationController a;

    private String nome;


    /**
     * Define o produto associado a este controlador e atualiza a exibição do nome do produto em um elemento de interface do utilizador.
     *
     * @param produto O objeto Stock que representa o produto a ser associado a este controlador.
     */
    public void setProduto(Stock produto){
        this.produto=produto;
        this.nome=produto.getNome();
        lb_produto.setText(nome);
    }

    /**
     * Define o controlador da tabela de itens confirmados associado a este controlador.
     *
     * @param TabelaConfirmar_StockFill O controlador da tabela de itens confirmados a ser associado a este controlador.
     */
    public void setTabelaConfirmar_StockFill(StockConfirmationController TabelaConfirmar_StockFill){
        this.a = TabelaConfirmar_StockFill;
    }

    /**
     * Manipula o evento de confirmação da opção selecionada.
     * Define o status de aprovação/rejeição para o produto associado, preenche a tabela de itens confirmados usando o
     * controlador associado e fecha a janela de confirmação.
     *
     * @param event O evento de ação associado ao clique no botão de confirmação.
     */
    @FXML
    void Handle_Confirmation_Option(ActionEvent event) {
        produto.setAprov_rejeitado(cmb_aceept_reject.getValue());
        a.TabelaConfirmar_StockFill(produto);
        Stage stage = (Stage) btn_ok.getScene().getWindow();
        stage.close();
    }

}