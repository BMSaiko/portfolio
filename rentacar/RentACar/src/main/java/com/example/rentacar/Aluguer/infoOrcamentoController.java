package com.example.rentacar.Aluguer;

import com.example.rentacar.Cliente.Clientes;
import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Veiculo.Editar_VeiculoController;
import com.example.rentacar.Veiculo.Veiculo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class infoOrcamentoController implements Initializable {
    private int receivedID;
    @FXML
    private Label txt_norcamento;

    @FXML
    private Label txt_ncliente;
    @FXML
    private Label txt_nfuncionario;


    @FXML
    private Label txt_estadopagamento;

    @FXML
    private Label txt_estadoaluguer;
    @FXML
    private Button editOrcamento;
    private String caminhofoto;

    @FXML
    private ImageView image;
    @FXML
    private TableView<Veiculo> tableVeiculos;
    @FXML
    private TableColumn<Veiculo, String> c_matricula;
    @FXML
    private TableColumn<Veiculo, String> c_marca;
    @FXML
    private TableColumn<Veiculo, String> c_modelo;
    @FXML
    private TableColumn<Veiculo, Integer> c_kms;
    @FXML
    private TableColumn<Veiculo, String> c_seguro;
    @FXML
    private TableColumn<Veiculo, Date> c_revisao;
    @FXML
    private TableColumn<Veiculo, Date> c_inspecao;
    @FXML
    private TableColumn<Veiculo, String> c_limpeza;
    @FXML
    private TableColumn<Veiculo, String > c_categoria;
    @FXML
    private TableColumn<Veiculo, Integer> c_cv;
    @FXML
    private TableColumn<Veiculo, Integer> c_cilindrada;
    @FXML
    private TableColumn<Veiculo, String> c_combustivel;
    @FXML
    private TableColumn<Veiculo, Integer> c_manutencao;
    @FXML
    private TableColumn<Veiculo, String> c_disponibilidade;
    ArrayList<Veiculo> veiculoList = Veiculo.veiculoList;
    ObservableList<Veiculo> observableVeiculoList;


    /**

     Sets the received ID and updates the target controller with the information related to the ID.

     @param id The received ID.

     @param targetController The target controller to update with the information.
     */
    public void setReceivedID(int id,infoOrcamentoController targetController){
        this.receivedID = id;
        System.out.println(receivedID);

        String fieldPagamento, fieldAluger,fieldCliente, fieldFuncionario;
        int fieldOrcamento;
        Image fieldImagem;

        String query = "SELECT DISTINCT ea.estado,\n" +
                "                    o.v_orcamento,\n" +
                "                    o.id as id,\n" +
                "                    Cli.nome as Cliente_nome,\n" +
                "                    con.matricula as matricula,\n" +
                "                    f.nome as Funcionario_nome,\n" +
                "                    con.data_inic,\n" +
                "                    con.data_fim,\n" +
                "                    eo.estado_orcamento,\n" +
                "                    (SELECT COUNT(con.matricula) FROM Contratos as con WHERE con.id_orcamento = o.id) AS n_carros\n" +
                "    FROM aluguer as a\n" +
                "             INNER JOIN Orcamento as o ON o.id = a.id_orcamento\n" +
                "             INNER JOIN estado_aluguer as ea ON ea.id = a.estado\n" +
                "             INNER JOIN Contratos as con ON con.id_orcamento = o.id\n" +
                "             INNER JOIN Cliente as cli ON cli.id = o.id_Cliente\n" +
                "             INNER JOIN Funcionario as f ON f.id = o.id_funcionario\n" +
                "             INNER JOIN estado_orcamento as eo ON o.estado_orcamento = eo.id" +
                "             WHERE o.id =" + receivedID;


        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.first()){
                System.out.println(resultSet.getRow());
                fieldOrcamento = resultSet.getInt("id");
                fieldPagamento = resultSet.getString("estado_orcamento");
                fieldAluger = resultSet.getString("estado");
                fieldCliente = resultSet.getString("Cliente_nome");
                fieldFuncionario = resultSet.getString("Funcionario_nome");

//                if(resultSet.getString("Imagem")!= null) {
//                    this.caminhofoto=resultSet.getString("Imagem");
//                    fieldImagem = new Image("file:" + caminhofoto);
//                }else{
//                    this.caminhofoto=getClass().getClassLoader().getResource("/com/example/rentacar/images/guest.png").toExternalForm();
//                    fieldImagem = new Image(caminhofoto);
//                }
                txt_norcamento.setText(String.valueOf(fieldOrcamento));
                txt_estadopagamento.setText(fieldPagamento);
                txt_estadoaluguer.setText(fieldAluger);
                txt_ncliente.setText(fieldCliente);
                txt_nfuncionario.setText(fieldFuncionario);

//                image.setImage(fieldImagem);

                targetController.initialize(null,null);

            }else {
                System.out.println("n");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    /**

     Retrieves a list of vehicles associated with the received ID.

     @return The list of vehicles.
     */
    private List<Veiculo> getVeiculo(){
//        List<Veiculo> veiculo = new ArrayList<>();
        String query = "SELECT * FROM ListarVeiculo WHERE matricula IN (SELECT matricula FROM contratos WHERE id_orcamento = " + receivedID + ")";

        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                String matricula = resultSet.getString("matricula");
                String marca = resultSet.getString("marca");
                String modelo = resultSet.getString("modelo");
                int cilindrada = resultSet.getInt("cilindrada");
                int cv = resultSet.getInt("cavalos");
                String combustivel = resultSet.getString("combustivel");
                int kms = resultSet.getInt("kms");
                LocalDate prox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                LocalDate ult_inspecao=resultSet.getDate("ult_inspecao").toLocalDate();;
                String limpeza= resultSet.getString("stat");
                String categoria = resultSet.getString("categoria");
                int id_manutencao = resultSet.getInt("id_manutencao");
                String disponibilizado = resultSet.getString("disponibilidade");
                String seguro = resultSet.getString("nome");
                String imagem = resultSet.getString("imagem");
                int valor_dia = resultSet.getInt("valor_dia");

                Veiculo veiculo1 = new Veiculo(imagem, valor_dia, matricula,marca,modelo,cilindrada,cv,combustivel,kms,prox_revisao,ult_inspecao,limpeza,categoria,id_manutencao,disponibilizado,seguro);
                 veiculoList.add(veiculo1);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return veiculoList;

    }

    /**

     Populates the table with the list of vehicles.
     */
    public void tablePopulate(){
        List<Veiculo> veiculoList = getVeiculo();

        c_matricula.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("matricula"));
        c_marca.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("marca"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("modelo"));
        c_cilindrada.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cilindrada"));
        c_cv.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cv"));
        c_combustivel.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("combustivel"));
        c_kms.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("kms"));
        c_revisao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("prox_revisao"));
        c_inspecao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("ult_inspecao"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("limpeza"));
        c_categoria.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("categoria"));
        c_manutencao.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("id_manutencao"));
        c_disponibilidade.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("disponibilizado"));
        c_seguro.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("seguro"));

        tableVeiculos.setItems(FXCollections.observableList(veiculoList));
    }

    /**

     Handles the event when the "Edit Orçamento" button is clicked.

     Loads the "editar_aluguer.fxml" view and initializes the Edit_AluguerController.

     Sets the receivedID value in the target controller.

     Creates and displays a new stage to show the view.

     @param event The action event triggered by clicking the button.
     */
    @FXML
    void editOrcamento(ActionEvent event){

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/editar_aluguer.fxml"));
            Parent root = loader.load();
            Edit_AluguerController targetController = loader.getController();

            targetController.setReceiveid(receivedID);
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle("Editar Orçamento");
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**

     Initializes the controller.
     Clears the tableVeiculos items and populates it with the list of vehicles.
     Called when the view is loaded.
     @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableVeiculos.getItems().clear();
        tablePopulate();
    }




}
