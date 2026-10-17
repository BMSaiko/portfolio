package com.example.sheetsandpicks.BL.Database;

import com.example.sheetsandpicks.Controllers.database.DBConnect;
import com.example.sheetsandpicks.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBstatements {

/**
 * Cria e retorna um objeto Statement para interação com a base de dados.
 *
 * Este método utiliza a classe DBConnect para obter uma conexão com a base de dados e,
 * em seguida, cria e retorna um objeto Statement associado a essa conexão. O objeto Statement
 * é utilizado para executar comandos SQL na base de dados.
 *
 * @return Um objeto Statement para interação com a base de dados.
 * @throws SQLException Se ocorrer um erro ao criar o objeto Statement.
 */
private static Statement criarostatment() throws SQLException {
        DBConnect databaseController = DBConnect.getInstance();
        Statement statement = databaseController.getConnection().createStatement();
        return statement;
    }

/**
 * Recupera uma lista de objetos Stock da base de dados com base no chamador.
 *
 * Este método utiliza a classe DBConnect para obter uma conexão com a base de dados e
 * executa uma consulta SQL específica com base no chamador do método. A lista de objetos
 * Stock resultante é retornada, e o método é projetado para ser utilizado em contextos
 * específicos, como nas classes StockConfirmationController e AppDashVizualizarStock.
 *
 * @return Uma lista observável de objetos Stock recuperados da base de dados.
 */
public static List<Stock> GetStock() {
        ObservableList<Stock> observableList = FXCollections.observableArrayList(); // Use FXCollections para criar uma lista observável
        ArrayList<Stock> StockConfirmar_List = new ArrayList<>(); // Lista de retorno

        String query = ""; // Defina a query conforme a lógica do seu código
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            if (caller.getClassName().equals("com.example.sheetsandpicks.Controllers.Stock.StockConfirmationController")) {
                query = "SELECT * FROM Listar_Stock_Confirmar";
            } else if (caller.getClassName().equals("com.example.sheetsandpicks.Controllers.Stock.AppDashVizualizarStock")) {
                query = "SELECT * FROM Listar_Stock";
            } else {
                return StockConfirmar_List; // Retorna uma lista vazia se o chamador não for reconhecido
            }
        }
        try (Statement statement = criarostatment();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String Nome = resultSet.getString("Nome");
                String Fornecedor = resultSet.getString("Fornecedor");
                int quantidade = resultSet.getInt("quantidade");
                String UOM = resultSet.getString("UOM");
                float Preco_uni = resultSet.getFloat("Preco_uni");
                String moeda = resultSet.getString("moeda");
                float v_base = Preco_uni*quantidade;
                String pais = resultSet.getString("pais");
                String n_encomenda = resultSet.getString("N_Encomenda");
                LocalDate data = resultSet.getDate("Data_encomenda").toLocalDate();
                String Aprov_Rejeitado = resultSet.getString("rejeitado");

                Stock stock1 = new Stock(n_encomenda,data,id, Nome, quantidade, Fornecedor,v_base, UOM, Preco_uni, moeda,pais,Aprov_Rejeitado);
                StockConfirmar_List.add(stock1);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Considere um tratamento mais adequado para as exceções
        }

        observableList.addAll(StockConfirmar_List); // Adiciona a lista ao ObservableList
        return observableList;
    }

/**
 * Confirma ou rejeita itens de estoque com base em uma lista de IDs e um status de aprovação.
 *
 * Este método executa procedimentos armazenados na base de dados para confirmar ou rejeitar itens
 * de estoque com base nos IDs fornecidos e no status de aprovação. Utiliza a classe DBConnect para
 * obter uma conexão com a base de dados e executa consultas SQL preparadas.
 *
 * @param ids Uma lista de IDs dos itens de estoque a serem confirmados ou rejeitados.
 * @param Aprov O status de aprovação, onde "rejeitado" indica rejeição e outros valores indicam aprovação.
 * @throws SQLException Se ocorrer um erro durante a execução das consultas SQL.
 */
