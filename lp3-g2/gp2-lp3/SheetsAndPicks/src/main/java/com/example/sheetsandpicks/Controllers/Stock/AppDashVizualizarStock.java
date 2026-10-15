package com.example.sheetsandpicks.Controllers.Stock;

import com.example.sheetsandpicks.BL.Database.DBstatements;
import com.example.sheetsandpicks.BL.Stock.StockFunctions;
import com.example.sheetsandpicks.BL.util;
import com.example.sheetsandpicks.Models.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppDashVizualizarStock implements Initializable {

    @FXML
    private TableColumn<Stock, String> c_Fornecedor;

    @FXML
    private TableColumn<Stock, Integer> c_Quantidade;

    @FXML
    private TableColumn<Stock, String> c_nome;

    @FXML
    private TableColumn<Stock, Float> c_p_uni;

    @FXML
    private TableColumn<Stock, Float> c_v_base;

    @FXML
    private TableColumn<Stock, String> c_UOM;

    @FXML
    private TableColumn<Stock, String> c_pais;

    @FXML
    private TableView<Stock> table_stock;

    @FXML
    private Button btn_adicionar_stock;

    @FXML
    private Button btn_confirmar_stock;

    ArrayList<Stock> Stock_List = Stock.StockList;
    ObservableList<Stock> observableList;

    String tipo;

    /**
     * Preenche a tabela de stock com dados obtidos da base de dados.
     * Obtém a lista de stocks utilizando a classe DBstatements, define as propriedades das colunas da tabela
     * e define os itens da tabela com a observablelist de stocks.
     */
    private void EncherTable(){
        List<Stock> StockList = DBstatements.GetStock();

        c_nome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        c_Fornecedor.setCellValueFactory(new PropertyValueFactory<>("Fornecedor"));
        c_Quantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        c_p_uni.setCellValueFactory(new PropertyValueFactory<>("Preco_uni"));
        c_v_base.setCellValueFactory(new PropertyValueFactory<>("v_base"));
        c_UOM.setCellValueFactory(new PropertyValueFactory<>("UOM"));
        c_pais.setCellValueFactory(new PropertyValueFactory<>("pais"));

        table_stock.setItems(FXCollections.observableList(StockList));
    }

    /**
     * Manipula o evento de adição de stock.
     * Chama o método estático da classe StockFunctions para verificar um arquivo XML relacionado aos stock.
     *
     * @param event O evento de ação associado ao clique no botão de adição de stock.
     */
    @FXML
    void Handler_adicionar_Stock(ActionEvent event) {
        StockFunctions.Verificarxml();
    }

    /**
     * Manipula o evento de confirmação de Stock.
     * Cria uma nova cena para exibir a confirmação do XML usando o método `criarCena` da classe `util`.
     *
     * @param event O evento de ação associado ao clique no botão de confirmação de Stock.
     */
    @FXML
    void Handler_confirmar_Stock(ActionEvent event) {
        String fxml = "com/example/sheetsandpicks/app_Confirmar-Stock.fxml";
        String title= "Confirmação do XML";
        util.criarCena(fxml,title);
    }

    /**
     * Configura um evento de clique duplo na tabela de Stock para realizar uma ação quando um item é selecionado.
     * Obtém o Stock selecionado na tabela, mas não executa nenhuma ação específica no método.
     */
    private void selectRow(){
        table_stock.setOnMouseClicked(event -> {
            if(event.getButton()== MouseButton.PRIMARY && event.getClickCount()==2){
                Stock selectedStock = table_stock.getSelectionModel().getSelectedItem();
            }
        });
    }

/**
 * Define o tipo de funcionário para o controle e ajusta a visibilidade do botão de confirmação.
 *
 * @param Funcionario O tipo de funcionário a ser definido (por exemplo, "Fornecedor", "Operador", "Admin").
 */
    public void setFuncionario(String Funcionario) {
        tipo = Funcionario;
        btn_confirmar_stock.setVisible(true);
        System.out.println(tipo);
        if (tipo.equals("Fornecedor")){
            btn_confirmar_stock.setVisible(false);
        }
        else {  btn_confirmar_stock.setVisible(true);}
    }

    /**
     * Método chamado automaticamente durante a inicialização do controlador FXML.
     * Inicializa a interface do utilizador, removendo itens da lista de stocks a confirmar, definindo um placeholder para
     * a tabela de stocks, configurando a tabela com uma observablelist, preenchendo a tabela com dados da base de dados
     * e definindo eventos de clique na tabela.
     *
     * @param location  O localizador de recursos para a URL, não é utilizado neste método.
     * @param resources Os recursos para a ResourceBundle, não é utilizado neste método.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        btn_confirmar_stock.setVisible(true);
        Stock.StockList.removeAll(Stock_List);
        table_stock.setPlaceholder(new Label("Não existe Stock novo a confirmar"));
        table_stock.setItems(observableList);
        EncherTable();
        selectRow();
    }
}

