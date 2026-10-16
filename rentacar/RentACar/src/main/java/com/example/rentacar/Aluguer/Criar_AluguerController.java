package com.example.rentacar.Aluguer;



import com.example.rentacar.Database.DatabaseConnection;
import com.example.rentacar.Menu.notifi;
import com.example.rentacar.Veiculo.Veiculo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class Criar_AluguerController implements Initializable {

    @FXML
    private TableColumn<Veiculo, LocalDate> c_data_fim_aluguer;

    @FXML
    private TableColumn<Veiculo, LocalDate> c_data_inic_aluguer;

    @FXML
    private Button CancelButton;

    @FXML
    private TableColumn<Veiculo, String> c_marca2_aluguer;

    @FXML
    private TableColumn<Veiculo, String> c_matricula2_aluguer;

    @FXML
    private TableColumn<Veiculo, String> c_modelo2_aluguer;

    @FXML
    private TableColumn<Veiculo,Integer>c_valor_aluguer;

    @FXML
    private Button butVerImagem;

    @FXML
    private ImageView imageCarro;

    @FXML
    private Pane Pane;

    @FXML
    private Button but_continuar;

    @FXML
    private TableColumn<Veiculo, String> c_categoria;

    @FXML
    private TableColumn<Veiculo, Integer> c_valor;

    @FXML
    private Slider sliderValor;

    @FXML
    private TextField txtValor;

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
    private TableColumn<Veiculo, Integer> c_manutencao;

    @FXML
    private TableColumn<Veiculo, String> c_marca;

    @FXML
    private TableColumn<Veiculo, String> c_matricula;
    int minValue = 0;
    int maxValue = 100;

    @FXML
    private TableColumn<Veiculo, String> c_modelo;

    @FXML
    private TableColumn<Veiculo, Date> c_revisao;

    @FXML
    private TableColumn<Veiculo, String> c_seguro;

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
    private TextField num_empregado_txt;

    @FXML
    private TextField num_txt;

    @FXML
    private TableView<Veiculo> tableVeiculos;

    @FXML
    private TableView<Veiculo> tableVeiculosAluguer;

    @FXML
    private TextField txtVeiculos;

    private int receivedID = 0;

    ArrayList<Veiculo> veiculoList = new ArrayList<>();


    ObservableList<Veiculo> observableVeiculoList;
    ObservableList<Veiculo> observableVeiculoAluguerList;

    /**
     * Manipulador de evento para o botão de abrir imagem.
     * Obtém a referência do botão ou qualquer outro controle que disparou o evento.
     * Obtém a referência da janela atual.
     * Cria uma nova janela (Stage).
     * Obtém a imagem do ImageView atual.
     * Cria um novo ImageView para exibir a imagem em resolução maior.
     * Configura a nova resolução desejada.
     * Cria um novo painel (AnchorPane) para conter o ImageView.
     * Cria uma nova cena (Scene) com o painel.
     * Configura a cena na nova janela.
     * Define o título da nova janela.
     * Exibe a nova janela.
     *
     * @param event O evento de clique no botão.
     */
    @FXML
    void abrirImagem(ActionEvent event) {
        // Obtém a referência do botão ou qualquer outro controle que disparou o evento
        Button openButton = (Button) event.getSource();

        // Obtém a referência da janela atual
        Stage currentStage = (Stage) openButton.getScene().getWindow();

        // Cria uma nova janela (Stage)
        Stage newStage = new Stage();

        // Obtém a imagem do ImageView atual
        ImageView imageView = (ImageView) currentStage.getScene().lookup("#imageCarro");

        // Cria um novo ImageView para exibir a imagem em resolução maior
        ImageView newImageView = new ImageView(imageView.getImage());

        // Configura a nova resolução desejada
        double novaLargura = 800; // Substitua pelo valor desejado
        double novaAltura = 600; // Substitua pelo valor desejado
        newImageView.setFitWidth(novaLargura);
        newImageView.setFitHeight(novaAltura);

        // Cria um novo painel (AnchorPane) para conter o ImageView
        AnchorPane anchorPane = new AnchorPane(newImageView);

        // Cria uma nova cena (Scene) com o painel
        Scene scene = new Scene(anchorPane);

        // Configura a cena na nova janela
        newStage.setScene(scene);

        // Define o título da nova janela
        newStage.setTitle("Imagem em resolução maior");

        // Exibe a nova janela
        newStage.show();
    }


    /**
     * Manipulador de evento para o botão de cancelar.
     * Exibe uma caixa de diálogo de confirmação.
     * Fecha a janela se a opção "OK" for selecionada.
     *
     * @param event O evento de clique no botão.
     */
    @FXML
    void HandleCancelButton(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação de ação");
        a.setContentText("Deseja mesmo cancelar a criação do Aluguer?");

        Optional<ButtonType> option = a.showAndWait();
        if (option.isPresent()) {

            if (option.get() == ButtonType.OK) {
                Stage stage = (Stage) CancelButton.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Define o ID recebido e atualiza o controlador de destino.
     *
     * @param selectedPerson   O ID da pessoa selecionada.
     * @param targetController O controlador de destino para atualização.
     */
    @FXML
    public void setReceivedId(int selectedPerson, Criar_AluguerController targetController) {
        this.receivedID = selectedPerson;
        num_txt.setText(String.valueOf(receivedID));

//        targetController.initialize(null,null);

    }

    /**
     * Manipulador de evento para a pressão da tecla no campo de texto.
     * Limpa a seleção da caixa de combinação de marca e realiza uma busca por veículos.
     */
    @FXML
    void textboxPress() {
        combo_Marca.getSelectionModel().clearSelection();
        procurarVeiculos();
    }

    /**
     * Manipulador de evento para o botão de procurar veículos.
     * Filtra a lista de veículos com base no texto inserido no campo de texto.
     * Atualiza a exibição da tabela com os veículos filtrados.
     */
    @FXML
    void procurarVeiculos() {
        tableVeiculos.setItems(observableVeiculoList);

        FilteredList<Veiculo> filteredList = new FilteredList<>(observableVeiculoList, b -> true);
        txtVeiculos.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(veiculo -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().contains(palavra) || veiculo.getMarca().toLowerCase().contains(palavra) || String.valueOf(veiculo.getKms()).contains(palavra) || veiculo.getSeguro().toLowerCase().contains(palavra) || String.valueOf(veiculo.getProx_revisao()).contains(palavra) || String.valueOf(veiculo.getUlt_inspecao()).contains(palavra) || veiculo.getLimpeza().toLowerCase().contains(palavra) || veiculo.getCategoria().toLowerCase().contains(palavra) || veiculo.getModelo().toLowerCase().contains(palavra);
            });

        });
        SortedList<Veiculo> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableVeiculos.comparatorProperty());
        tableVeiculos.setItems(sortedData);
    }

    /**
     * Manipulador de evento para o botão de continuar.
     * Realiza a criação de um novo aluguer com os veículos selecionados.
     * Atualiza o controlador de destino com as informações do aluguer.
     *
     * @param event O evento de clique no botão.
     */
    @FXML
    void but_continuar(ActionEvent event) {
        int id_orcamento = 0;

        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            List<Veiculo> veiculoaluguer = tableVeiculosAluguer.getItems();
            ArrayList<String> matriculas = getSecondTable();
            int cliente = 0;
            int n_empregado = 0;
            int v_orcamento = 0;

            String numTxtText = num_txt.getText();
            String numEmpregadoTxtText = num_empregado_txt.getText();

            if (!numTxtText.isEmpty() && numTxtText.matches("\\d+")) {
                cliente = Integer.parseInt(numTxtText);
            } else {
                // Alerta
            }

            if (!numEmpregadoTxtText.isEmpty() && numEmpregadoTxtText.matches("\\d+")) {
                n_empregado = Integer.parseInt(numEmpregadoTxtText);
            } else {
                // Alerta
            }

            for (Veiculo tab : veiculoaluguer) {
                v_orcamento += c_valor_aluguer.getCellData(tab);
            }
            String query = "DECLARE @id_orcamento INT; " +
                    "EXEC dbo.Alugar_carro " +
                    "   @v_orcamento = " + v_orcamento + ", " +
                    "   @estado_orcamento = 1, " +
                    "   @id_Cliente = " + cliente + ", " +
                    "   @id_funcionario = " + n_empregado + "," +
                    "   @estado_aluguer = 1," +
                    "   @id_orcamento = @id_orcamento OUTPUT; " +
                    "SELECT @id_orcamento as id_orcamento;";
            try {
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    id_orcamento = resultSet.getInt("id_orcamento");

                    String descricao = "Aluguel criado com sucesso. ID do aluguel: " + id_orcamento;

                    // Obter a hora atual
                    LocalDateTime horaAtual = LocalDateTime.now();

                    // Criar a notificação com a matrícula correta e a hora atual
                    notifi notificacao = new notifi(id_orcamento, matriculas.get(0), "Aluguel", descricao, horaAtual);
                    saveNotificacao(notificacao);
                    
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int i = 0;
            for (String matricula : matriculas) {
                Veiculo veiculo = tableVeiculosAluguer.getItems().get(i);
                LocalDate data_inic = null;
                LocalDate dataFim = null;

                try {
                    if (c_data_inic_aluguer.getCellData(veiculo) != null) {
                        data_inic = c_data_inic_aluguer.getCellData(veiculo);
                    }
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }

                try {
                    if (c_data_fim_aluguer.getCellData(veiculo) != null) {
                        dataFim = c_data_fim_aluguer.getCellData(veiculo);
                    }
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }


                // Verificar se as células têm valores definidos
                if (data_inic != null && dataFim != null) {
                    String query2 = "EXEC Alugar_carros2\n" +
                            "    @data_inic = '" + data_inic + "',\n" +
                            "    @data_fim = '" + dataFim + "',\n" +
                            "    @matricula = '" + matricula + "',\n" +
                            "    @id_orcamento = " + id_orcamento + ";";
                    try {
                        statement.executeUpdate(query2);
                        i++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Preencha todas as datas.");
                    alert.showAndWait();

                    return;
                }
            }
            openDashOrcamento(num_txt, num_empregado_txt, estado_combox, data_iniciodate.getValue(), data_fimdate.getValue(), id_orcamento);

            Stage stage = (Stage) but_continuar.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveNotificacao(notifi notificacao) {
        try {
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();

            String query = "INSERT INTO notifica (matricula, tipo, Descricao, hora_recebimento) VALUES ('" +
                    notificacao.getMatricula() + "', '" +
                    notificacao.getTipo() + "', '" +
                    notificacao.getDescricao() + "', '" +
                    notificacao.getHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "')";


            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            // Tratamento do erro ao salvar a notificação, se necessário
        }
    }


    /**
     * Abre a janela do painel de controle de orçamento.
     * Configura os dados necessários para exibição na janela.
     * Fecha a janela atual.
     *
     * @param cliente      O campo de texto para o número do cliente.
     * @param empregado    O campo de texto para o número do empregado.
     * @param estado       A caixa de combinação para o estado.
     * @param id_orcamento
     */
    private void openDashOrcamento(TextField cliente, TextField empregado, ComboBox<String> estado, LocalDate datainic, LocalDate datafim, int id_orcamento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rentacar/dash_orcamento.fxml"));
            Parent root = loader.load();
            OrcamentoController orcamentoController = loader.getController();

            List<Veiculo> veiculoList1 = tableVeiculosAluguer.getItems();

            orcamentoController.adicionarVeiculos(veiculoList1);

            // Configure as labels com os dados passados
            orcamentoController.setLabelCliente(cliente.getText());
            orcamentoController.setLabelNumEmpregado(empregado.getText());
            orcamentoController.setLabelEstado(estado.getValue());

            // Calcular o valor total de todos os carros selecionados
            double valorTotal = calcularValorTotal(datainic, datafim);

            // Exibir o valor total na label correspondente
            orcamentoController.setLabelValorCarro(String.valueOf(valorTotal));

            double desconto = desconto(Integer.parseInt(cliente.getText()), valorTotal);
            orcamentoController.setLabelDesconto("- " + String.valueOf(desconto));

            double valorliquido = valorTotal - desconto;
            orcamentoController.setLabelTotalliquido(String.valueOf(valorliquido));

            double IVA = (valorTotal * 0.23);
            orcamentoController.setLabelIVA(String.valueOf(IVA));

            double ValorTotals = valorliquido + IVA;
            orcamentoController.setLabelvalortotalbruto(String.valueOf(ValorTotals));

            orcamentoController.setLabelIdOrcamento(id_orcamento);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Fechar a janela atual (se necessário)
            Stage currentStage = (Stage) but_continuar.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double desconto(int cliente , double valor) {

        String query = "SELECT id_rank FROM Cliente WHERE id = " + cliente;

        try {
            // Estabelecer uma conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            double desconto = 0.0;

            if (resultSet.next()) {
                String rank = resultSet.getString("id_rank");

                if (rank.equalsIgnoreCase("1")) {
                    desconto = (0.05 * valor); // 5% de desconto
                } else if (rank.equalsIgnoreCase("silver")) {
                    desconto = (0.10 * valor); // 10% de desconto
                } else if (rank.equalsIgnoreCase("gold")) {
                    desconto = (0.15 * valor); // 15% de desconto
                } else if (rank.equalsIgnoreCase("diamond")) {
                    desconto = (0.20 * valor); // 20% de desconto
                }
            }

            return desconto;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public double calcularValorTotal(LocalDate datainicio, LocalDate datafim) {
        double valorTotal = 0.0;

        // Obtenha a lista de veículos do controlador atual
        List<Veiculo> veiculos = tableVeiculosAluguer.getItems();

        // Calcule o valor total somando os valores de cada veículo
        for (Veiculo veiculo : veiculos) {
            double valorDia = veiculo.getValor_dia();
            int diff = calcularDiasAluguel(datainicio, datafim);
            double valor = valorDia * diff;
            valorTotal += valor;
        }

        return valorTotal;
    }

    private int calcularDiasAluguel(LocalDate dataInicio, LocalDate dataFim) {
        return (int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
    }


    /**
     * Obtém as marcas de veículos do banco de dados.
     *
     * @return Uma lista observável de marcas de veículos.
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
     * Seleciona uma linha na tabela de veículos.
     * Ao clicar duas vezes em uma linha na tabela, o veículo é adicionado à segunda tabela.
     */
    private void selectRow() {
        tableVeiculos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Veiculo selectedVeiculo = tableVeiculos.getSelectionModel().getSelectedItem();
                int valor = tableVeiculos.getSelectionModel().getSelectedItem().getValor_dia();
                if (selectedVeiculo != null) {
                    setSecondTable(selectedVeiculo,valor);
                }
            }
        });
        tableVeiculosAluguer.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Veiculo selectedVeiculo = tableVeiculosAluguer.getSelectionModel().getSelectedItem();
                if (selectedVeiculo != null) {
                    setFirstTable(selectedVeiculo);
                }
            }
        });
    }

    /**
     * Sets the second table with the selected veiculo and valor.
     *
     * @param selectedVeiculo The selected veiculo.
     * @param valor           The valor.
     */
    private void setSecondTable(Veiculo selectedVeiculo, int valor){
        if (observableVeiculoAluguerList == null) {
            observableVeiculoAluguerList = FXCollections.observableArrayList();

            c_matricula2_aluguer.setCellValueFactory(new PropertyValueFactory<>("matricula"));
            c_marca2_aluguer.setCellValueFactory(new PropertyValueFactory<>("marca"));
            c_modelo2_aluguer.setCellValueFactory(new PropertyValueFactory<>("modelo"));

            LocalDate dataInicio = data_iniciodate.getValue();
            LocalDate dataFim = data_fimdate.getValue();

            c_data_inic_aluguer.setCellValueFactory(cellData -> new SimpleObjectProperty<>(dataInicio));
            c_data_fim_aluguer.setCellValueFactory(cellData -> new SimpleObjectProperty<>(dataFim));

            if (dataInicio != null && dataFim != null) {
                int dif = (int) ChronoUnit.DAYS.between(dataInicio, dataFim);
                int v_orcamento = valor * (dif + 1);
                c_valor_aluguer.setCellValueFactory(cellData -> new SimpleObjectProperty<>(v_orcamento));
            } else {
                c_valor_aluguer.setCellValueFactory(cellData -> new SimpleObjectProperty<>(0));
            }

            tableVeiculosAluguer.setItems(observableVeiculoAluguerList);
        }

        if (!observableVeiculoAluguerList.contains(selectedVeiculo)) {
            observableVeiculoAluguerList.add(selectedVeiculo);

            ObservableList<Veiculo> tableVeiculosItems = FXCollections.observableArrayList(tableVeiculos.getItems());
            if (tableVeiculosItems.contains(selectedVeiculo)) {
                tableVeiculosItems.remove(selectedVeiculo);
                tableVeiculos.setItems(tableVeiculosItems);
            }
        }
    }

    /**
     * Sets the first table with the selected veiculo.
     *
     * @param selectedVeiculo The selected veiculo.
     */
    private void setFirstTable(Veiculo selectedVeiculo) {
        observableVeiculoList.add(selectedVeiculo);

        // Cria uma lista temporária contendo o objeto a ser removido
        List<Veiculo> itensRemover = new ArrayList<>();
        itensRemover.add(selectedVeiculo);

        // Remove os itens da lista usando o método removeAll
        tableVeiculos.getItems().removeAll(itensRemover);
    }

    /**
     * Retrieves the matriculas from the second table.
     *
     * @return The list of matriculas.
     */
    private ArrayList<String>   getSecondTable() {
        ObservableList<Veiculo> listadematriculas = tableVeiculosAluguer.getItems();
        ArrayList<String> matriculas = new ArrayList<>();

        for (Veiculo veiculo : listadematriculas) {
            String matricula = veiculo.getMatricula();
            matriculas.add(matricula);
        }
        return matriculas;
    }

    /**
     * Retrieves the modelo IDs from the database based on the selectedMarca.
     *
     * @param selectedMarca The selected marca.
     * @return The list of modelo IDs.
     */
    private ArrayList<Integer> getModeloIdFromDatabase(String selectedMarca) {
        ArrayList<Integer> modeloId = new ArrayList<>();

        String query = "SELECT DISTINCT id_modelo FROM marca_modelo INNER JOIN marca ON marca.id=marca_modelo.id_marca WHERE marca.marca ='" + selectedMarca + "';";

        try {
            // Estabelecer uma conexão com o banco de dados
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            Statement statement = databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Ler o resultado da consulta
            while (resultSet.next()) {
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
     * Retrieves the modelo from the database based on the modeloId.
     *
     * @param modeloId The modelo ID.
     * @return The list of modelos.
     */
    private ObservableList<String> getmodeloFromDatabase(Integer modeloId) {
        ObservableList<String> modelo = FXCollections.observableArrayList();

        try {
            // Establish a connection to the database
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            PreparedStatement statement = databaseConnection.getConnection().prepareStatement("SELECT DISTINCT modelo FROM modelo WHERE id = ?");
            statement.setString(1, String.valueOf(modeloId));

            ResultSet resultSet = statement.executeQuery();

            // Read the results of the query and add the values to the ObservableList
            while (resultSet.next()) {
                String mod = resultSet.getString("modelo");
                modelo.add(mod);
            }

            // Close the connection and release resources
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modelo;
    }

    /**
     * Retrieves the estados aluguer from the database.
     *
     * @return The list of estados aluguer.
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

            // Fechar todos os resultados
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estados;
    }

    /**
     * Handles the event when the marca is changed.
     *
     * @param event The ActionEvent.
     */
    public void onmarcachange(ActionEvent event) {
        String selectedMarca = combo_Marca.getValue(); // Get the selected marca
        ArrayList<Integer> modeloIds = getModeloIdFromDatabase(selectedMarca);
        combo_Modelo.setDisable(false);
        // Limpar itens anteriores
        combo_Modelo.getItems().clear();

        for (Integer modeloId : modeloIds) {
            combo_Modelo.getItems().addAll(getmodeloFromDatabase(modeloId));
        }
        FilteredList<Veiculo> filteredList = new FilteredList<>(observableVeiculoList, b -> true);
        combo_Modelo.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(veiculo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String palavra = newValue.toLowerCase();

                return veiculo.getMatricula().contains(palavra) || veiculo.getMarca().toLowerCase().contains(palavra) || String.valueOf(veiculo.getKms()).contains(palavra) || veiculo.getSeguro().toLowerCase().contains(palavra) || String.valueOf(veiculo.getProx_revisao()).contains(palavra) || String.valueOf(veiculo.getUlt_inspecao()).contains(palavra) || veiculo.getLimpeza().toLowerCase().contains(palavra) || veiculo.getCategoria().toLowerCase().contains(palavra) || veiculo.getModelo().toLowerCase().contains(palavra);
            });
        });

        SortedList<Veiculo> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tableVeiculos.comparatorProperty());
        tableVeiculos.setItems(sortedData);
    }


    /**
     * Handles the event when the data is changed.
     *
     * @param event The ActionEvent.
     */
    @FXML
    public void OnDataChange(ActionEvent event) {
        tableVeiculos.getItems().clear();
        LocalDate data = data_iniciodate.getValue();
        List<Veiculo> veiculoList = getdata(data);

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
        c_valor.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("valor_dia"));

        tableVeiculos.setItems(FXCollections.observableList(veiculoList));
    }

    /**
     * Handles the event when the modelo is changed.
     *
     * @param event The ActionEvent.
     */
    @FXML
    void onmodelochange(ActionEvent event) {
        String selectedMarca = combo_Marca.getValue(); // Get the selected marca
        String selectedModelo = combo_Modelo.getValue(); // Obter o modelo selecionado

        // Obter o ID do modelo

    }

    /**
     * Retrieves the list of veiculos.
     *
     * @return The list of veiculos.
     */
    public List<Veiculo> getVeiculo() {

        String query = "SELECT * FROM ListarVeiculo";

        //String query = "exec criar_alugar_carros @data_inicial='"+data_iniciodate+"';";

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
                ;
                String limpeza = resultSet.getString("stat");
                String categoria = resultSet.getString("categoria");
                int id_manutencao = resultSet.getInt("id_manutencao");
                String disponibilizado = resultSet.getString("disponibilidade");
                String seguro = resultSet.getString("nome");
                int valor_dia = resultSet.getInt("valor_dia");
                String Imagem = resultSet.getString("Imagem");

                Veiculo veiculo1 = new Veiculo(Imagem, valor_dia, matricula, marca, modelo, cilindrada, cv, combustivel, kms, prox_revisao, ult_inspecao, limpeza, categoria, id_manutencao, disponibilizado, seguro);
                observableVeiculoList = FXCollections.observableList(veiculoList);
                veiculoList.add(veiculo1);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculoList;

    }

    /**
     * Obtém os veículos com base na data fornecida.
     * @param data A data utilizada para filtrar os veículos.
     * @return Uma lista de veículos filtrados pela data.
     */
    public ArrayList<Veiculo> getdata(LocalDate data) {

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
                ;
                String limpeza = resultSet.getString("stat");
                String categoria = resultSet.getString("categoria");
                int id_manutencao = resultSet.getInt("id_manutencao");
                String disponibilizado = resultSet.getString("disponibilidade");
                String seguro = resultSet.getString("nome");
                String Imagem = resultSet.getString("Imagem");
                int valor_dia = resultSet.getInt("valor_dia");

                Veiculo veiculo1 = new Veiculo(Imagem, valor_dia, matricula, marca, modelo, cilindrada, cv, combustivel, kms, prox_revisao, ult_inspecao, limpeza, categoria, id_manutencao, disponibilizado, seguro);
                observableVeiculoList = FXCollections.observableList(veiculoList);
                veiculoList.add(veiculo1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return veiculoList;
    }

    /**
     * Popula a tabela de veículos.
     */
    public void tablePopulate() {
        List<Veiculo> veiculoList = getVeiculo();

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
        c_valor.setCellValueFactory(new PropertyValueFactory<Veiculo, Integer>("valor_dia"));

        tableVeiculos.setItems(FXCollections.observableList(veiculoList));
    }

    /**
     * Inicializa o controlador.
     * @param location O URL da localização do FXML.
     * @param resources Os recursos utilizados para localizar o FXML.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combo_Marca.setItems(getmarcaFromDatabase());
        estado_combox.setItems(getEstadosAluguerFromDatabase());
        observableVeiculoList = FXCollections.observableArrayList(veiculoList);
        combo_Modelo.setDisable(true);
        tablePopulate();


        Veiculo.veiculoList.removeAll(veiculoList);
        if (!(data_iniciodate == null)) {
            tableVeiculos.getItems().clear();
            tablePopulate();
            selectRow();
            tableVeiculos.setPlaceholder(new Label("Não foram encontrados resultados."));
            tableVeiculos.setItems(observableVeiculoList);
            tableVeiculosAluguer.setPlaceholder(new Label("Não foram encontrados resultados."));
        }

        // Definir a lista de veículos na tabela
        tableVeiculos.setItems(observableVeiculoList);





        // Agendar a definição dos valores mínimo e máximo do controle deslizante para ser executada posteriormente
        Platform.runLater(() -> {
            try {
                // Obter uma instância da classe DatabaseConnection
                DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

                // Abrir a conexão com o banco de dados
                Connection connection = databaseConnection.getConnection();
                if (connection != null) {
                    // Obter os valores mínimo e máximo do banco de dados
                    double minValue = getMinValueFromDatabase(connection);
                    double maxValue = getMaxValueFromDatabase(connection);

//                    // Fechar a conexão com o banco de dados
//                    connection.close();

                    // Converter os valores para inteiros
                    int minIntValue = (int) Math.round(minValue);
                    int maxIntValue = (int) Math.round(maxValue);

                    // Definir os valores mínimo e máximo do controle deslizante
                    Platform.runLater(() -> {
                        sliderValor.setMin(minIntValue);
                        sliderValor.setMax(maxIntValue);
                        sliderValor.setValue((maxIntValue + minIntValue) / 2); // Definir um valor inicial

                        // Adicionar um ouvinte de alteração ao Slider
                        sliderValor.valueProperty().addListener((observable, oldValue, newValue) -> {
                            // Atualizar o valor do TextField com o valor atual do Slider
                            txtValor.setText(String.valueOf(newValue.intValue()));
                        });

                        // Atualizar o valor do TextField com o valor inicial do Slider
                        txtValor.setText(String.valueOf(Math.round(sliderValor.getValue())));
                    });
                } else {
                    throw new SQLException("Falha ao obter a conexão com o banco de dados.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


    }

    /**
     * Manipula o evento de mover o Slider.
     * @param event O evento do mouse.
     */
    @FXML
    void moverSlider(MouseEvent event) {

    }

    /**
     * Obtém o valor mínimo do banco de dados.
     * @param connection A conexão com o banco de dados.
     * @return O valor mínimo obtido do banco de dados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private int getMinValueFromDatabase(Connection connection) throws SQLException {
        int minValue = 0;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MIN(valor_dia) AS minimo FROM Veiculos;")) {

            if (resultSet.next()) {
                minValue = resultSet.getInt("minimo");
            }
        }

        return minValue;
    }

    /**
     * Obtém o valor máximo do banco de dados.
     * @param connection A conexão com o banco de dados.
     * @return O valor máximo obtido do banco de dados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    private int getMaxValueFromDatabase(Connection connection) throws SQLException {
        int maxValue = 0;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT MAX(valor_dia) AS maximo FROM Veiculos;")) {

            if (resultSet.next()) {
                maxValue = resultSet.getInt("maximo");
            }
        }

        return maxValue;
    }

    /**
     * Cria uma nova cena com base no arquivo FXML fornecido e exibe uma nova janela com essa cena.
     * @param arquivofxml O caminho para o arquivo FXML.
     * @param Mensagem O título da nova janela.
     */
    private void Criarcena(String arquivofxml, String Mensagem) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(arquivofxml));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Configurar a cena
        Scene scene = new Scene(root);

        // Configurar o palco (janela)
        Stage novaJanela = new Stage();
        novaJanela.setResizable(false);
        novaJanela.setTitle(Mensagem);
        novaJanela.setScene(scene);

        // Exibir a nova janela
        novaJanela.show();
    }

    /**
     * Abre uma nova janela exibindo a imagem atualmente selecionada.
     * @param event O evento de clique do botão.
     */
    @FXML
    void verImagem(ActionEvent event) {
        ImageView imageView = new ImageView(imageCarro.getImage());

        // Crie um novo Stage para a nova janela
        Stage imageStage = new Stage();
        imageStage.setTitle("Imagem");

        // Crie um StackPane e adicione o ImageView a ele
        StackPane imagePane = new StackPane();
        imagePane.getChildren().add(imageView);

        // Crie uma nova Scene com o StackPane
        Scene imageScene = new Scene(imagePane, 800, 600);

        // Defina a Scene no Stage e mostre a nova janela
        imageStage.setScene(imageScene);
        imageStage.show();
    }
}
