package com.example.rentacar.Veiculo;

import com.example.rentacar.Database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Text;

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

public class Editar_VeiculoController implements Initializable {

    String id = "";

    private String caminhofoto;

    @FXML
    private Button Cancelbutton;

    @FXML
    private Button editbtn;
    @FXML
    private TextField cilindrada_txt;
    @FXML
    private TextField cavalos_txt;
    @FXML
    private TextField kms_txt;
    @FXML
    private TextField matricula_txt;
    @FXML
    private TextField ult_revisao_txt;
    @FXML
    private TextField valor_txt;
    @FXML
    private TextField ano_txt;

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
    private ImageView popoimage;

    private int idCombustivel ;
    private int idCategoria;
    private int idLimpeza;
    private int idDisponibilidade;
    private int idSeguro;
    private int idMarca;
    private String nomeMarca;
    private int idModelo;

    @FXML
    private Button importbtn;


    public void setReceivedId(String id, Editar_VeiculoController targetController) {
        this.id = id;

        String fieldMarca, fieldModelo, fieldCilindrada, fieldCavalos, fieldKms, fieldMatricula, fieldUltrevisao, fieldSeguro;
        String fieldCombustivel, fieldCategoria, fieldLimpeza, fieldDisponibilidade;
        Image fieldImagem;
        int fieldValor,fieldAno;


        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            combustivel_cb.getItems().clear();
            String queryCombustivel = "SELECT combustivel FROM tipo_combustivel";
            ResultSet rsCombustivel = statement.executeQuery(queryCombustivel);
            while (rsCombustivel.next()) {
                String combustivel = rsCombustivel.getString("combustivel");
                combustivel_cb.getItems().add(combustivel);
            }

            String queryCombustivelID = "SELECT tc.id FROM tipo_combustivel tc INNER JOIN specs s on tc.id=s.combustivel INNER JOIN Veiculos v on v.id_specs=s.id WHERE v.matricula = '" + id + "'";
            ResultSet rsCombustivelID = statement.executeQuery(queryCombustivelID);
            while (rsCombustivelID.next()){
                idCombustivel = rsCombustivelID.getInt("id");
            }

            categoria_cb.getItems().clear();
            String queryCategoria = "SELECT categoria FROM categoria";
            ResultSet rsCategoria = statement.executeQuery(queryCategoria);
            while (rsCategoria.next()) {
                String categoria = rsCategoria.getString("categoria");
                categoria_cb.getItems().add(categoria);
            }
            String queryCategoriaID = "SELECT c.id FROM categoria c INNER JOIN Veiculos v ON c.id=v.id_categoria WHERE v.matricula = '" + id + "'";
            ResultSet rsCategoriaID = statement.executeQuery(queryCategoriaID);
            while (rsCategoriaID.next()) {
                idCategoria = rsCategoriaID.getInt("id");
            }

            limpeza_cb.getItems().clear();
            String queryLimpeza = "SELECT id,stat as limpeza FROM limpeza";
            ResultSet rsLimpeza = statement.executeQuery(queryLimpeza);
            while (rsLimpeza.next()) {
                String limpeza = rsLimpeza.getString("limpeza");
                limpeza_cb.getItems().add(limpeza);
            }

            String queryLimpezaID = "SELECT l.id FROM limpeza l INNER JOIN Veiculos v on l.id=v.id_limpeza WHERE v.matricula = '" + id+ "'";
            ResultSet rsLimpezaID = statement.executeQuery(queryLimpezaID);
            while (rsLimpezaID.next()) {
                idLimpeza = rsLimpezaID.getInt("id");

            }

            disponibilidade_cb.getItems().clear();
            String queryDisponibilidade = "SELECT disponibilidade FROM Veiculo_disponibilidade";
            ResultSet rsDisponibilidade = statement.executeQuery(queryDisponibilidade);
            while (rsDisponibilidade.next()) {
                String disponibilidade = rsDisponibilidade.getString("disponibilidade");
                disponibilidade_cb.getItems().add(disponibilidade);
            }

            String queryDisponibilidadeID = "SELECT vd.id_disponibilidade FROM Veiculo_disponibilidade vd INNER JOIN Veiculos v on vd.id_disponibilidade=v.id_disponibilidade WHERE v.matricula = '" + id+ "'";
            ResultSet rsDisponibilidadeID = statement.executeQuery(queryDisponibilidadeID);
            while (rsDisponibilidadeID.next()) {
                idDisponibilidade = rsDisponibilidadeID.getInt("id_disponibilidade");

            }

            seguro_cb.getItems().clear();
            String querySeguro = "SELECT id_seguro,nome FROM seguro ";
            ResultSet rsSeguro = statement.executeQuery(querySeguro);
            while (rsSeguro.next()) {
                String seguro = rsSeguro.getString("nome");
                seguro_cb.getItems().add(seguro);
            }

            String querySeguroID = "SELECT s.id_seguro FROM seguro s INNER JOIN Veiculos v on s.id_seguro=v.id_seguro WHERE v.matricula = '" + id+ "'";
            ResultSet rsSeguroID = statement.executeQuery(querySeguroID);
            while (rsSeguroID.next()) {
                idSeguro = rsSeguroID.getInt("id_seguro");
            }

            marca_cb.getItems().clear();
            String queryMarca = "SELECT DISTINCT marca FROM marca";
            ResultSet rsMarca = statement.executeQuery(queryMarca);
            while (rsMarca.next()) {
                String marca = rsMarca.getString("marca");
                marca_cb.getItems().add(marca);
            }
            String queryMarcaID = "SELECT m.id as id,m.marca as marca FROM marca m inner join marca_modelo mm on mm.id=m.id inner join Veiculos v on v.id_marca_modelo=mm.id WHERE v.matricula = '" + id+ "'";
            ResultSet rsMarcaID = statement.executeQuery(queryMarcaID);
            while (rsMarcaID.next()) {
                idMarca = rsMarcaID.getInt("id");
                nomeMarca = rsMarcaID.getString("marca");
            }

            modelo_cb.getItems().clear();
            String queryModelo= "SELECT m.id,m.modelo as modelo FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo INNER JOIN marca ma on ma.id=mm.id_marca WHERE ma.marca = '" + nomeMarca + "'";
            ResultSet rsModelo = statement.executeQuery(queryModelo);
            while (rsModelo.next()) {
                String modelo = rsModelo.getString("modelo");
                modelo_cb.getItems().add(modelo);
            }

            String queryModeloID= "SELECT m.id as id FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo INNER JOIN marca ma on ma.id=mm.id_marca INNER JOIN Veiculos v on v.id_marca_modelo=mm.id WHERE v.matricula = '" + id+ "'";
            ResultSet rsModeloID = statement.executeQuery(queryModeloID);
            while (rsModeloID.next()) {
                idModelo = rsModeloID.getInt("id");
            }

            String query = "SELECT v.matricula as matricula,v.valor_dia as valor ,v.ano as ano, v.kms as kms,v.Imagem as imagem,m.marca as marca, m.id as idmarca,m3.modelo as modelo,m3.id as idmodelo,sp.cilindrada as cilindrada,sp.cavalos as cavalos,tc.combustivel as combustivel,r.revisao_kms as proxrevisaokms,i.ult_inspecao as ultinspecao,l.stat as stat,m2.id as manutencao,s.nome as nome,c.categoria as categoria,vd.disponibilidade as disponibilidade,l.stat as limpeza,s.nome as seguro \n" +
                    "FROM Veiculos as v \n" +
                    "INNER JOIN specs as sp ON v.id_specs=sp.id \n" +
                    "INNER JOIN seguro as s  ON v.id_seguro=s.id_seguro \n" +
                    "INNER JOIN revisao as r ON v.id_revisao=r.id \n" +
                    "INNER JOIN inspecao as i ON v.id_inspecao=i.id \n" +
                    "INNER JOIN limpeza as l ON v.id_limpeza=l.id \n" +
                    "LEFT JOIN manutencao as m2 ON v.id_manutencao=m2.id \n" +
                    "INNER JOIN Veiculo_disponibilidade as vd ON v.id_disponibilidade=vd.id_disponibilidade\n" +
                    "INNER JOIN tipo_combustivel as tc ON sp.combustivel=tc.id \n" +
                    "INNER JOIN categoria as c ON v.id_categoria=c.id \n" +
                    "INNER JOIN marca_modelo mm ON v.id_marca_modelo=mm.id \n" +
                    "INNER JOIN modelo as m3 ON mm.id_modelo=m3.id \n" +
                    "INNER JOIN marca as m ON mm.id_marca=m.id\n" +
                    "where v.matricula = '" + id + "'";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.first()) {
                fieldMarca = resultSet.getString("marca");
                fieldModelo = resultSet.getString("modelo");
                fieldCilindrada = resultSet.getString("cilindrada");
                fieldCavalos = resultSet.getString("cavalos");
                fieldKms=resultSet.getString("kms");
                fieldMatricula=resultSet.getString("matricula");
                fieldUltrevisao=resultSet.getString("proxrevisaokms");
                fieldCombustivel=resultSet.getString("combustivel");
                fieldCategoria=resultSet.getString("categoria");
                fieldLimpeza=resultSet.getString("limpeza");
                fieldDisponibilidade=resultSet.getString("disponibilidade");
                fieldSeguro=resultSet.getString("seguro");
                fieldValor=resultSet.getInt("valor");
                fieldAno=resultSet.getInt("ano");


                if(resultSet.getString("imagem")!=null){
                    this.caminhofoto=resultSet.getString("imagem");
                    fieldImagem = new Image("file:" + caminhofoto);
                }else{
                    this.caminhofoto=getClass().getClassLoader().getResource("/com/example/rentacar/images/carro.png").toExternalForm();
                    fieldImagem = new Image(caminhofoto);
                }

                combustivel_cb.setValue(fieldCombustivel);
                categoria_cb.setValue(fieldCategoria);
                limpeza_cb.setValue(fieldLimpeza);
                disponibilidade_cb.setValue(fieldDisponibilidade);
                seguro_cb.setValue(fieldSeguro);
                marca_cb.setValue(fieldMarca);
                modelo_cb.setValue(fieldModelo);

                cilindrada_txt.setText(fieldCilindrada);
                cavalos_txt.setText(fieldCavalos);
                kms_txt.setText(fieldKms);
                matricula_txt.setText(fieldMatricula);
                ult_revisao_txt.setText(fieldUltrevisao);
                valor_txt.setText(String.valueOf(fieldValor));
                ano_txt.setText(String.valueOf(fieldAno));


                popoimage.setImage(fieldImagem);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @FXML
    void CancelarEdit(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a Edição do Carro?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) Cancelbutton.getScene().getWindow();
                stage.close();
            }
        }
    }

    @FXML
    void EditVeiculoInfo(ActionEvent event) {
        String id = this.id;

        int cilindrada = Integer.parseInt(cilindrada_txt.getText());
        int cavalos = Integer.parseInt(cavalos_txt.getText());
        int kms = Integer.parseInt(kms_txt.getText());
        String matricula = matricula_txt.getText();
        int revisaokms = Integer.parseInt(ult_revisao_txt.getText());
        int valor = Integer.parseInt(valor_txt.getText());
        int ano = Integer.parseInt(ano_txt.getText());
        String query = "exec Upd_Veiculo @id='" + id + "', @marca = "+ idMarca +" , @modelo = " + idModelo +
                ", @cilindrada = '" + cilindrada + " ', @kms = " + kms + " ,@cavalos = " + cavalos + ", " +
                "@matricula = '" + matricula + "' , @revisaokms = " + revisaokms + " , @seguro = " + idSeguro +
                " , @combustivel = " + idCombustivel + " , @categoria = " + idCategoria + " , @limpeza = "
                + idLimpeza + " , @disponibilidade = " + idDisponibilidade + ", @image = '"
                + caminhofoto +"', @ano = '" + ano + "', @valor = '" + valor + "';";

        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            statement.executeUpdate(query);
            Stage stage = (Stage) editbtn.getScene().getWindow();
            stage.close();
            System.out.println("Exec Update");
            System.out.println(idCategoria + " Categoria");
            System.out.println("kms:" + kms);
            System.out.println("cavalos:" + cavalos);
            System.out.println("id:" + id);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    @FXML
    void Importar_imagem(ActionEvent event) {
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM categoria WHERE categoria = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idCategoria = rs.getInt("id");
                        System.out.println("Selected Categoria ID: " + idCategoria);
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
        marca_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedItem = newValue.toString();

                try {
                    DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
                    Statement statement = databaseConnection.getConnection().createStatement();

                    String query = "SELECT id FROM marca WHERE marca = '" + selectedItem + "'";
                    ResultSet rs = statement.executeQuery(query);

                    if (rs.next()) {
                        idMarca = rs.getInt("id");
                        System.out.println("Selected Marca ID: " + idMarca);
                        modelo_cb.getItems().clear();
                        String queryModelo= "SELECT m.id, m.modelo AS modelo FROM modelo m INNER JOIN marca_modelo mm on m.id=mm.id_modelo" +
                                " INNER JOIN marca ma ON ma.id = mm.id_marca WHERE ma.marca = (SELECT marca FROM marca WHERE id ='" + idMarca + "')";
                        ResultSet rsModelo = statement.executeQuery(queryModelo);
                        while (rsModelo.next()) {
                            String modelo = rsModelo.getString("modelo");
                            modelo_cb.getItems().add(modelo);
                        }
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
                        System.out.println("Selected Modelo ID: " + idModelo);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

