package com.example.rentacar.Veiculo;

import com.example.rentacar.Aluguer.Aluguer;
import com.example.rentacar.Aluguer.Orcamento;
import com.example.rentacar.Aluguer.infoOrcamentoController;
import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.dash.revisao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class infoVeiculoController implements Initializable {
    @FXML
    private Label matricula;
    @FXML
    private Label marca;
    @FXML
    private Label modelo;
    @FXML
    private Label cilindrada;
    @FXML
    private Label cavalos;
    @FXML
    private Label combustivel;
    @FXML
    private Label kms;
    @FXML
    private Label categoria;
    @FXML
    private Label prox_revisao;
    @FXML
    private Label prox_inspecao;
    @FXML
    private Label limpeza;
    @FXML
    private Label disponibilidade;
    @FXML
    private Label seguro;
    @FXML
    private TableView<Orcamento> tableOrcamento;
    @FXML
    private TableView<revisao> tableRevisao;
    @FXML
    private TableColumn<Orcamento,String> c_ncliente;
    @FXML
    private TableColumn<Orcamento, Date> c_datainic;
    @FXML
    private TableColumn<Orcamento, Date> c_datafinal;
    @FXML
    private TableColumn<Orcamento, Integer> c_valor;
    @FXML
    private TableColumn<revisao,Date> c_proxrevisao;
    @FXML
    private TableColumn<revisao,Integer> c_revisaokms;

    ArrayList<Orcamento> orcamentoList = Orcamento.orcamentoList;
    ArrayList<revisao> revisaoList1 = revisao.revisaoList;
    ObservableList<Orcamento> observableOrcamentoList;
    ObservableList<revisao> observableRevisaoList;

    private String caminhofoto;

    @FXML
    private ImageView image;
    private String receivedID;

    public void setReceivedID(String variable){
        this.receivedID = variable;
        System.out.println(receivedID);

        String fieldMatricula, fieldMarca, fieldModelo, fieldCombustivel, fieldCategoria, fieldProx_revisao, fieldProx_inspecao, fieldLimpeza, fieldDisponibilidade, fieldSeguro = null;
        int fieldCavalos, fieldCilindrada, fieldKms = 0;
        Image fieldImagem;

        String query = "SELECT v.matricula ,v.kms,v.Imagem,m.marca,m3.modelo,sp.cilindrada,sp.cavalos as cavalos,tc.combustivel,r.prox_revisao,i.ult_inspecao,\n" +
                "                l.stat as stat,m2.id,s.nome as nome,c.categoria,vd.disponibilidade as disponibilidade FROM Veiculos as v\n" +
                "                INNER JOIN specs as sp ON v.id_specs=sp.id\n" +
                "                INNER JOIN seguro as s  ON v.id_seguro=s.id_seguro\n" +
                "                INNER JOIN revisao as r ON v.id_revisao=r.id\n" +
                "                INNER JOIN inspecao as i ON v.id_inspecao=i.id\n" +
                "                INNER JOIN limpeza as l ON v.id_limpeza=l.id\n" +
                "                LEFT JOIN manutencao as m2 ON v.id_manutencao=m2.id\n" +
                "                INNER JOIN Veiculo_disponibilidade as vd ON v.id_disponibilidade=vd.id_disponibilidade\n" +
                "                INNER JOIN categoria c ON v.id_categoria=c.id " +
                "                INNER JOIN marca_modelo mm on v.id_marca_modelo = mm.id " +
                "                INNER JOIN marca as m ON mm.id_marca=m.id\n" +
                "                INNER JOIN tipo_combustivel as tc ON sp.combustivel=tc.id\n" +

                "                INNER JOIN modelo m3 on mm.id_modelo=m3.id WHERE v.matricula = '" + receivedID  + "'";


        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.first()){
                System.out.println(resultSet.getRow());
                fieldMatricula = resultSet.getString("matricula");
                fieldMarca = resultSet.getString("marca");
                fieldModelo = resultSet.getString("modelo");
                fieldCilindrada = resultSet.getInt("cilindrada");
                fieldCavalos = resultSet.getInt("cavalos");
                fieldCombustivel = resultSet.getString("combustivel");
                fieldKms = resultSet.getInt("kms");
                fieldCategoria = resultSet.getString("categoria");
                fieldProx_revisao = resultSet.getString("prox_revisao");
                fieldProx_inspecao = resultSet.getString("ult_inspecao");
                fieldLimpeza = resultSet.getString("stat");
                fieldDisponibilidade = resultSet.getString("disponibilidade");
                fieldSeguro = resultSet.getString("nome");
                if(resultSet.getString("Imagem")!= null) {
                    this.caminhofoto=resultSet.getString("Imagem");
                    fieldImagem = new Image("file:" + caminhofoto);
                }else{
                    this.caminhofoto=getClass().getClassLoader().getResource("/com/example/rentacar/images/guest.png").toExternalForm();
                    fieldImagem = new Image(caminhofoto);
                }


                marca.setText(fieldMarca);
                matricula.setText(fieldMatricula);
                modelo.setText(fieldModelo);
                cilindrada.setText(String.valueOf(fieldCilindrada));
                cavalos.setText(String.valueOf(fieldCavalos));
                combustivel.setText(fieldCombustivel);
                kms.setText(String.valueOf(fieldKms));
                categoria.setText(fieldCategoria);
                prox_revisao.setText(fieldProx_revisao);
                prox_inspecao.setText(fieldProx_inspecao);
                limpeza.setText(fieldLimpeza);
                disponibilidade.setText(fieldDisponibilidade);
                seguro.setText(fieldSeguro);
                image.setImage(fieldImagem);
                populateTables();

            }else {
                System.out.println("n");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    @FXML
    void editcarro(ActionEvent event) {
        String id = matricula.getText();
        try {
            EditarVeiculo(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void EditarVeiculo(String id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/editar_veiculo_info.fxml"));
        Parent root = loader.load();
        Editar_VeiculoController targetController = loader.getController();

        targetController.setReceivedId(id,targetController);
        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle("Perfil Veiculo");
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    private List<Orcamento> getAluguer() {
        List<Orcamento> aluguerList = new ArrayList<>();
        System.out.println("receivedID: " + receivedID);
        String query = "SELECT * FROM ConsultarAluguer WHERE matricula='" + receivedID + "'";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("oid_orcamento");
                String cliente_nome = resultSet.getString("Cliente_nome");
                String funcionario_nome = resultSet.getString("Funcionario_nome");
                int n_carros = resultSet.getInt("n_carros");
                LocalDate data_inic = resultSet.getDate("data_inic").toLocalDate();
                LocalDate data_final = resultSet.getDate("data_fim").toLocalDate();
                String estado_orcamento = resultSet.getString("estado_orcamento");
                String estado = resultSet.getString("estado");
                int valor_total = resultSet.getInt("v_orcamento");
                String matricula = resultSet.getString("matricula");

                Aluguer aluguer1 = new Aluguer(id, valor_total, estado_orcamento, cliente_nome, funcionario_nome, matricula, data_inic, data_final, n_carros, estado);
                observableOrcamentoList = FXCollections.observableList(orcamentoList);
                aluguerList.add(aluguer1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aluguerList;
    }

    private List<revisao> getRevisao(){
        List<revisao> revisaoList = new ArrayList<>();
        System.out.println("receivedID: " + receivedID);
        String query = "SELECT r.prox_revisao,r.revisao_kms,r.ult_revisao_kms, v.matricula , r.Descricao FROM revisao r INNER JOIN Veiculos v on v.id_revisao=r.id WHERE v.matricula='" + receivedID + "'";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String matricula = resultSet.getString("matricula");
                LocalDate prox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                int revisao_kms = resultSet.getInt("revisao_kms");
                int ult_revisao_kms = resultSet.getInt("ult_revisao_kms");
                String descricao = resultSet.getString("Descricao");


                revisao revisao1 = new revisao(matricula,prox_revisao,revisao_kms,descricao);
                observableRevisaoList = FXCollections.observableList(revisaoList1);
                revisaoList.add(revisao1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revisaoList;
    }

    private void tablePopulate1() {
        List<Orcamento> aluguerList = getAluguer();
        System.out.println("populate");

        c_ncliente.setCellValueFactory(new PropertyValueFactory<>("nome_cliente"));
        c_datainic.setCellValueFactory(new PropertyValueFactory<>("data_inic"));
        c_datafinal.setCellValueFactory(new PropertyValueFactory<>("data_fim"));
        c_valor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        tableOrcamento.setItems(FXCollections.observableList(aluguerList));
    }

    private void tablePopulate2(){
        List<revisao> revisaoList = getRevisao();
        System.out.println("ola");

        c_proxrevisao.setCellValueFactory(new PropertyValueFactory<>("data_revisao"));
        c_revisaokms.setCellValueFactory(new PropertyValueFactory<>("prox_revisao_kms"));

        tableRevisao.setItems(FXCollections.observableList(revisaoList));

    }

    private void populateTables(){
        tablePopulate1();
        tablePopulate2();
    }

    private void VerAluguer(int id){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/info_Aluguer.fxml"));
            Parent root = loader.load();
            infoOrcamentoController targetController = loader.getController();

            targetController.setReceivedID(id,targetController);
            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle("Informação Orçamento");
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableOrcamento.setPlaceholder(new Label("Não foram encontrados resultados"));
        tableRevisao.setPlaceholder(new Label("Não foram encontrados resultados"));
        tableOrcamento.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Orcamento selectedAluguer = tableOrcamento.getSelectionModel().getSelectedItem();
                if (selectedAluguer != null) {
                    int id = selectedAluguer.getId();
                    System.out.println("Double-clicked: " + id);
                    VerAluguer(id);
                }
            }
        });
    }
}
