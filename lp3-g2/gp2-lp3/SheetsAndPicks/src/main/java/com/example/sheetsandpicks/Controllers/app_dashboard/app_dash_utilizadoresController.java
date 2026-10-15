package com.example.sheetsandpicks.Controllers.app_dashboard;

import com.example.sheetsandpicks.BL.Database.DBstatements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class app_dash_utilizadoresController{

    @FXML
    private Button btn_Cancelar;

    @FXML
    private Label lblusername;

    @FXML
    private Label lblpassword;

    @FXML
    private Button btn_adicionar;

    @FXML
    private ComboBox<String> cmb_pais;

    @FXML
    private DatePicker dpicker_data;

    @FXML
    private Label lb_titulo;

    @FXML
    private TextField txt_cidade;

    @FXML
    private TextField txt_cod_postal;

    @FXML
    private TextField txt_email;

    @FXML
    private TextField txt_endereco;

    @FXML
    private TextField txt_endereco2;

    @FXML
    private TextField txt_nome;

    @FXML
    private TextField txt_password;

    @FXML
    private TextField txt_telefone;

    @FXML
    private TextField txt_username;

    private String funcionario;

    @FXML
    public void initialize() {

    }

    /**
     * Gera a hash SHA-256 para uma senha fornecida.
     *
     * @param password A senha a ser hashada.
     * @return A representação hexadecimal da hash SHA-256 da senha.
     * @throws NoSuchAlgorithmException Se o algoritmo SHA-256 não estiver disponível.
     */
    public static String hashWithSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception accordingly
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Define as informações do funcionário e realiza a inicialização de elementos da interface gráfica.
     *
     * @param Funcionario O tipo de funcionário (ex: "Clientes").
     * @throws SQLException Se ocorrer uma exceção ao acessar o banco de dados.
     */
    public void setFuncionario(String Funcionario) throws SQLException {
            lb_titulo.setText(Funcionario);
            this.funcionario = Funcionario;
            cmb_pais.setItems(DBstatements.getPais());

            if(funcionario == "Clientes"){
                txt_username.setManaged(false);
                txt_password.setManaged(false);
                lblpassword.setManaged(false);
                lblusername.setManaged(false);
            }
    }


    /**
     * Manipula o evento de adição de um novo funcionário (Operador, Fornecedor, Admin ou Clientes).
     * Coleta os dados dos campos de entrada, chama o método apropriado para criar o funcionário na base de dados
     * e exibe uma mensagem de confirmação.
     *
     * @param event O evento associado à ação de adicionar um novo funcionário.
     * @throws SQLException Se ocorrer uma exceção ao acessar o banco de dados.
     */
    @FXML
    void HandlerAdicionar(ActionEvent event) throws SQLException {
        String Nome = txt_nome.getText();
        String endereco = txt_endereco.getText();
        String endereco2 = txt_endereco2.getText();
        String cidade = txt_cidade.getText();
        String pais = String.valueOf(cmb_pais.getValue());
        int telefone = Integer.parseInt(txt_telefone.getText());
        String email = txt_email.getText();
        String cod_postal = txt_cod_postal.getText();
        LocalDate Data_nascimento = dpicker_data.getValue();
        String username = txt_username.getText();
        String password = hashWithSHA256(txt_password.getText());
        System.out.println(Nome+","+endereco+","+endereco2+","+cidade+","+pais+","+telefone+","+email+","+cod_postal+","+ Data_nascimento +","+username+","+password+"!");

        if(funcionario == "Operador"){
            DBstatements.CriarOperador(Nome,endereco,endereco2,cidade,pais,telefone,email,cod_postal,Data_nascimento,username,password);
            HandlerConfirmado();
        }else if(funcionario=="Fornecedor"){
            DBstatements.CriarFornecedor(Nome,endereco,endereco2,cidade,pais,telefone,email,cod_postal,Data_nascimento,username,password);
            HandlerConfirmado();
        }else if (funcionario=="Admin") {
            DBstatements.CriarAdmin(Nome,endereco,endereco2,cidade,pais,telefone,email,cod_postal,Data_nascimento,username,password);
            HandlerConfirmado();
        }else if (funcionario=="Clientes"){
            DBstatements.CriarCliente(Nome, endereco, endereco2, cidade, pais, telefone, email, cod_postal, Data_nascimento);
            HandlerConfirmado();
        }else {
            System.out.println("ERRO , não reconheço o tipo");
        }
    }

    /**
     * Manipula o evento de cancelamento da criação de um novo funcionário (Operador, Fornecedor, Admin ou Clientes).
     * Exibe uma mensagem de confirmação e fecha a janela de criação caso o usuário confirme a ação.
     *
     * @param event O evento associado à ação de cancelar a criação de um novo funcionário.
     */
    @FXML
    void HandlerCancelar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo sair da Criaço de " + funcionario + " ?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Manipula o evento de confirmação bem-sucedida após a criação de um novo funcionário (Operador, Fornecedor, Admin ou Clientes).
     * Exibe uma mensagem informativa sobre o sucesso da operação e fecha a janela de criação.
     */
    public void HandlerConfirmado() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("SUCESSO");
        a.setContentText("O " + funcionario + " foi adicionado com sucesso");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
                stage.close();
            }
        }
    }
}


