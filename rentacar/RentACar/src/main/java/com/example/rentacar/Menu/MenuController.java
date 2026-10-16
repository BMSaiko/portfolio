package com.example.rentacar.Menu;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Singleton.SingletonData;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;


public class MenuController implements Initializable {

    @FXML
    private Label lb_nome;

    @FXML
    private Label alertaVermelho;

    @FXML
    private Pane pane;

    @FXML
    private Button butAlugar;

    @FXML
    private Button butClientes;

    @FXML
    private Button butLogout;

    @FXML
    private Button butMenu;

    @FXML
    private Button butStaff;

    @FXML
    private Button butVeiculos;

    @FXML
    private Button btnSino;

    private Button botaoSelecionado;  // Armazena a referência do botão selecionado anteriormente

    private String username;

    private int nv_permissao;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNv_permissao() {
        return nv_permissao;
    }

    public void setNv_permissao(int nv_permissao) {
        this.nv_permissao = nv_permissao;
    }

    /**
     * Inicializa o controlador após o carregamento do arquivo FXML.
     *
     * @param url            URL do arquivo FXML.
     * @param resourceBundle ResourceBundle associado ao arquivo FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        alterarBotaoSelecionado(butMenu);  // Define o botão "Menu" como selecionado por padrão

        int nivelAcesso = nv_permissao; // Certifique-se de que nv_permissao seja do tipo int

        atualizardados();
        SingletonData.getInstance().setNivelAcesso(nivelAcesso);
        lb_nome.setText(username);

        if (nivelAcesso == 2) {
            butStaff.setVisible(false);
        }

        if (nivelAcesso == 1) {
            butStaff.setVisible(false);
            butVeiculos.setVisible(false);
            //butClientes.setVisible(false);
        }

        carregarConteudo("/com/example/rentacar/app_pane_menu.fxml");
    }

    /**
     * Envia os dados de permissão e nome de usuário para o controlador de destino e
     * inicializa o controlador de destino.
     *
     * @param nv_permissao      O nível de permissão do usuário.
     * @param username          O nome de usuário.
     * @param targetController  O controlador de destino.
     */
    void enviardados(int nv_permissao, String username, MenuController targetController) {
        setNv_permissao(nv_permissao);
        setUsername(username);

        targetController.initialize(null, null);
    }

    /**
     * Abre uma nova janela com o arquivo FXML especificado.
     *
     * @param arquivofxml O caminho para o arquivo FXML.
     * @param Mensagem    A mensagem a ser exibida no título da janela.
     */
    private void Criarcena(String arquivofxml, String Mensagem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivofxml));
            Parent root = loader.load();
            MenuController targetController = loader.getController();

            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle(Mensagem);
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre uma nova janela de alerta com o arquivo FXML especificado.
     *
     * @param arquivofxml O caminho para o arquivo FXML.
     * @param Mensagem    A mensagem a ser exibida no título da janela de alerta.
     */
    private void CriarcenaAlerta(String arquivofxml, String Mensagem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivofxml));
            Parent root = loader.load();

            // Configurar a cena
            Scene scene = new Scene(root);

            // Configurar o palco (janela)
            Stage novaJanela = new Stage();
            novaJanela.setResizable(false);
            novaJanela.setTitle(Mensagem);
            novaJanela.setScene(scene);

            // Exibir a nova janela
            novaJanela.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Altera o estilo visual do botão selecionado.
     *
     * @param novoBotaoSelecionado O novo botão selecionado.
     */
    private void alterarBotaoSelecionado(Button novoBotaoSelecionado) {
        if (botaoSelecionado != null) {
            botaoSelecionado.setFont(Font.font("System", 17));
            botaoSelecionado.setTextFill(Color.web("#000000b2"));
            botaoSelecionado.setStyle("-fx-background-color: transparent; -fx-background-radius: 0;");
        }

        botaoSelecionado = novoBotaoSelecionado;
        botaoSelecionado.setFont(Font.font("System", 17));
        botaoSelecionado.setTextFill(Color.WHITE);
        botaoSelecionado.setStyle("-fx-background-color: #1e868d; -fx-background-radius: 0;");
    }

    /**
     * Executa o logout do usuário, abrindo a janela de login e fechando a janela atual.
     *
     * @param event O evento de ação que disparou o método.
     */
    @FXML
    void logout(ActionEvent event) {
        CriarcenaAlerta("/com/example/rentacar/login.fxml", "login");
        butLogout = (Button) event.getSource();

        // Obtém a referência do palco (Stage) da janela atual
        Stage stage = (Stage) butLogout.getScene().getWindow();

        // Fecha a janela
        stage.close();
    }

    @FXML
    void mudarDashAlugar(ActionEvent event) {
        alterarBotaoSelecionado(butAlugar);
        carregarConteudo("/com/example/rentacar/app_pane_Alugar.fxml");
    }

    @FXML
    void mudarDashClientes(ActionEvent event) {
        alterarBotaoSelecionado(butClientes);
        carregarConteudo("/com/example/rentacar/app_pane_Clientes.fxml");
    }

    @FXML
    void verAlerta(ActionEvent event){
        CriarcenaAlerta("/com/example/rentacar/dash_alerta.fxml","Alerta");
    }

    @FXML
    void mudarDashMenu(ActionEvent event) {
        alterarBotaoSelecionado(butMenu);
        carregarConteudo("/com/example/rentacar/app_pane_menu.fxml");
    }

    @FXML
    void mudarDashStaff(ActionEvent event) {
        alterarBotaoSelecionado(butStaff);
        carregarConteudo("/com/example/rentacar/app_pane_staff.fxml");
    }

    @FXML
    void mudarDashVeiculos(ActionEvent event) {
        alterarBotaoSelecionado(butVeiculos);
        carregarConteudo("/com/example/rentacar/app_pane_veiculos.fxml");
    }

    /**
     * Muda a cor de fundo do botão "Logout" quando o mouse é pressionado.
     *
     * @param event O evento do mouse.
     */
    @FXML
    void mudarCor(MouseEvent event) {
        butLogout.setStyle("-fx-background-color: #1e868d; -fx-text-fill: #FFFFFF; -fx-background-radius: 0;");
    }

    /**
     * Restaura a cor de fundo original do botão "Logout" quando o mouse é liberado.
     *
     * @param event O evento do mouse.
     */
    @FXML
    void tirarCor(MouseEvent event) {
        butLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF0000; -fx-background-radius: 0;");
    }

    /**
     * Atualiza os dados no banco de dados.
     */
    private void atualizardados() {
        String query = "UPDATE Veiculos\n" +
                "SET Veiculos.id_limpeza = 1\n" +
                "FROM Veiculos\n" +
                "INNER JOIN Contratos ON Contratos.matricula = Veiculos.matricula\n" +
                "WHERE CONVERT(DATE, Contratos.data_fim) <= CONVERT(DATE, CURRENT_TIMESTAMP);";
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega o conteúdo de um arquivo FXML para exibição no painel.
     *
     * @param fxmlFile O caminho para o arquivo FXML.
     */
    @FXML
    private void carregarConteudo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node conteudo = loader.load();

            if (fxmlFile.equals("/com/example/rentacar/app_pane_menu.fxml") && nv_permissao != 0) {
                app_pane_menuController targetController2 = loader.getController();
                SingletonData.getInstance().setNivelAcesso(nv_permissao);
                //targetController2.enviaDados(nv_permissao, targetController2);
            }
            pane.getChildren().setAll(conteudo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}