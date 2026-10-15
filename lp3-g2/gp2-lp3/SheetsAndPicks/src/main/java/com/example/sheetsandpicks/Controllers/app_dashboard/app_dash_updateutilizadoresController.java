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

public class app_dash_updateutilizadoresController {
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

    public String username;
    public String password;
    public Integer idear;

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
     * Define o tipo de funcionário para a janela de criação e atualiza os elementos da interface de usuário
     * com base no tipo de funcionário especificado.
     *
     * @param Funcionario O tipo de funcionário (Operador, Fornecedor, Admin ou Clientes).
     * @throws SQLException Se ocorrer um erro ao obter informações do banco de dados.
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
     * Manipula a ação de alterar informações do funcionário/cliente e atualiza os dados na base de dados.
     * Exibe uma mensagem de sucesso após a conclusão.
     *
     * @param event O evento de ação associado à ação de alterar.
     * @throws SQLException Se ocorrer um erro ao acessar ou atualizar o banco de dados.
     */
    public void HandlerAlterar(ActionEvent event) throws SQLException {
        String Nome = txt_nome.getText();
        String endereco = txt_endereco.getText();
        String endereco2 = txt_endereco2.getText();
        String cidade = txt_cidade.getText();
        String pais = String.valueOf(cmb_pais.getValue());
        int telefone = Integer.parseInt(txt_telefone.getText());
        String email = txt_email.getText();
        String cod_postal = txt_cod_postal.getText();
        LocalDate Data_nascimento = dpicker_data.getValue();
        if(funcionario == "Clientes"){
            System.out.println(idear);
            DBstatements.Updateclientes(idear, Nome,endereco,endereco2,cidade,pais,telefone,email,cod_postal,Data_nascimento);
            HandlerConfirmado();
        }
        else {
            String username = txt_username.getText();
            String password = hashWithSHA256(txt_password.getText()) ;
            DBstatements.Updateusers(idear, Nome,endereco,endereco2,cidade,pais,telefone,email,cod_postal,Data_nascimento,username,password);
            HandlerConfirmado();
        }

    }

    /**
     * Manipula a ação de cancelar a alteração de informações do funcionário/cliente.
     * Exibe uma mensagem de confirmação antes de fechar a janela de alteração.
     *
     * @param event O evento de ação associado à ação de cancelar.
     */
    @FXML
    void HandlerCancelar(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo sair da Alteração de " + funcionario + " ?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Manipula a confirmação bem-sucedida da alteração de informações do funcionário/cliente.
     * Exibe uma mensagem de sucesso antes de fechar a janela de alteração.
     */
    public void HandlerConfirmado() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("SUCESSO");
        a.setContentText("O " + funcionario + " foi alterado com sucesso");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) btn_Cancelar.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Define os dados do utilizador ou funcionário nos campos de entrada.
     *
     * @param id            O ID do utilizador ou funcionário.
     * @param nome          O nome do utilizador ou funcionário.
     * @param endereco      O endereço do utilizador ou funcionário.
     * @param pais          O país do utilizador ou funcionário.
     * @param endereco2     O segundo endereço do utilizador ou funcionário.
     * @param cidade        A cidade do utilizador ou funcionário.
     * @param telefone      O número de telefone do utilizador ou funcionário.
     * @param email         O endereço de e-mail do utilizador ou funcionário.
     * @param codPostal     O código postal do utilizador ou funcionário.
     * @param dataNascimento A data de nascimento do utilizador ou funcionário no formato "yyyy-MM-dd".
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void setdata(Integer id, String nome, String endereco, String pais,String endereco2, String cidade, Integer telefone, String email, String codPostal, String dataNascimento) throws SQLException {
        idear =id;
        username = DBstatements.getuser(id);
        password = DBstatements.getpass(id);

        txt_password.setText(password);
        txt_username.setText(username);
        txt_nome.setText(nome);
        txt_endereco.setText(endereco);
        txt_endereco2.setText(endereco2);
        txt_cidade.setText(cidade);
        cmb_pais.setValue(pais);
        txt_telefone.setText(String.valueOf(telefone));
        txt_email.setText(email);
        txt_cod_postal.setText(codPostal);
        dpicker_data.setValue(LocalDate.parse(dataNascimento));

    }
}