public static void ConfirmarStock(ArrayList<Integer> ids,String Aprov) throws SQLException {
        String query;
        if(Aprov != "rejeitado") {
            query = "EXEC ConfirmacaodeStock @id = ?";
        }else {
            query = "EXEC RejeitarStock @id = ?";
        }
        Connection connection = DBConnect.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        try {
            // Confirmar os itens
            for (Integer id : ids) {
                preparedStatement.setInt(1, id);
            }
            int ama=preparedStatement.executeUpdate();
            if(ama==0){
                System.out.println("ERRO!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Insere dados de uma encomenda na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para criar uma nova encomenda.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param numeroEncomenda O número da encomenda.
 * @param data A data da encomenda.
 * @param nomeFornecedor O nome do fornecedor.
 * @param total O valor total da encomenda.
 * @param pais O país da encomenda.
 * @param nome O nome do produto.
 * @param precoUni O preço unitário do produto.
 * @param quantidade A quantidade de produtos na encomenda.
 * @param pesoLiquido O peso líquido do produto.
 * @param contagem A contagem do produto.
 * @param UOM A unidade de medida do produto.
 * @param UOM2 A segunda unidade de medida do produto.
 * @param UOM3 A terceira unidade de medida do produto.
 * @param agenciaBuyer A agência do comprador.
 * @param agenciaBuyerValor O valor da agência do comprador.
 * @param agenciaSupplier A agência do fornecedor.
 * @param agenciaSupplierValor O valor da agência do fornecedor.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void enviarDados(String numeroEncomenda, LocalDate data, String nomeFornecedor, float total, String pais, String nome, float precoUni, int quantidade, float pesoLiquido, float contagem, String UOM, String UOM2, String UOM3, String agenciaBuyer, String agenciaBuyerValor, String agenciaSupplier, String agenciaSupplierValor) throws SQLException {
        String query = "EXEC Criar_Encomenda " +
                "@id=?, " +
                "@data=?, " +
                "@nome_fornecedor=?, " +
                "@Total=?, " +
                "@pais=?, " +
                "@nome=?, " +
                "@preco_uni=?, " +
                "@quantidade=?, " +
                "@peso_liquido=?, " +
                "@contagem=?, " +
                "@UOM=?, " +
                "@UOM2=?, " +
                "@UOM3=?, " +
                "@Agencia_buyer=?, " +
                "@Agencia_buyer_valor=?, " +
                "@Agencia_supplier=?, " +
                "@Agencia_supplier_valor=?;";

        Connection connection = DBConnect.getInstance().getConnection();
        PreparedStatement callableStatement = connection.prepareStatement(query);
            try {
            callableStatement.setString(1, numeroEncomenda);
            callableStatement.setDate(2, Date.valueOf(data));
            callableStatement.setString(3, nomeFornecedor);
            callableStatement.setFloat(4, total);
            callableStatement.setString(5, pais);
            callableStatement.setString(6, nome);
            callableStatement.setFloat(7, precoUni);
            callableStatement.setInt(8, quantidade);
            callableStatement.setFloat(9, pesoLiquido);
            callableStatement.setFloat(10, contagem);
            callableStatement.setString(11, UOM);
            callableStatement.setString(12, UOM2);
            callableStatement.setString(13, UOM3);
            callableStatement.setString(14, agenciaBuyer);
            callableStatement.setString(15, agenciaBuyerValor);
            callableStatement.setString(16, agenciaSupplier);
            callableStatement.setString(17, agenciaSupplierValor);

            int hasResults = callableStatement.executeUpdate();
            if(hasResults==0){
                System.out.println("ERRO!");
            }

        } catch (SQLException e) {
                e.printStackTrace();
            }
    }

/**
 * Busca e retorna uma lista de objetos Suppliers da base de dados.
 *
 * Este método utiliza o método obterDadosUsers para buscar dados do tipo "Fornecedor"
 * da base de dados e cria uma lista de objetos Suppliers com base nos resultados obtidos.
 *
 * @return Uma lista de objetos Suppliers representando fornecedores na base de dados.
 */
public static List<Suppliers> buscarFornecedores() {
        List<Suppliers> fornecedores = new ArrayList<>();
        String tipo = "Fornecedor";
        try {
            ResultSet rs = obterDadosUsers(tipo);
            while (rs.next()) {
                Suppliers fornecedor = criarFornecedor(rs);
                fornecedores.add(fornecedor);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tratar a exceção de alguma forma apropriada
        }
        return fornecedores;
    }

/**
 * Obtém uma lista observável de nomes de países da base de dados.
 *
 * Este método executa uma consulta SQL na base de dados para obter os nomes dos países
 * e cria uma lista observável de strings com base nos resultados obtidos.
 *
 * @return Uma lista observável de strings representando nomes de países.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static ObservableList<String> getPais() throws SQLException {
        ObservableList<String> Pais = FXCollections.observableArrayList();
        Connection conn = DBConnect.getInstance().getConnection();
        String query = "SELECT Pais FROM Ver_pais";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        try {
            while (rs.next()) {
                String pais = rs.getString("Pais");
                Pais.add(pais);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pais;
    }

/**
 * Obtém dados de utilizadores com base no tipo de permissão especificado.
 *
 * Este método executa uma sequência de consultas SQL na base de dados para obter dados pessoais
 * de utilizadores com um determinado tipo de permissão e ativos. Utiliza a classe DBConnect para obter
 * uma conexão com a base de dados e executa consultas preparadas.
 *
 * @param tipo O tipo de permissão dos utilizadores a serem obtidos.
 * @return Um conjunto de resultados contendo os dados pessoais dos utilizadores.
 * @throws SQLException Se ocorrer um erro durante a execução das consultas SQL.
 */
private static ResultSet obterDadosUsers(String tipo) throws SQLException {

        String query2 = "SELECT id FROM permissao WHERE Tipo = '" + tipo +"'";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query2);
        ResultSet rs = stmt.executeQuery();
        try{
            while (rs.next()){

               int id = rs.getInt("id");
                String query = "SELECT d.id, d.Nome, d.Endereco, d.Endereco2, d.Cidade, d.Telefone, d.Email, d.cod_postal, d.Data_nascimento, f.id_permissao, f.ativo \n" +
                        "FROM Dados_Pessoais d\n" +
                        "JOIN Funcionarios f ON d.id = f.id_Dados \n" +
                        "WHERE f.id_permissao = " + id + "AND f.ativo = 'S'";

                PreparedStatement stmt1 = conn.prepareStatement(query);
                return stmt1.executeQuery();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

/**
 * Cria um objeto Suppliers com base nos dados obtidos da base de dados.
 *
 * Este método recebe um conjunto de resultados (ResultSet) contendo dados de um fornecedor
 * e cria um objeto Suppliers com base nesses dados.
 *
 * @param rs Um conjunto de resultados contendo os dados do fornecedor.
 * @return Um objeto Suppliers criado com base nos dados do fornecedor.
 * @throws SQLException Se ocorrer um erro ao acessar os dados no ResultSet.
 */
private static Suppliers criarFornecedor(ResultSet rs) throws SQLException {
        return new Suppliers(
                rs.getInt("id"),
                rs.getString("ativo"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("cod_postal"),
                rs.getString("endereco2"),
                rs.getString("cidade"),
                rs.getInt("id_permissao"),
                rs.getString("endereco"),
                rs.getDate("data_nascimento"),
                rs.getInt("telefone")
        );
    }

/**
 * Busca e retorna uma lista de objetos Clients ativos da base de dados.
 *
 * Este método utiliza o método obterDadosClientesAtivos para buscar dados de clientes ativos
 * da base de dados e cria uma lista de objetos Clients com base nos resultados obtidos.
 *
 * @return Uma lista de objetos Clients representando clientes ativos na base de dados.
 */
public static List<Clients> buscarClientesAtivos() {
        List<Clients> clientesAtivos = new ArrayList<>();
        try {
            ResultSet rs = obterDadosClientesAtivos();
            while (rs.next()) {
                Clients cliente = criarCliente(rs);
                clientesAtivos.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tratar a exceção de alguma forma apropriada
        }
        return clientesAtivos;
    }

/**
 * Obtém dados de clientes ativos da base de dados.
 *
 * Este método executa uma consulta SQL na base de dados para obter os dados pessoais
 * de clientes ativos, utilizando a tabela Dados_Pessoais e a tabela Clientes.
 *
 * @return Um conjunto de resultados contendo os dados pessoais de clientes ativos.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
private static ResultSet obterDadosClientesAtivos() throws SQLException {
        String query = "SELECT Dados_pessoais.id, Dados_pessoais.Nome, Dados_pessoais.Endereco, Dados_pessoais.Endereco2," +
                "       Dados_pessoais.Cidade, Dados_pessoais.Telefone, Dados_pessoais.Email, Dados_pessoais.cod_postal," +
                "       Dados_pessoais.Data_nascimento, Clientes.ativo " +
                "FROM Dados_Pessoais " +
                "INNER JOIN Clientes ON Dados_Pessoais.id = Clientes.id_Dados " +
                "WHERE Clientes.ativo = 'S'";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        return stmt.executeQuery();
    }

/**
 * Cria um objeto Clients com base nos dados obtidos da base de dados.
 *
 * Este método recebe um conjunto de resultados (ResultSet) contendo dados de um cliente
 * e cria um objeto Clients com base nesses dados.
 *
 * @param rs Um conjunto de resultados contendo os dados do cliente.
 * @return Um objeto Clients criado com base nos dados do cliente.
 * @throws SQLException Se ocorrer um erro ao acessar os dados no ResultSet.
 */
private static Clients criarCliente(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String ativo = rs.getString("ativo");
        String nome = rs.getString("nome");
        String endereco = rs.getString("endereco");
        String endereco2 = rs.getString("endereco2");
        String cidade = rs.getString("cidade");
        int telefone = rs.getInt("telefone");
        String email = rs.getString("email");
        String cod_postal = rs.getString("cod_postal");
        String data_nascimento = rs.getString("data_nascimento");

        return new Clients(id, ativo, nome, endereco, endereco2, cidade, telefone, email, cod_postal, data_nascimento);
    }

/**
 * Cria um novo operador (funcionário) na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para criar um novo funcionário
 * com o nível de permissão de operador. Utiliza a classe DBConnect para obter uma conexão com a
 * base de dados e executa uma consulta SQL preparada.
 *
 * @param Nome O nome do operador.
 * @param Endereco O endereço do operador.
 * @param Endereco2 O segundo endereço do operador.
 * @param Cidade A cidade do operador.
 * @param pais O país do operador.
 * @param Telefone O número de telefone do operador.
 * @param Email O endereço de e-mail do operador.
 * @param cod_postal O código postal do operador.
 * @param Data_nascimento A data de nascimento do operador.
 * @param username O nome de utilizador do operador.
 * @param password A senha do operador.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void CriarOperador(String Nome, String Endereco, String Endereco2, String Cidade, String pais, int Telefone,
                                     String Email, String cod_postal, LocalDate Data_nascimento, String username, String password) throws SQLException {
        String query = "EXEC Criar_Funcionario" +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?," +
                " @username=?," +
                " @password=?," +
                " @nv_permissao=5;";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setString(1, Nome);
            pstmt.setString(2, Endereco);
            pstmt.setString(3, Endereco2);
            pstmt.setString(4, Cidade);
            pstmt.setString(5, pais);
            pstmt.setInt(6, Telefone);
            pstmt.setString(7, Email);
            pstmt.setString(8, cod_postal);
            pstmt.setDate(9, java.sql.Date.valueOf(Data_nascimento));
            pstmt.setString(10, username);
            pstmt.setString(11, password);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Cria um novo fornecedor na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para criar um novo funcionário
 * com o nível de permissão de fornecedor. Utiliza a classe DBConnect para obter uma conexão com o
 * base de dados e executa uma consulta SQL preparada.
 *
 * @param Nome O nome do fornecedor.
 * @param Endereco O endereço do fornecedor.
 * @param Endereco2 O segundo endereço do fornecedor.
 * @param Cidade A cidade do fornecedor.
 * @param pais O país do fornecedor.
 * @param Telefone O número de telefone do fornecedor.
 * @param Email O endereço de e-mail do fornecedor.
 * @param cod_postal O código postal do fornecedor.
 * @param Data_nascimento A data de nascimento do fornecedor.
 * @param username O nome de utilizador do fornecedor.
 * @param password A senha do fornecedor.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void CriarFornecedor(String Nome, String Endereco, String Endereco2, String Cidade, String pais, int Telefone,
                                       String Email, String cod_postal, LocalDate Data_nascimento, String username, String password) throws SQLException {
        String query = "EXEC Criar_Funcionario" +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?," +
                " @username=?," +
                " @password=?," +
                " @nv_permissao=4;";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try {

            pstmt.setString(1, Nome);
            pstmt.setString(2, Endereco);
            pstmt.setString(3, Endereco2);
            pstmt.setString(4, Cidade);
            pstmt.setString(5, pais);
            pstmt.setInt(6, Telefone);
            pstmt.setString(7, Email);
            pstmt.setString(8, cod_postal);
            pstmt.setDate(9, java.sql.Date.valueOf(Data_nascimento));
            pstmt.setString(10, username);
            pstmt.setString(11, password);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Cria um novo cliente na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para criar um novo cliente.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param Nome O nome do cliente.
 * @param Endereco O endereço do cliente.
 * @param Endereco2 O segundo endereço do cliente.
 * @param Cidade A cidade do cliente.
 * @param pais O país do cliente.
 * @param Telefone O número de telefone do cliente.
 * @param Email O endereço de e-mail do cliente.
 * @param cod_postal O código postal do cliente.
 * @param Data_nascimento A data de nascimento do cliente.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void CriarCliente(String Nome, String Endereco, String Endereco2, String Cidade, String pais, int Telefone,
                                       String Email, String cod_postal, LocalDate Data_nascimento) throws SQLException {
        String query = "EXEC Criar_Cliente" +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try {

            pstmt.setString(1, Nome);
            pstmt.setString(2, Endereco);
            pstmt.setString(3, Endereco2);
            pstmt.setString(4, Cidade);
            pstmt.setString(5, pais);
            pstmt.setInt(6, Telefone);
            pstmt.setString(7, Email);
            pstmt.setString(8, cod_postal);
            pstmt.setDate(9, java.sql.Date.valueOf(Data_nascimento));

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Busca e retorna uma lista de objetos Operators do tipo "Operador" da base de dados.
 *
 * Este método utiliza o método obterDadosUsers para buscar dados de utilizadores do tipo "Operador"
 * da base de dados e cria uma lista de objetos Operators com base nos resultados obtidos.
 *
 * @return Uma lista de objetos Operators representando operadores na base de dados.
 */
public static List<Operators> buscarOperadores() {
        List<Operators> operadores = new ArrayList<>();
        String tipo = "Operador";
        try {
            ResultSet rs = obterDadosUsers(tipo);
            while (rs.next()) {
                Operators operador = criarOperador(rs);
                operadores.add(operador);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tratar a exceção de alguma forma apropriada
        }
        return operadores;
    }

/**
 * Cria e retorna um objeto Operators com base nos dados obtidos da base de dados.
 *
 * Este método recebe um conjunto de resultados (ResultSet) contendo dados de um operador
 * e cria um objeto Operators com base nesses dados.
 *
 * @param rs Um conjunto de resultados contendo os dados do operador.
 * @return Um objeto Operators criado com base nos dados do operador.
 * @throws SQLException Se ocorrer um erro ao acessar os dados no ResultSet.
 */
private static Operators criarOperador(ResultSet rs) throws SQLException {
        return new Operators(
                rs.getInt("id"),
                rs.getString("ativo"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("cod_postal"),
                rs.getString("endereco2"),
                rs.getString("cidade"),
                rs.getInt("id_permissao"),
                rs.getString("endereco"),
                rs.getDate("data_nascimento"),
                rs.getInt("telefone")
        );
    }

/**
 * Busca e retorna uma lista de objetos Admins do tipo "Admin" da base de dados.
 *
 * Este método utiliza o método obterDadosUsers para buscar dados de utilizadores do tipo "Admin"
 * da base de dados e cria uma lista de objetos Admins com base nos resultados obtidos.
 *
 * @return Uma lista de objetos Admins representando administradores na base de dados.
 */
public static List<Admins> buscarAdmins() {
        List<Admins> admins = new ArrayList<>();
        String tipo = "Admin";
        try {
            ResultSet rs = obterDadosUsers(tipo);
            while (rs.next()) {
                Admins admin = criarAdmin(rs);
                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Tratar a exceção de alguma forma apropriada
        }
        return admins;
    }

/**
 * Cria e retorna um objeto Admins com base nos dados obtidos da base de dados.
 *
 * Este método recebe um conjunto de resultados (ResultSet) contendo dados de um administrador
 * e cria um objeto Admins com base nesses dados.
 *
 * @param rs Um conjunto de resultados contendo os dados do administrador.
 * @return Um objeto Admins criado com base nos dados do administrador.
 * @throws SQLException Se ocorrer um erro ao acessar os dados no ResultSet.
 */
private static Admins criarAdmin(ResultSet rs) throws SQLException {
        return new Admins(
                rs.getInt("id"),
                rs.getString("ativo"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("cod_postal"),
                rs.getString("endereco2"),
                rs.getString("cidade"),
                rs.getInt("id_permissao"),
                rs.getString("endereco"),
                rs.getDate("data_nascimento"),
                rs.getInt("telefone")
        );
    }

/**
 * Cria um novo administrador na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para criar um novo administrador.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param Nome O nome do administrador.
 * @param Endereco O endereço do administrador.
 * @param Endereco2 O segundo endereço do administrador.
 * @param Cidade A cidade do administrador.
 * @param pais O país do administrador.
 * @param Telefone O número de telefone do administrador.
 * @param Email O endereço de e-mail do administrador.
 * @param cod_postal O código postal do administrador.
 * @param Data_nascimento A data de nascimento do administrador.
 * @param username O nome de utilizador do administrador.
 * @param password A senha do administrador.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void CriarAdmin(String Nome, String Endereco, String Endereco2, String Cidade, String pais, int Telefone,
                                     String Email, String cod_postal, LocalDate Data_nascimento, String username, String password) throws SQLException {
        String query = "EXEC Criar_Funcionario" +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?," +
                " @username=?," +
                " @password=?," +
                " @nv_permissao=6;";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setString(1, Nome);
            pstmt.setString(2, Endereco);
            pstmt.setString(3, Endereco2);
            pstmt.setString(4, Cidade);
            pstmt.setString(5, pais);
            pstmt.setInt(6, Telefone);
            pstmt.setString(7, Email);
            pstmt.setString(8, cod_postal);
            pstmt.setDate(9, java.sql.Date.valueOf(Data_nascimento));
            pstmt.setString(10, username);
            pstmt.setString(11, password);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Elimina um cliente da base de dados com base no ID de dados fornecido.
 *
 * Este método executa um procedimento armazenado na base de dados para eliminar um cliente com base no ID de dados.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param id_dados O ID de dados do cliente a ser eliminado.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void EliminarCliente(int id_dados) throws SQLException {
        String query = "EXEC Eliminar_Cliente" +
                " @id_dados=?";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setInt(1, id_dados);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Atualiza as informações de um utilizador na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para alterar as informações de um utilizador.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param id O ID do utilizador a ser atualizado.
 * @param nome O novo nome do utilizador.
 * @param endereco O novo endereço do utilizador.
 * @param endereco2 O novo segundo endereço do utilizador.
 * @param cidade A nova cidade do utilizador.
 * @param pais O novo país do utilizador.
 * @param telefone O novo número de telefone do utilizador.
 * @param email O novo endereço de e-mail do utilizador.
 * @param codPostal O novo código postal do utilizador.
 * @param dataNascimento A nova data de nascimento do utilizador.
 * @param username O novo nome de utilizador do utilizador.
 * @param password A nova senha do utilizador.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void Updateusers(Integer id, String nome, String endereco, String endereco2, String cidade, String pais, int telefone, String email, String codPostal, LocalDate dataNascimento, String username, String password) throws SQLException {
        String query = "EXEC Alterar_Funcionario" +
                " @id=?," +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?," +
                " @username=?," +
                " @password=?;";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setInt(1, id);
            pstmt.setString(2, nome);
            pstmt.setString(3, endereco);
            pstmt.setString(4, endereco2);
            pstmt.setString(5, cidade);
            pstmt.setString(6, pais);
            pstmt.setInt(7, telefone);
            pstmt.setString(8, email);
            pstmt.setString(9, codPostal);
            pstmt.setDate(10, Date.valueOf(dataNascimento));
            pstmt.setString(11, username);
            pstmt.setString(12, password);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Elimina um funcionário da base de dados com base no ID de dados fornecido.
 *
 * Este método executa um procedimento armazenado na base de dados para eliminar um funcionário com base no ID de dados.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param id_dados O ID de dados do funcionário a ser eliminado.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void Eliminar_Funcionario(int id_dados) throws SQLException {
        String query = "EXEC Eliminar_Funcionario" +
                " @id_dados=?";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setInt(1, id_dados);

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPaisid(Integer id) throws SQLException {
        String query2 = "SELECT id_pais FROM Dados_pessoais WHERE id = '" + id +"'";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query2);
        ResultSet rs = stmt.executeQuery();
        try{
            while (rs.next()){

                int id_pais = rs.getInt("id_pais");
                String query = "SELECT Pais FROM Pais WHERE id =" + id_pais +"";

                PreparedStatement stmt1 = conn.prepareStatement(query);
                ResultSet rs2 = stmt1.executeQuery();

                try {
                    while (rs2.next()){
                        String pais = rs2.getString("Pais");
                        return pais;
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

/**
 * Atualiza as informações de um cliente na base de dados.
 *
 * Este método executa um procedimento armazenado na base de dados para alterar as informações de um cliente.
 * Utiliza a classe DBConnect para obter uma conexão com a base de dados e executa uma consulta SQL preparada.
 *
 * @param id O ID do cliente a ser atualizado.
 * @param nome O novo nome do cliente.
 * @param endereco O novo endereço do cliente.
 * @param endereco2 O novo segundo endereço do cliente.
 * @param cidade A nova cidade do cliente.
 * @param pais O novo país do cliente.
 * @param telefone O novo número de telefone do cliente.
 * @param email O novo endereço de e-mail do cliente.
 * @param codPostal O novo código postal do cliente.
 * @param dataNascimento A nova data de nascimento do cliente.
 * @throws SQLException Se ocorrer um erro durante a execução da consulta SQL.
 */
public static void Updateclientes(Integer id, String nome, String endereco, String endereco2, String cidade, String pais, int telefone, String email, String codPostal, LocalDate dataNascimento) throws SQLException {
        String query = "EXEC Alterar_Cliente" +
                " @id=?," +
                " @Nome=?," +
                " @Endereco=?," +
                " @Endereco2=?," +
                " @Cidade=?," +
                " @pais=?," +
                " @Telefone=?," +
                " @Email=?," +
                " @cod_postal=?," +
                " @Data_nascimento=?;";

        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        try{

            pstmt.setInt(1, id);
            pstmt.setString(2, nome);
            pstmt.setString(3, endereco);
            pstmt.setString(4, endereco2);
            pstmt.setString(5, cidade);
            pstmt.setString(6, pais);
            pstmt.setInt(7, telefone);
            pstmt.setString(8, email);
            pstmt.setString(9, codPostal);
            pstmt.setDate(10, Date.valueOf(dataNascimento));

            int rs = pstmt.executeUpdate();
            if (rs == 0) {
                System.out.println("Erro!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/**
 * Obtém o nome de utilizador associado a um funcionário com base no ID do funcionário.
 *
 * Este método realiza consultas SQL na base de dados para obter o ID de acesso associado ao ID do funcionário
 * e, em seguida, recupera o nome de utilizador correspondente a partir da tabela de acesso.
 *
 * @param id O ID do funcionário para o qual se deseja obter o nome de utilizador.
 * @return O nome de utilizador associado ao funcionário, ou null se não encontrado.
 * @throws SQLException Se ocorrer um erro durante a execução das consultas SQL.
 */
public static String getuser(Integer id) throws SQLException {
        String query2 = "SELECT id_acesso FROM Funcionarios WHERE id_Dados ='" + id +"'";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query2);
        ResultSet rs = stmt.executeQuery();
        try{
            while (rs.next()){

                int id_acesso = rs.getInt("id_acesso");
                String query = "SELECT username FROM acesso WHERE id =" + id_acesso +"";

                PreparedStatement stmt1 = conn.prepareStatement(query);
                ResultSet rs2 = stmt1.executeQuery();

                try {
                    while (rs2.next()){
                        String username = rs2.getString("username");
                        return username;
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

/**
 * Obtém a senha associada a um funcionário com base no ID do funcionário.
 *
 * Este método realiza consultas SQL na base de dados para obter o ID de acesso associado ao ID do funcionário
 * e, em seguida, recupera a senha correspondente a partir da tabela de acesso.
 *
 * @param id O ID do funcionário para o qual se deseja obter a senha.
 * @return A senha associada ao funcionário, ou null se não encontrada.
 * @throws SQLException Se ocorrer um erro durante a execução das consultas SQL.
 */
public static String getpass(Integer id) throws SQLException {
        String query2 = "SELECT id_acesso FROM Funcionarios WHERE id_Dados ='" + id +"'";
        Connection conn = DBConnect.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query2);
        ResultSet rs = stmt.executeQuery();
        try{
            while (rs.next()){

                int id_acesso = rs.getInt("id_acesso");
                String query = "SELECT password FROM acesso WHERE id =" + id_acesso +"";

                PreparedStatement stmt1 = conn.prepareStatement(query);
                ResultSet rs2 = stmt1.executeQuery();

                try {
                    while (rs2.next()){
                        String password = rs2.getString("password");
                        return password;
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
