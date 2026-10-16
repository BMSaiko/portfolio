package com.example.rentacar.Cliente;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class Editar_ClienteController implements Initializable {

    int id;

    private String caminhofoto;

    @FXML
    private Button Cancelbutton;

    @FXML
    private TextField NIF_txt;

    @FXML
    private DatePicker dataNascimento_datapicker;

    @FXML
    private Button editbtn;

    @FXML
    private TextField email_txt;

    @FXML
    private ImageView image;

    @FXML
    private Button importbtn;

    @FXML
    private TextField morada_txt;

    @FXML
    private TextField name_txt;

    @FXML
    private TextField telefone_txt;

    @FXML
    private TextField txt_CC;

    @FXML
    private TextField txt_CConducao;

    @FXML
    private TextField txt_Pais;


    /**

     Sets the received ID for the client and retrieves and displays the client's information from the database.

     @param id The ID of the client.
     */
    public void setReceivedId(int id) {
        this.id = id;

        String fieldNome, fieldEmail, fieldTelefone, fieldNIF = null , fieldMorada, fieldCC , fieldCconducao,fieldPais;
        Image fieldfoto;
        LocalDate fieldData_nascimento;

        String query = "SELECT c.nome as nome,c.email as email,ltc.telefone as telefone,c.nif as nif, c.morada as morada, c.cc as cc, c.cconducao as cconducao,c.país as pais, c.nascimento as data_nascimento , c.Imagem as imagem FROM Cliente c " +
                "INNER JOIN list_telefonica_cliente ltc on c.id_list_telefonica=ltc.id where c.id =" + id;

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.first()) {
                fieldNome = resultSet.getString("nome");
                fieldEmail = resultSet.getString("email");
                fieldTelefone = resultSet.getString("telefone");
                fieldNIF = resultSet.getString("NIF");
                fieldMorada=resultSet.getString("morada");
                fieldCC=resultSet.getString("cc");
                fieldCconducao=resultSet.getString("cconducao");
                fieldPais=resultSet.getString("pais");
                fieldData_nascimento=resultSet.getDate("data_nascimento").toLocalDate();
                if(resultSet.getString("Imagem")!= null) {
                    this.caminhofoto=resultSet.getString("Imagem");
                    fieldfoto = new Image("file:" + caminhofoto);
                }else{
                    this.caminhofoto=getClass().getClassLoader().getResource("/com/example/rentacar/images/guest.png").toExternalForm();
                    fieldfoto = new Image(caminhofoto);
                }


                name_txt.setText(fieldNome);
                email_txt.setText(fieldEmail);
                telefone_txt.setText(fieldTelefone);
                NIF_txt.setText(fieldNIF);
                morada_txt.setText(fieldMorada);
                txt_CC.setText(fieldCC);
                txt_CConducao.setText(fieldCconducao);
                txt_Pais.setText(fieldPais);
                dataNascimento_datapicker.setValue(fieldData_nascimento);
                image.setImage(fieldfoto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**

     Action handler for the "Cancelar" button in the edit client form.

     Displays a confirmation dialog and closes the window if confirmed.

     @param event The action event triggered by clicking the "Cancelar" button.
     */
        @FXML
    void CancelarEdit(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a Edição do Cliente?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) Cancelbutton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**

     Action handler for the "EditClienteInfo" button in the edit client form.

     Retrieves the input values from the form fields and updates the client's information in the database.

     Closes the window upon successful update.

     @param event The action event triggered by clicking the "EditClienteInfo" button.
     */
    @FXML
    void EditClienteInfo(ActionEvent event) {
        int id = this.id;
        String nome= name_txt.getText();
        String email = email_txt.getText();
        int Telefone= Integer.parseInt(telefone_txt.getText());
        int NIF = Integer.parseInt(NIF_txt.getText());
        String Morada = morada_txt.getText();
        LocalDate Data_nascimento = dataNascimento_datapicker.getValue();
        int CC = Integer.parseInt(txt_CC.getText());
        int Cconducao = Integer.parseInt(txt_CConducao.getText());
        String Pais = txt_Pais.getText();

        String query="exec Upd_Cliente @id="+id+", @nome = '" +nome+ "' , @email = '"+email+"', @Telefone = "+Telefone+" , @NIF = "+NIF+" , @Morada = '"+Morada+"' , @Data_nascimento = '"+Data_nascimento+"' , @CC = "+CC+" , @Cconducao = "+Cconducao+" , @pais = '"+Pais+"' , @image = '"+caminhofoto+"' ;";
        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            statement.executeUpdate(query);
            Stage stage = (Stage) editbtn.getScene().getWindow();
            stage.close();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**

    Action handler for the "Importar_imagem" button in the edit client form.
    Allows the user to select an image file from the file system and sets it as the client's profile image.
    @param event The action event triggered by clicking the "Importar_imagem" button.
    */
    @FXML
    void Importar_imagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            File destino = new File("resources/com/example/rentacar/images/clientes");
            try {
                Path origemPath = selectedFile.toPath();
                Path destinoPath = destino.toPath().resolve(selectedFile.getName());
                Files.createDirectories(destinoPath.getParent());
                Files.copy(origemPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
                this.caminhofoto = destinoPath.toString();
                Image novaImagem = new Image(destinoPath.toUri().toString());
                image.setImage(novaImagem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**

     Initializes the edit client form by setting the received ID and displaying the client's information.
     @param location The location used to resolve relative paths for the root object.
     @param resources The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setReceivedId(id);
    }
}

