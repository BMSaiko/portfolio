package com.example.rentacar.Cliente;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class Criar_ClienteController{

    @FXML
    private TextField NIF_text;

    @FXML
    private Button cancelbtn;

    @FXML
    private ImageView cli_photo;

    @FXML
    private Button createbtn;

    @FXML
    private TextField email_text;

    @FXML
    private Button importbtn;

    @FXML
    private TextField name_text;

    @FXML
    private TextField telefone_text;

    @FXML
    private DatePicker dataNascimento_Datepicker;

    @FXML
    private TextField Morada_text;

    @FXML
    private TextField txt_CC;

    @FXML
    private TextField txt_CConducao;

    @FXML
    private TextField txt_Pais;

    private String caminhofoto;

    /**

     Action handler for the "Cancel" button in the create client form.

     Displays a confirmation dialog and closes the window if confirmed.

     @param event The action event triggered by clicking the "Cancel" button.
     */
    @FXML
    void Cancel_create_client(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a criação do Cliente?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) cancelbtn.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**

     Action handler for the "Criar Cliente" button in the create client form.

     Retrieves the input values from the form fields and creates a new client in the database.

     Closes the window upon successful creation.

     @param event The action event triggered by clicking the "Criar Cliente" button.
     */
    @FXML
    void Criar_Cliente(ActionEvent event) {
        int id=0;
        String nome= name_text.getText();
        String email = email_text.getText();
        int Telefone= Integer.parseInt(telefone_text.getText());
        int NIF = Integer.parseInt(NIF_text.getText());
        String morada = Morada_text.getText();
        LocalDate data_nascimento = dataNascimento_Datepicker.getValue();
        int CC= Integer.parseInt(txt_CC.getText());
        int CConducao = Integer.parseInt(txt_CConducao.getText());
        String cliphoto = caminhofoto;
        String Pais = txt_Pais.getText();

        String query = "EXEC InserirCliente @nome = '" + nome + "', @email = '" +email +
                "', @telefone = " + Telefone + ", @NIF = " + NIF +
                ", @morada = '" + morada + "', @data_nascimento = '" + data_nascimento +
                "', @cc = " + CC + ", @cconducao = " + CConducao +
                ", @photo = '" + cliphoto + "' , @pais='"+Pais+"' ;";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            statement.executeUpdate(query);
            Stage stage = (Stage) cancelbtn.getScene().getWindow();
            stage.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     Action handler for the "Importar" button in the create client form.
    Opens a file chooser dialog for selecting an image file to import.
    Copies the selected image file to the destination folder and updates the client's photo.
    @param event The action event triggered by clicking the "Importar" button.
    */
    @FXML
    void Importar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", ".jpg", ".png", ".jpeg"));
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
                cli_photo.setImage(novaImagem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
