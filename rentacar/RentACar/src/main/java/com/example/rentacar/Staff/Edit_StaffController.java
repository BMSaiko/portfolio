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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * O controlador Edit_StaffController é responsável por gerenciar a edição de um funcionário existente.
 */
public class Edit_StaffController implements Initializable {

    private int id;

    @FXML
    private Button editbtn;

    @FXML
    private Button btn_change;

    @FXML
    private Button cancelbtn;

    @FXML
    private ComboBox<Integer> combo_lvl;

    @FXML
    private TextField email_txt;

    @FXML
    private ImageView image;

    @FXML
    private TextField name_txt;

    @FXML
    private TextField telefone_txt;

    @FXML
    private TextField txt_password;

    private String caminhofoto;

    /**
     * Define o ID do funcionário recebido.
     *
     * @param id O ID do funcionário a ser editado.
     */
    public void setReceivedId(int id) {
        this.id = id;

        String fieldNome, fieldEmail, fieldTelefone, fieldpassword;
        Image fieldimagem;
        int fieldNv_acesso;

        String query = "SELECT * FROM Funcionario WHERE id=" + id + ";";

        Statement statement = null;
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.first()) {
                fieldNome = resultSet.getString("nome");
                fieldEmail = resultSet.getString("email");
                fieldTelefone = resultSet.getString("telefone");
                fieldNv_acesso = resultSet.getInt("nivel_acesso");
                fieldpassword = resultSet.getString("password");
                if (resultSet.getString("Imagem") != null) {
                    this.caminhofoto = resultSet.getString("Imagem");
                    fieldimagem = new Image("file:" + caminhofoto);
                } else {
                    this.caminhofoto = getClass().getClassLoader().getResource("com/example/rentacar/images/guest.png").toExternalForm();
                    fieldimagem = new Image(caminhofoto);
                }

                name_txt.setText(fieldNome);
                email_txt.setText(fieldEmail);
                telefone_txt.setText(fieldTelefone);
                combo_lvl.setValue(fieldNv_acesso);
                txt_password.setText(fieldpassword);
                image.setImage(fieldimagem);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cancela a edição do funcionário.
     *
     * @param event O evento de ação associado ao botão cancelbtn.
     */
    @FXML
    void CancelEdit(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a criação do Funcionário?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) cancelbtn.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Altera a imagem do funcionário.
     *
     * @param event O evento de ação associado ao botão btn_change.
     */
    @FXML
    void Change_Image(ActionEvent event) {
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

    /**
     * Edita as informações do funcionário.
     *
     * @param event O evento de ação associado ao botão editbtn.
     */
    @FXML
    void EditStaff(ActionEvent event) {
        int id = 0;
        String nome = name_txt.getText();
        String email = email_txt.getText();
        int Telefone = Integer.parseInt(telefone_txt.getText());
        int nv_acesso = Integer.parseInt(String.valueOf(combo_lvl.getValue()));
        String imagem = caminhofoto;
        String password = txt_password.getText();
        String query = "UPDATE Staff SET (nome = '" + nome + "',email='" + email + "',Telefone = '" + Telefone + "', nivel_acesso = " + nv_acesso + ", password = '" + password + "' , Imagem = '" + imagem + "') Where id = '" + id + "'";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = null;
            statement = databaseConnection.getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> list = FXCollections.observableArrayList();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        combo_lvl.setItems(list);
    }
}
