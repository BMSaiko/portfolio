package com.example.rentacar.Veiculo;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Criar_VeiculoController {

    @FXML
    private Button buttonCancelar;

    @FXML
    private Button buttonCriar;

    @FXML
    private Button buttonImportar;

    @FXML
    private ComboBox combustivel_cb;
    @FXML
    private ComboBox categoria_cb;
    @FXML
    private ComboBox limpeza_cb;
    @FXML
    private ComboBox disponibilidade_cb;
    @FXML
    private ComboBox seguro_cb;
    @FXML
    private ComboBox marca_cb;
    @FXML
    private ComboBox modelo_cb;

    @FXML
    private DatePicker dateinspecao;

    @FXML
    private DatePicker daterevisao;

    @FXML
    private TextField txtCavalos;

    @FXML
    private TextField txtCilindrada;

    @FXML
    private TextField txt_kms;

    @FXML
    private TextField txt_matricula;

    @FXML
    private TextField txt_revisaokms;

    @FXML
    private TextField txt_valor;

    @FXML
    private ImageView popoimage;

    private int idCombustivel ;
    private int idCategoria;
    private int idLimpeza;
    private int idDisponibilidade;
    private int idSeguro;
    private int idMarca;
    private int idModelo;


    private String caminhofoto;
    @FXML
    private void initialize() {
        // Chamado quando a interface gráfica é inicializada
        modelo_cb.setDisable(true);

        // Inicialize o ComboBox com os valores do estado do aluguer procurados na base de dados.
        combustivel_cb.setItems(getCombustivelFromDatabase());
        limpeza_cb.setItems(getLimpezaFromDatabase());
        disponibilidade_cb.setItems(getDisponibilidadeFromDatabase());
        categoria_cb.setItems(getCategoriaFromDatabase());
        seguro_cb.setItems(getSeguroFromDatabase());
        marca_cb.setItems(getMarcaFromDatabase());
        modelo_cb.setItems(getModeloFromDatabase());
        marca_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();
                System.out.println("Selected Marca: " + selectedItem);
                modelo_cb.setDisable(false);
                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM marca WHERE marca = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idMarca = rs.getInt("id");
                        System.out.println(idMarca);
                        updateModelComboBox();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        modelo_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM modelo WHERE modelo = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idModelo = rs.getInt("id");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        combustivel_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM tipo_combustivel WHERE combustivel = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idCombustivel = rs.getInt("id");
                        System.out.println("Selected Combustivel ID: " + idCombustivel);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        categoria_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();
                System.out.println("Selected Categoria: " + selectedItem);
                modelo_cb.setDisable(false);
                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM categoria WHERE categoria = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idCategoria = rs.getInt("id");
                        System.out.println("Selected Categoria ID: " + idCategoria);
                        updateModelComboBox();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        limpeza_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM limpeza WHERE stat = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idLimpeza= rs.getInt("id");
                        System.out.println("Selected Limpeza ID: " + idLimpeza);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        disponibilidade_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id_disponibilidade FROM Veiculo_disponibilidade WHERE disponibilidade = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idDisponibilidade = rs.getInt("id_disponibilidade");
                        System.out.println("Selected Disponibilidade ID: " + idDisponibilidade);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        seguro_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id_seguro FROM seguro WHERE nome = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idSeguro = rs.getInt("id_seguro");
                        System.out.println("Selected Seguro ID: " + idSeguro);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        txt_matricula.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                return;
            }

            newValue = newValue.replaceAll("[^A-Za-z0-9-]", "").toUpperCase();

            if (!newValue.matches("[A-Z0-9]{0,2}(-[A-Z0-9]{0,2}){0,2}")) {
                txt_matricula.setText(oldValue);
            }

            if (newValue.length() == 3 && oldValue.length() == 2 && !newValue.endsWith("-")) {
                txt_matricula.setText(oldValue + "-" + newValue.charAt(2));
            } else if (newValue.length() == 6 && oldValue.length() == 5 && !newValue.endsWith("--")) {
                txt_matricula.setText(oldValue + "-" + newValue.charAt(5));
            }

        });
    }

    private void updateModelComboBox(){
        modelo_cb.getItems().clear();
        System.out.println("oi");

        String selectedMarca = null, selectedCategory=null;
        if (marca_cb.getSelectionModel().getSelectedItem()!=null){
            selectedMarca = marca_cb.getSelectionModel().getSelectedItem().toString();
            System.out.println(selectedMarca);
        }
        if (categoria_cb.getSelectionModel().getSelectedItem()!=null){
            selectedCategory = categoria_cb.getSelectionModel().getSelectedItem().toString();
            System.out.println(selectedCategory);
        }

        if (selectedCategory==null & selectedMarca!=null){
            try {
                System.out.println("only marca");
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();

                String queryModelo= "SELECT DISTINCT m.id, m.modelo AS modelo FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo" +
                        " INNER JOIN marca ma ON ma.id = mm.id_marca WHERE ma.marca = (SELECT marca FROM marca WHERE marca ='" + selectedMarca + "')";
                ResultSet rsModelo = statement.executeQuery(queryModelo);

                if (rsModelo.next()) {
                    String modelo = rsModelo.getString("modelo");
                    modelo_cb.getItems().add(modelo);
                }
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (selectedCategory!=null & selectedMarca==null){
            try {
                System.out.println("only categoria");
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();

                String queryModelo= "SELECT DISTINCT m.id, m.modelo AS modelo FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo" +
                        " INNER JOIN marca ma ON ma.id = mm.id_marca INNER JOIN categoria c on c.id=m.categoria WHERE c.categoria ='"+ selectedCategory +"'";
                ResultSet rsModelo = statement.executeQuery(queryModelo);

                if (rsModelo.next()) {
                    String modelo = rsModelo.getString("modelo");
                    modelo_cb.getItems().add(modelo);
                }
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(selectedCategory!=null & selectedMarca!=null){
            try {
                System.out.println("marca&categoria");
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                Statement statement = databaseConnection.getConnection().createStatement();

                String queryModelo= "SELECT DISTINCT m.id, m.modelo AS modelo FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo" +
                        " INNER JOIN marca ma ON ma.id = mm.id_marca INNER JOIN categoria c on m.categoria = c.id " +
                        "WHERE c.categoria ='"+ selectedCategory +"' and ma.marca='" + selectedMarca+"'";
                ResultSet rsModelo = statement.executeQuery(queryModelo);

                    if (rsModelo.next()) {
                    String modelo = rsModelo.getString("modelo");
                    modelo_cb.getItems().add(modelo);
                }
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private ObservableList<String> getModeloFromDatabase() {
        ObservableList<String> modelo = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT modelo FROM modelo";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String model = resultSet.getString("modelo");
                modelo.add(model);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modelo;
    }

    private ObservableList<String> getMarcaFromDatabase() {
        ObservableList<String> marca = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT marca FROM marca";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String marc = resultSet.getString("marca");
                marca.add(marc);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marca;
    }

    private ObservableList<String> getSeguroFromDatabase() {
        ObservableList<String> seguro = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT nome FROM seguro";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String segur = resultSet.getString("nome");
                seguro.add(segur);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seguro;
    }
    private ObservableList<String> getCombustivelFromDatabase() {
        ObservableList<String> combustivel = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT combustivel FROM tipo_combustivel";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String combusti = resultSet.getString("combustivel");
                combustivel.add(combusti);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return combustivel;
    }

    private ObservableList<String> getLimpezaFromDatabase() {
        ObservableList<String> limpeza = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT stat FROM limpeza";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String limp = resultSet.getString("stat");
                limpeza.add(limp);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return limpeza;
    }

    private ObservableList<String> getDisponibilidadeFromDatabase() {
        ObservableList<String> disponivel = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT disponibilidade FROM Veiculo_disponibilidade";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String dispo = resultSet.getString("disponibilidade");
                disponivel.add(dispo);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return disponivel;
    }

    private ObservableList<String> getCategoriaFromDatabase() {
        ObservableList<String> categoria = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT categoria FROM categoria";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String categ = resultSet.getString("categoria");
                categoria.add(categ);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoria;
    }

    @FXML
    void ImportarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            File destino = new File("resources/com/example/rentacar/images/Veiculos");
            try {
                Path origemPath = selectedFile.toPath();
                Path destinoPath = destino.toPath().resolve(selectedFile.getName());
                Files.createDirectories(destinoPath.getParent());
                Files.copy(origemPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
                this.caminhofoto = destinoPath.toString();
                Image novaImagem = new Image(destinoPath.toUri().toString());
                popoimage.setImage(novaImagem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void adicionarVeiculo(ActionEvent event) {

        System.out.println("addteste");
        int cilindrada = Integer.parseInt(txtCilindrada.getText());
        int cavalos = Integer.parseInt(txtCavalos.getText());
        int kms = Integer.parseInt(txt_kms.getText());
        String matricula = txt_matricula.getText();
        int valor = Integer.parseInt(txt_valor.getText());
        LocalDate revisaoDate = daterevisao.getValue();
        LocalDate inspecaoDate = dateinspecao.getValue();
        int revisaokms = Integer.parseInt(txt_revisaokms.getText());

        String query = "exec InserirVeiculo @marca='" + idMarca + "', @modelo='"+idModelo+"', @cilindrada ='"+cilindrada+"',@kms='"+kms+"',@cavalos='"+cavalos+"',@matricula='"+matricula+"',@revisaokms='"+revisaokms+"',@combustivel='"+idCombustivel+"',@categoria='"+idCategoria+"',@limpeza='"+idLimpeza+"',@disponibilidade='"+idDisponibilidade+"',@seguro='"+idSeguro+"',@proxrevisao='"+revisaoDate+"',@proxinspecao='"+inspecaoDate+"',@valor='"+valor+"',@image='"+"ola"+"';";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();

            statement.executeUpdate(query);

            System.out.println("Veiculo adicionado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erro ao adicionar Veiculo á base de dados: " + e.getMessage());
        }
    }

}



