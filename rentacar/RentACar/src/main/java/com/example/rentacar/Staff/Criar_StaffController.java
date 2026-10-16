package com.example.rentacar.Staff;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * O controlador Criar_StaffController é responsável por gerenciar a criação de um novo funcionário.
 */
public class Criar_StaffController implements Initializable {

    @FXML
    private TextField NIF_txt;

    @FXML
    private ImageView image;

    @FXML
    private ComboBox<Integer> combo;

    @FXML
    private Button cancelbtn;

    @FXML
    private Button importbtn;

    @FXML
    private Button criarbtn;

    @FXML
    private TextField email_txt;

    @FXML
    private TextField name_txt;

    @FXML
    private TextField telefone_txt;

    @FXML
    private TextField txt_password;

    private String caminhofoto;

    /**
     * Cancela a criação do funcionário.
     *
     * @param event O evento de ação associado ao botão cancelbtn.
     */
    @FXML
    void Cancelcreate(ActionEvent event) {
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
     * Cria um novo funcionário.
     *
     * @param event O evento de ação associado ao botão criarbtn.
     */
    @FXML
    void Createstaff(ActionEvent event) {
        String name = name_txt.getText();
        String email = email_txt.getText();
        int Telefone = Integer.parseInt(telefone_txt.getText());
        int nv_acess = combo.getValue();
        String password = txt_password.getText();
        String cli_photo = caminhofoto;

        String query = "EXEC InserirStaff @name='" + name + "', @email='" + email
                + "', @telefone='" + Telefone + "', @nv_acess='" + nv_acess
                + "', @password = '" + password + "' , @imagem = '" + cli_photo + "' ; ";
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
     * Importa uma imagem para ser associada ao funcionário.
     *
     * @param event O evento de ação associado ao botão importbtn.
     */
    @FXML
    void ImportarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            File destino = new File("resources/com/example/rentacar/images/funcionarios");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> list = FXCollections.observableArrayList();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        combo.setItems(list);
    }
}
