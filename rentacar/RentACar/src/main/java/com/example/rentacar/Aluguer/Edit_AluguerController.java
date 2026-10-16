package com.example.rentacar.Aluguer;

import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Veiculo.Veiculo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class Edit_AluguerController implements Initializable {

    @FXML
    private Button CancelButton;

    @FXML
    private TableColumn<Veiculo,String> c_matricula2;

    @FXML
    private TableColumn<Veiculo,String> c_marca2;

    @FXML
    private TableColumn<Veiculo,String> c_modelo2;

    @FXML
    private Pane Pane;

    @FXML
    private Button butVerImagem;

    @FXML
    private Button but_editar;

    @FXML
    private TableColumn<Veiculo, String> c_categoria;

    @FXML
    private TableColumn<Veiculo, Integer> c_cilindrada;

    @FXML
    private TableColumn<Veiculo, String> c_combustivel;

    @FXML
    private TableColumn<Veiculo, Integer> c_cv;

    @FXML
    private TableColumn<Veiculo, Date> c_inspecao;

    @FXML
    private TableColumn<Veiculo, Integer> c_kms;

    @FXML
    private TableColumn<Veiculo, String> c_limpeza;

    @FXML
    private TableColumn<Veiculo, String> c_marca;

    @FXML
    private TableColumn<Veiculo, String> c_matricula;

    @FXML
    private TableColumn<Veiculo, String> c_modelo;

    @FXML
    private TableColumn<Veiculo, Date> c_revisao;

    @FXML
    private TableColumn<Veiculo, String> c_seguro;

    @FXML
    private TableColumn<Carro_aluguer, LocalDate> c_data_fim;

    @FXML
    private TableColumn<Carro_aluguer, LocalDate> c_data_inicio;

    @FXML
    private TableColumn<Carro_aluguer, Integer> c_valor;

    @FXML
    private TableColumn<Carro_aluguer, String> c_modelo2_aluguer;

    @FXML
    private TableColumn<Carro_aluguer, String> c_valor_aluguer;

    @FXML
    private TableColumn<Carro_aluguer, String> c_marca2_aluguer;

    @FXML
    private TableColumn<Carro_aluguer, LocalDate> c_data_fim_aluguer;

    @FXML
    private TableColumn<Carro_aluguer, LocalDate> c_data_inic_aluguer;

    @FXML
    private VBox combo_EstAluguer;

    @FXML
    private ComboBox<String> combo_Marca;

    @FXML
    private ComboBox<String> combo_Modelo;

    @FXML
    private DatePicker data_fimdate;

    @FXML
    private DatePicker data_iniciodate;

    @FXML
    private ComboBox<String> estado_combox;

    @FXML
    private ImageView imageCarro;

    @FXML
    private Pane mypane;

    @FXML
    private TextField num_empregado_txt;

    @FXML
    private TextField num_txt;

    @FXML
    private Slider sliderValor;

    @FXML
    private TableView<Carro_aluguer> tableVeiculos;

    @FXML
    private TableView<Carro_aluguer> tableVeiculosAluguer;

    @FXML
    private TextField tipo_uso_txt;

    @FXML
    private TextField txtValor;

    @FXML
    private TextField txtVeiculos;

    private int id;

    ArrayList<Carro_aluguer> carrosList = Carro_aluguer.carrosAluguerList;
    ObservableList<Carro_aluguer> observableCarroList;

    ArrayList<Carro_aluguer> veiculList = Carro_aluguer.veiculList;

    ArrayList<Veiculo> veiculoList = Veiculo.veiculoList;


    ObservableList<Carro_aluguer> observableVeiculoList;
    ObservableList<Carro_aluguer> observableVeiculoAluguerList;

    /**
     * Define o ID de receção para o Aluguer e atualiza a interface do utilizador com os dados correspondentes.
     *
     * @param id O ID a definir para o Aluguer.
     */
    public void setReceiveid(int id) {
        this.id = id;

        // Variáveis de estado
        String fieldestado = null;
        int fieldcliente = 0, fieldfuncionario = 0;
        LocalDate fielddata_inic = null, fielddata_fim = null;
        List<Carro_aluguer> veiculList = getVeiculostart();

        // Consulta à base de dados para obter os dados do Aluguer com base no ID
        String query = "exec Editar_Veiculo @id = " + id + ";";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                // Obtenção dos dados do resultado da consulta
                fielddata_inic = resultSet.getDate("data_inic").toLocalDate();
                fielddata_fim = resultSet.getDate("data_fim").toLocalDate();
                fieldcliente = resultSet.getInt("cliente");
                fieldfuncionario = resultSet.getInt("funcionario");
                fieldestado = resultSet.getString("estado");
            }

            // Atualização da interface do utilizador com os dados obtidos
            data_iniciodate.setValue(fielddata_inic);
            data_fimdate.setValue(fielddata_fim);
            num_txt.setText(String.valueOf(fieldcliente));
            num_empregado_txt.setText(String.valueOf(fieldfuncionario));
            estado_combox.setValue(fieldestado);

            c_marca2_aluguer.setCellValueFactory(new PropertyValueFactory<>("Marca"));
            c_modelo2_aluguer.setCellValueFactory(new PropertyValueFactory<>("Modelo"));
            c_data_inic_aluguer.setCellValueFactory(new PropertyValueFactory<>("Data_inic"));
            c_data_fim_aluguer.setCellValueFactory(new PropertyValueFactory<>("Data_fim"));
            c_valor_aluguer.setCellValueFactory(new PropertyValueFactory<>("Valor"));

            tableVeiculosAluguer.setItems(FXCollections.observableList(veiculList));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initialize(null, null);
    }

    /**
     * Obtém uma lista de objetos Carro_aluguer da base de dados.
     *
     * @return A lista de objetos Carro_aluguer.
     */
    private ArrayList<Carro_aluguer> getVeiculostart() {
        String fieldmatricula, fieldmarca, fieldmodelo, fieldcombustivel, fieldstat, fieldcategoria, fieldnome, fielddisponibilidade;
        int fieldkms, fieldcilindrada, fieldcv, fieldvalor;
        LocalDate fieldprox_revisao, fieldult_inspecao, fielddata_inic, fielddata_fim;
        String query = "exec Editar_Veiculo @id = " + id + ";";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                fieldmatricula = resultSet.getString("matricula");
                fieldkms = resultSet.getInt("kms");
                fieldmarca = resultSet.getString("marca");
                fieldmodelo = resultSet.getString("modelo");
                fieldcilindrada = resultSet.getInt("cilindrada");
                fieldcv = resultSet.getInt("cavalos");
                fieldcombustivel = resultSet.getString("combustivel");
                fieldprox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                fieldult_inspecao = resultSet.getDate("ult_inspecao").toLocalDate();
                fieldstat = resultSet.getString("stat");
                fieldnome = resultSet.getString("nome");
                fielddata_inic = resultSet.getDate("data_inic").toLocalDate();
                fielddata_fim = resultSet.getDate("data_fim").toLocalDate();
                fieldcategoria = resultSet.getString("categoria");
                fieldvalor = resultSet.getInt("valor_dia");
                int dif = (int) ChronoUnit.DAYS.between(fielddata_inic, fielddata_fim);
                int v_orcamento = (fieldvalor * dif);

                Carro_aluguer carro1 = new Carro_aluguer(fieldmatricula, fieldmarca, fieldmodelo, fieldcilindrada, fieldcv, fieldcombustivel, fieldkms, fieldprox_revisao, fieldult_inspecao, fieldstat, fieldcategoria, fieldnome, fielddata_inic, fielddata_fim, v_orcamento);
                observableCarroList = FXCollections.observableList(carrosList);
                //carrosList.add(carro1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carrosList;
    }

    /**
     * Lida com a ação do botão "Cancelar", exibindo uma caixa de diálogo de confirmação e fechando a janela se confirmado.
     *
     * @param event O evento que aciona a ação.
     */
    @FXML
    void HandleCancelButton(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a edição do Aluguer?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) CancelButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Obtém uma lista de objetos Carro_aluguer da base de dados com base numa data especificada.
     *
     * @param data A data para filtrar os dados.
     * @return A lista de objetos Carro_aluguer.
     */
    public List<Carro_aluguer> getdata(LocalDate data) {
        String query = "exec filtro_data @data_inic='" + data + "';";

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String matricula = resultSet.getString("matricula");
                String marca = resultSet.getString("marca");
                String modelo = resultSet.getString("modelo");
                int cilindrada = resultSet.getInt("cilindrada");
                int cv = resultSet.getInt("cavalos");
                String combustivel = resultSet.getString("combustivel");
                int kms = resultSet.getInt("kms");
                LocalDate prox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                LocalDate ult_inspecao = resultSet.getDate("ult_inspecao").toLocalDate();
                String limpeza = resultSet.getString("stat");
                String categoria = resultSet.getString("categoria");
                String seguro = resultSet.getString("nome");
                LocalDate data_inic = null;
                LocalDate data_fim = null;
                int valor = resultSet.getInt("valor_dia");

                Carro_aluguer veiculo1 = new Carro_aluguer(matricula, marca, modelo, cilindrada, cv, combustivel, kms, prox_revisao, ult_inspecao, limpeza, categoria, seguro, data_inic, data_fim, valor);
                observableVeiculoList = FXCollections.observableList(veiculList);
                veiculList.add(veiculo1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculList;
    }

    /**
     * Lida com o evento quando a data é alterada e atualiza a tabela com os dados filtrados.
     *
     * @param event O evento que aciona a ação.
     */

    @FXML
    void OnDataChange(ActionEvent event) {
        tableVeiculos.getItems().clear();
        LocalDate data = data_iniciodate.getValue();
        List<Carro_aluguer> veiculList = getdata(data);

        c_matricula.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("matricula"));
        c_marca.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("marca"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("modelo"));
        c_cilindrada.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cilindrada"));
        c_cv.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cv"));
        c_combustivel.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("combustivel"));
        c_kms.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("kms"));
        c_revisao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("prox_revisao"));
        c_inspecao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("ult_inspecao"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("limpeza"));
        c_categoria.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("categoria"));
        c_seguro.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("seguro"));

        tableVeiculos.setItems(FXCollections.observableList(veiculList));
    }


    /**
     * Obtém uma lista de objetos Carro_aluguer da base de dados com base na data selecionada e preenche a tabela.
     *
     * @return A lista de objetos Carro_aluguer.
     */
    public List<Carro_aluguer> getVeiculo(){
        String query = "exec filtro_data @data_inic='"+data_iniciodate.getValue()+"';";

        try{
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                String matricula = resultSet.getString("matricula");
                String marca = resultSet.getString("marca");
                String modelo = resultSet.getString("modelo");
                int cilindrada = resultSet.getInt("cilindrada");
                int cv = resultSet.getInt("cavalos");
                String combustivel = resultSet.getString("combustivel");
                int kms = resultSet.getInt("kms");
                LocalDate prox_revisao = resultSet.getDate("prox_revisao").toLocalDate();
                LocalDate ult_inspecao = resultSet.getDate("ult_inspecao").toLocalDate();;
                String limpeza = resultSet.getString("stat");
                int id_manutencao = resultSet.getInt("id_manutencao");
                String disponibilidade = resultSet.getString("disponibilidade");
                String categoria = resultSet.getString("categoria");
                String seguro = resultSet.getString("nome");
                LocalDate data_inic = null;
                LocalDate data_fim = null;
                int valor = resultSet.getInt("valor_dia");

                Carro_aluguer veiculo1 = new Carro_aluguer(matricula,marca,modelo,cilindrada,cv,combustivel,kms,prox_revisao,ult_inspecao,limpeza,categoria,seguro,data_inic,data_fim,valor);
                observableVeiculoList  = FXCollections.observableList(veiculList);
                veiculList.add(veiculo1);

            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return veiculList;

    }

    /**
     * Preenche a tabela com os dados obtidos da base de dados.
     */
    public void tablePopulate(){
        List<Carro_aluguer> veiculList = getVeiculo();

        c_matricula.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("matricula"));
        c_marca.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("marca"));
        c_modelo.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("modelo"));
        c_cilindrada.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cilindrada"));
        c_cv.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("cv"));
        c_combustivel.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("combustivel"));
        c_kms.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("kms"));
        c_revisao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("prox_revisao"));
        c_inspecao.setCellValueFactory(new PropertyValueFactory<Veiculo, Date>("ult_inspecao"));
        c_limpeza.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("limpeza"));
        c_categoria.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("categoria"));
        c_seguro.setCellValueFactory(new PropertyValueFactory<Veiculo, String>("seguro"));

        tableVeiculos.setItems(FXCollections.observableList(veiculList));
    }

    /**

     Lida com o evento quando o botão "editar" é clicado.

     Atualiza as informações de aluguel na base de dados com base nos valores selecionados e fecha a janela.

     @param event O evento acionado ao clicar no botão.
     */
    @FXML
    void editar(ActionEvent event) {
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            int num_cliente = Integer.parseInt(num_txt.getText());
            int num_funcionario = Integer.parseInt(num_empregado_txt.getText());
            int estado_aluguer = estado_combox.getSelectionModel().getSelectedIndex();
            estado_aluguer +=1;
            ArrayList<Integer> valores = null;
            ArrayList<String> matriculas = getSecondTable();
            String tipoUso = tipo_uso_txt.getText();
            int v_orcamento = 0;

            ObservableList<Carro_aluguer> veiculosAluguer = tableVeiculosAluguer.getItems();
            for(Carro_aluguer tab : veiculosAluguer){
                v_orcamento += c_valor.getCellData(tab);
            }
            String query ="EXEC Atualizar_aluguer " +
                    "   @v_orcamento = " + v_orcamento + ", " +
                    "   @estado_orcamento = 1, " +
                    "   @id_Cliente = " + num_cliente + ", " +
                    "   @id_funcionario = " + num_funcionario + "," +
                    "   @estado_aluguer = "+estado_aluguer+"," +
                    "   @id = "+id+ ";";

            try {
                statement.executeUpdate(query);
            }catch (SQLException e){
                e.printStackTrace();
            }

            int i=0;
            for(String matricula : matriculas) {
                LocalDate data_inic = c_data_inicio.getCellData(tableVeiculosAluguer.getItems().get(i));
                LocalDate data_fim = c_data_fim.getCellData(tableVeiculosAluguer.getItems().get(i));
                String query2 = "EXEC Alugar_carros2\n" +
                        "    @data_inic = '" + data_inic + "',\n" +
                        "    @data_fim = '" + data_fim + "',\n" +
                        "    @matricula = '" + matricula + "',\n" +
                        "    @id_orcamento = "+id+";";
                try{
                    statement.executeUpdate(query2);
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        /*for (Carro_aluguer veiculo : veiculosAluguer) {
            // Enviar o veículo para OrcamentosController
        }*/

            Stage stage = (Stage) but_editar.getScene().getWindow();
            stage.close();
        }catch(SQLException e ){
            e.printStackTrace();
        }
    }

    @FXML
    void moverSlider(MouseEvent event) {

    }

    /**

     Obtém a lista de matrículas de carros da segunda tabela na interface gráfica.

     @return A lista de matrículas de carros.
     */
    private ArrayList<String> getSecondTable(){

        ObservableList<Carro_aluguer> listadematriculas=tableVeiculosAluguer.getItems();
        ArrayList<String> matriculas = new ArrayList<>();

        for(Carro_aluguer veiculo : listadematriculas){
            String matricula = veiculo.getMatricula();
            matriculas.add(matricula);
        }
        return matriculas;
    }

    /**

     Obtém os IDs dos modelos da base de dados com base na marca selecionada.

     @param selectedMarca A marca selecionada.

     @return A lista de IDs dos modelos.
     */
    private ArrayList<Integer> getModeloIdFromDatabase(String selectedMarca) {
        ArrayList<Integer> modeloId = new ArrayList<>();

        String query="SELECT id_modelo FROM marca_modelo INNER JOIN marca ON marca.id=marca_modelo.id_marca WHERE marca.marca ='"+selectedMarca+"';";

        try {
            // Estabelecer uma conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Ler o resultado da consulta
            while(resultSet.next()) {
                modeloId.add(resultSet.getInt("id_modelo"));
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modeloId;
    }

    /**

     Obtém o modelo da base de dados com base no ID do modelo fornecido.

     @param modeloId O ID do modelo.

     @return A ObservableList com os nomes dos modelos.
     */
    private ObservableList<String> getmodeloFromDatabase(Integer modeloId) {
        ObservableList<String> modelo = FXCollections.observableArrayList();

        try {
            // Estabelecer uma conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            PreparedStatement statement = databaseConnection.getConnection().prepareStatement(
                    "SELECT DISTINCT modelo FROM modelo WHERE id = ?"
            );
            statement.setString(1, String.valueOf(modeloId));

            ResultSet resultSet = statement.executeQuery();

            // Ler o resultado da consulta e adicionar os valores à ObservableList
            while (resultSet.next()) {
                String mod = resultSet.getString("modelo");
                modelo.add(mod);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modelo;
    }


    /**
     * Trata o evento quando a marca selecionada no combo box é alterada.
     * Recupera os IDs de modelo correspondentes do banco de dados e atualiza o combo box de modelos.
     *
     * @param event O evento de ação acionado pela seleção da marca.
     */
    @FXML
    void onmarcachange(ActionEvent event) {
        String selectedMarca = combo_Marca.getValue(); // Get the selected marca
        ArrayList<Integer> modeloIds = getModeloIdFromDatabase(selectedMarca);
        combo_Modelo.setDisable(false);
        // Limpar itens anteriores
        combo_Modelo.getItems().clear();

        for (Integer modeloId : modeloIds) {
            combo_Modelo.getItems().addAll(getmodeloFromDatabase(modeloId));
        }
        FilteredList<Carro_aluguer> filteredList = new FilteredList<>(observableVeiculoList, b -> true);
        combo_Modelo.valueProperty().addListener((observable , oldValue , newValue )->{
            filteredList.setPredicate(veiculo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().contains(palavra)
                        || veiculo.getMarca().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getKms()).contains(palavra)
                        || veiculo.getSeguro().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getProx_revisao()).contains(palavra)
                        || String.valueOf(veiculo.getUlt_inspecao()).contains(palavra)
                        || veiculo.getLimpeza().toLowerCase().contains(palavra)
                        || veiculo.getCategoria().toLowerCase().contains(palavra)
                        || veiculo.getModelo().toLowerCase().contains(palavra);
            });
        });

        SortedList<Carro_aluguer> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableVeiculos.comparatorProperty());
        tableVeiculos.setItems(sortedData);
    }

    /**
     * Trata o evento quando o modelo selecionado no combo box é alterado.
     *
     * @param event O evento de ação acionado pela seleção do modelo.
     */
    @FXML
    void onmodelochange(ActionEvent event) {
        String selectedMarca = combo_Marca.getValue(); // Get the selected marca
        String selectedModelo = combo_Modelo.getValue();
    }

    /**
     * Trata o evento quando uma tecla é pressionada no campo de texto.
     * Limpa a marca selecionada no combo box e aciona a busca por veículos.
     *
     * @param event O evento de tecla acionado ao pressionar uma tecla no campo de texto.
     */
    @FXML
    void textboxPress(KeyEvent event) {
        combo_Marca.getSelectionModel().clearSelection();
        procurarVeiculos();
    }

    /**
     * Realiza a busca por veículos com base no texto inserido no campo de texto.
     */
    @FXML
    void procurarVeiculos() {
        tableVeiculos.setItems(observableVeiculoList);

        FilteredList<Carro_aluguer> filteredList =  new FilteredList<>(observableVeiculoList, b -> true);
        txtVeiculos.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(veiculo -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().contains(palavra)
                        || veiculo.getMarca().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getKms()).contains(palavra)
                        || veiculo.getSeguro().toLowerCase().contains(palavra)
                        || String.valueOf(veiculo.getProx_revisao()).contains(palavra)
                        || String.valueOf(veiculo.getUlt_inspecao()).contains(palavra)
                        || veiculo.getLimpeza().toLowerCase().contains(palavra)
                        || veiculo.getCategoria().toLowerCase().contains(palavra)
                        || veiculo.getModelo().toLowerCase().contains(palavra);
            });

        });
        SortedList<Carro_aluguer> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableVeiculos.comparatorProperty());
        tableVeiculos.setItems(sortedData);
    }


    /**
     * Trata o evento quando o botão "Ver Imagem" é clicado.
     *
     * @param event O evento de ação acionado ao clicar no botão.
     */
    @FXML
    void verImagem(ActionEvent event) {

    }

    /**
     * Recupera os estados de aluguel do banco de dados.
     *
     * @return O ObservableList de estados de aluguel.
     */
    private ObservableList<String> getEstadosAluguerFromDatabase() {
        ObservableList<String> estados = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT estado FROM estado_aluguer";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String estado = resultSet.getString("estado");
                estados.add(estado);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estados;
    }

    /**
     * Recupera o valor mínimo do banco de dados.
     *
     * @param connection A conexão com o banco de dados.
     * @return O valor mínimo.
     * @throws SQLException se ocorrer um erro de acesso ao banco de dados.
     */

    private int getMinValueFromDatabase(Connection connection) throws SQLException {
        int minValue = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MIN(valor_dia) AS minimo FROM Veiculos;")) {

            if (resultSet.next()) {
                minValue = resultSet.getInt("minimo");
            }
        }

        return minValue;
    }

    /**
     * Recupera o valor máximo do banco de dados.
     *
     * @param connection A conexão com o banco de dados.
     * @return O valor máximo.
     * @throws SQLException se ocorrer um erro de acesso ao banco de dados.
     */
    // Método para buscar o valor máximo do banco de dados
    private int getMaxValueFromDatabase(Connection connection) throws SQLException {
        int maxValue = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT MAX(valor_dia) AS maximo FROM Veiculos;")) {

            if (resultSet.next()) {
                maxValue = resultSet.getInt("maximo");
            }
        }

        return maxValue;
    }

    /**
     * Recupera as marcas de carros da base de dados.
     *
     * @return O ObservableList de marcas de carros.
     */
    private ObservableList<String> getmarcaFromDatabase() {
        ObservableList<String> marca = FXCollections.observableArrayList();

        try {
            // Estabelecer a conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            String query = "SELECT marca FROM marca";
            ResultSet resultSet = statement.executeQuery(query);

            // Ler os resultados da consulta e adicionar os valores ao ObservableList
            while (resultSet.next()) {
                String mar = resultSet.getString("marca");
                marca.add(mar);
            }

            // Fechar a conexão e liberar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return marca;
    }

    /**
     * Define o evento para selecionar uma linha na tabela.
     */
    private void selectRow() {
        tableVeiculos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Carro_aluguer selectedVeiculo = tableVeiculos.getSelectionModel().getSelectedItem();
                if (selectedVeiculo != null) {
                    setSecondTable(selectedVeiculo);
                }
            }
        });
        tableVeiculosAluguer.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Carro_aluguer selectedVeiculo = tableVeiculosAluguer.getSelectionModel().getSelectedItem();
                if (selectedVeiculo !=null){
                    setFirstTable(selectedVeiculo);
                }
            }
        });
    }

    /**
     Inicializa o controlador.

     @param location O local usado para resolver caminhos relativos para o objeto raiz, ou null se o local não for conhecido.

     @param resources Os recursos usados para localizar o objeto raiz, ou null se o objeto raiz não foi localizado.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combo_Marca.setItems(getmarcaFromDatabase());
        estado_combox.setItems(getEstadosAluguerFromDatabase());
        observableVeiculoList = FXCollections.observableArrayList(veiculList);
        observableVeiculoAluguerList = FXCollections.observableArrayList(carrosList);
        combo_Modelo.setDisable(true);

        //Veiculo.veiculoList.removeAll(veiculoList);
        System.out.println(data_iniciodate);
        if(data_iniciodate.getValue()!=null) {
            tablePopulate();
            selectRow();
            tableVeiculos.setPlaceholder(new Label("Não foram encontrados resultados."));
            tableVeiculos.setItems(observableVeiculoList);
            tableVeiculosAluguer.setPlaceholder(new Label("Não foram encontrados resultados."));
        }else{
            Carro_aluguer.carrosAluguerList.removeAll(carrosList);
        }
    }

    /**

     Define a segunda tabela com o veículo selecionado.

     @param selectedVeiculo O veículo selecionado.
     */
    private void setSecondTable(Carro_aluguer selectedVeiculo) {

        if (observableVeiculoAluguerList == null) {
            observableVeiculoAluguerList = FXCollections.observableArrayList();
            tableVeiculosAluguer.setItems(observableVeiculoAluguerList);

            c_matricula2.setCellValueFactory(new PropertyValueFactory<>("matricula"));
            c_marca2.setCellValueFactory(new PropertyValueFactory<>("marca"));
            c_modelo2.setCellValueFactory(new PropertyValueFactory<>("modelo"));
            c_data_inicio.setCellValueFactory(cellDate -> new SimpleObjectProperty<>(data_iniciodate.getValue()));
            c_data_fim.setCellValueFactory(cellDate -> new SimpleObjectProperty<>(data_fimdate.getValue()));
        }

        tableVeiculosAluguer.setItems(observableVeiculoAluguerList);
        observableVeiculoAluguerList.add(selectedVeiculo);
        tableVeiculos.getItems().remove(selectedVeiculo);
    }

    /**

     Define a primeira tabela com o veículo selecionado.
     @param selectedVeiculo O veículo selecionado.
     */
    private void setFirstTable(Carro_aluguer selectedVeiculo){
        observableVeiculoList.add(selectedVeiculo);
        tableVeiculosAluguer.getItems().remove(selectedVeiculo);
    }

}
