package com.example.rentacar.Staff;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * O controlador para a exibição das informações do funcionário.
 */
public class infoFuncionarioController implements Initializable {

    @FXML
    private Button changebutton;

    @FXML
    private Label n_funcionario;

    @FXML
    private Label nome;

    @FXML
    private Label email;

    @FXML
    private ImageView image;

    @FXML
    private Label telefone;

    @FXML
    private Label password;

    @FXML
    private Label nivel_acesso;

    private int receivedID;
    private String caminhofoto;

    /**
     * Manipula o evento de clique no botão de edição do funcionário.
     *
     * @param event O evento de clique.
     * @throws IOException Se ocorrer um erro ao carregar a interface de edição do funcionário.
     */
    @FXML
    void Editarfuncionario(ActionEvent event) throws IOException {
        int id = receivedID;
        EditarStaff(id);
    }

    private void EditarStaff(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/editar_staff_info.fxml"));
        Parent root = loader.load();
        Edit_StaffController targetController = loader.getController();

        targetController.setReceivedId(id);
        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle("Perfil Funcionário");
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**
     * Carrega as informações do funcionário com o ID fornecido.
     *
     * @param id O ID do funcionário.
     */
    public void loadLabelFuncionario(int id) {
        this.receivedID = id;
        String fieldID, fieldNome, fieldEmail, fieldTelefone, fieldNivelAcesso, fieldPassword = null;
        Image fieldImagem;

        String connectQuery = "SELECT id,nome,email,telefone,password,nivel_acesso,Imagem FROM Funcionario WHERE id=" + receivedID;

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(connectQuery);
            if (resultSet.next()) {
                fieldID = resultSet.getString("id");
                fieldNome = resultSet.getString("nome");
                fieldEmail = resultSet.getString("email");
                fieldTelefone = resultSet.getString("telefone");
                fieldPassword = resultSet.getString("password");
                fieldNivelAcesso = resultSet.getString("nivel_acesso");
                if (resultSet.getString("Imagem") != null) {
                    this.caminhofoto = resultSet.getString("Imagem");
                    fieldImagem = new Image("file:" + caminhofoto);
                } else {
                    this.caminhofoto = getClass().getClassLoader().getResource("com/example/rentacar/images/guest.png").toExternalForm();
                    fieldImagem = new Image(caminhofoto);
                }

                n_funcionario.setText(fieldID);
                nome.setText(fieldNome);
                email.setText(fieldEmail);
                telefone.setText(fieldTelefone);
                password.setText(fieldPassword);
                nivel_acesso.setText(fieldNivelAcesso);
                image.setImage(fieldImagem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
