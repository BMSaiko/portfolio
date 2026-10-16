package com.example.rentacar.Cliente;

import com.example.rentacar.Database.DatabaseConnection;
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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.rentacar.Cliente.InfoClienteModel.InfoclientesList;
import static com.example.rentacar.Cliente.InfoClienteModel.InfoclientesList2;

public class infoClienteController implements Initializable {

    @FXML
    private TableColumn<InfoClienteModel, String> c_marca;

    @FXML
    private TableColumn<InfoClienteModel, String> c_modelo;

    @FXML
    private ImageView image;

    @FXML
    private TableColumn<InfoClienteModel, Integer> c_n_aluguerers;

    @FXML
    private TableView<InfoClienteModel> table_top_aluguer;

    @FXML
    private Button buttonchange;

    @FXML
    private Label n_cliente;

    @FXML
    private Label nome;

    @FXML
    private Label email;
    @FXML
    private Label telefone;
    @FXML
    private Label NIF;

    private int receivedID=0;

    @FXML
    private TableColumn<InfoClienteModel, LocalDate> c_data_fim;

    @FXML
    private TableColumn<InfoClienteModel, LocalDate> c_data_inic;

    @FXML
    private TableColumn<InfoClienteModel, String> c_marca2;


    @FXML
    private TableColumn<InfoClienteModel, String> c_modelo2;


    @FXML
    private TableColumn<InfoClienteModel, Integer> c_valor;


    @FXML
    private TableView<InfoClienteModel> registotable;


    ArrayList<InfoClienteModel> InfoclienteList = InfoclientesList;
    ArrayList<InfoClienteModel> InfoclienteList2 = InfoClienteModel.InfoclientesList2;


    ObservableList<InfoClienteModel> observableClienteList;

    ObservableList<InfoClienteModel> observableClienteList2;
    private String caminhofoto;


    /**

     Sets the received ID for the client and retrieves and displays the client's information from the database.

     @param id The ID of the client.

     @param targetController The target controller to pass the received ID.
     */
    public void setReceivedId(int id,infoClienteController targetController){
        this.receivedID = id;
        System.out.println(receivedID);

        String fieldID,fieldNome,fieldEmail,fieldTelefone,fieldNIF = null;
        Image fieldImage;

        String query = "SELECT c.id as id,c.nome as nome,c.email as email,ltc.telefone as telefone,c.nif as nif, c.Imagem as imagem FROM Cliente c " +
                "INNER JOIN list_telefonica_cliente ltc on c.id_list_telefonica=ltc.id where c.id =" + receivedID;


        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.first()){
                fieldID = resultSet.getString("id");
                fieldNome = resultSet.getString("nome");
                fieldEmail = resultSet.getString("email");
                fieldTelefone = resultSet.getString("telefone");
                fieldNIF = resultSet.getString("NIF");
                if(resultSet.getString("Imagem")!= null) {
                    this.caminhofoto=resultSet.getString("Imagem");
                    fieldImage = new Image("file:" + caminhofoto);
                }else{
                    this.caminhofoto=getClass().getClassLoader().getResource("com/example/rentacar/images/guest.png").toExternalForm();
                    fieldImage = new Image(caminhofoto);
                }

                n_cliente.setText(fieldID);
                nome.setText(fieldNome);
                email.setText(fieldEmail);
                telefone.setText(fieldTelefone);
                NIF.setText(fieldNIF);
                image.setImage(fieldImage);

                targetController.initialize(null,null);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**

     Retrieves the top car rental brands for the client and returns a list of InfoClienteModel objects.

     @return The list of InfoClienteModel objects representing the top car rental brands.
     */
    public List<InfoClienteModel> gettop_aluguer_cliente(){
        String query="EXEC Topo_marcas @id="+receivedID+";";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String Marca=resultSet.getString("marca");
                String Modelo=resultSet.getString("modelo");
                int n_aluguers= resultSet.getInt("total_clientes");
                System.out.println(Marca);

                InfoClienteModel cliente1 = new InfoClienteModel(n_aluguers,Modelo,Marca,id);
                observableClienteList = FXCollections.observableList(InfoclientesList);
                InfoclienteList.add(cliente1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return InfoclienteList;
    }

    /**

     Retrieves the rental history for the client and returns a list of InfoClienteModel objects.

     @return The list of InfoClienteModel objects representing the rental history.
     */
    public List<InfoClienteModel> getregisto_aluguer_cliente(){
        String query="EXEC registo_aluguer_cliente @id="+receivedID+";";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String Marca = resultSet.getString("marca");
                String Modelo = resultSet.getString("modelo");
                LocalDate data_inic = resultSet.getDate("data_inic").toLocalDate();
                LocalDate data_fim = resultSet.getDate("data_fim").toLocalDate();
                int valor = resultSet.getInt("valor");

                InfoClienteModel cliente2 = new InfoClienteModel(id,Modelo,Marca,data_inic,data_fim,valor);
                observableClienteList2 = FXCollections.observableList(InfoclientesList2);
                InfoclienteList2.add(cliente2);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return InfoclienteList2;
    }

    /**

     Populates the top car rental brands table with data from the database.
     */
    public void tablePopulate() {
        List<InfoClienteModel> top_aluguer_cliente = gettop_aluguer_cliente();


        c_marca.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, String>("Marca"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, String>("Modelo"));
        c_n_aluguerers.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, Integer>("n_aluguers"));


        table_top_aluguer.setItems(FXCollections.observableList(top_aluguer_cliente));
    }

    /**

     Populates the rental history table with data from the database.
     */
    public void tablePopulate2(){
        List<InfoClienteModel> registo_aluguer = getregisto_aluguer_cliente();

        c_marca2.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, String>("Marca"));
        c_modelo2.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, String>("Modelo"));
        c_valor.setCellValueFactory(new PropertyValueFactory<InfoClienteModel, Integer>("valor"));
        c_data_fim.setCellValueFactory(new PropertyValueFactory<InfoClienteModel,LocalDate>("data_fim"));
        c_data_inic.setCellValueFactory(new PropertyValueFactory<InfoClienteModel,LocalDate>("data_inic"));

        registotable.setItems(FXCollections.observableList(registo_aluguer));

    }

    /**

     Handles the "EditarPerfil" button click event and opens the edit client form.

     @param event The action event triggered by clicking the "EditarPerfil" button.

     @throws IOException if an I/O error occurs.
     */
    @FXML
    void EditarPerfil(ActionEvent event) throws IOException {
        int id = receivedID;

        EditarCliente(id);
    }

    /**

     Opens the edit client form with the provided ID.

     @param id The ID of the client to edit.

     @throws IOException if an I/O error occurs.
     */
    private void EditarCliente(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/editar_cliente_info.fxml"));
        Parent root = loader.load();
        Editar_ClienteController targetController = loader.getController();

        targetController.setReceivedId(id);
        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle("Perfil Cliente");
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**

     Initializes the controller after its root element has been completely processed.

     This method is automatically called by the FXMLLoader.

     @param location The location used to resolve relative paths for the root object, or null if the location is not known.

     @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        InfoClienteModel.InfoclientesList.removeAll(InfoclienteList);
        InfoClienteModel.InfoclientesList2.removeAll(InfoclienteList2);
        if(receivedID!=0) {
            tablePopulate();
            table_top_aluguer.setPlaceholder(new Label("Não foram encontrados resultados."));
            tablePopulate2();
            registotable.setPlaceholder(new Label("Não foram encontrados resultados."));
        }
    }



}
