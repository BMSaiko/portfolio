package com.example.rentacar.Aluguer;

import com.example.rentacar.Veiculo.Veiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public abstract class Orcamento {

    private int id;
    private int valor;

    private String estado_orcamento;

    private int n_carros;

    private String nome_cliente;

    private String nome_funcionario;

    private String matricula;

    private LocalDate data_inic;

    private LocalDate data_fim;

    public static ArrayList<Orcamento> orcamentoList = new ArrayList<>();


    /**
     * Constructs a new Orcamento object with the specified parameters.
     *
     * @param id               The ID of the Orcamento.
     * @param valor            The value of the Orcamento.
     * @param estado_orcamento The estado_orcamento of the Orcamento.
     * @param nome_cliente     The nome_cliente of the Orcamento.
     * @param nome_funcionario The nome_funcionario of the Orcamento.
     * @param matricula        The matricula of the Orcamento.
     * @param data_inic        The data_inic of the Orcamento.
     * @param data_fim         The data_fim of the Orcamento.
     * @param n_carros         The number of cars in the Orcamento.
     */
    public Orcamento(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario, String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros) {
        this.id = id;
        this.valor = valor;
        this.estado_orcamento = estado_orcamento;
        this.nome_cliente = nome_cliente;
        this.nome_funcionario = nome_funcionario;
        this.matricula = matricula;
        this.data_inic = data_inic;
        this.data_fim = data_fim;
        this.n_carros = n_carros;
    }

    /**
     * Returns the ID of the Orcamento.
     *
     * @return The ID of the Orcamento.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the Orcamento.
     *
     * @param id The ID to set for the Orcamento.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the value of the Orcamento.
     *
     * @return The value of the Orcamento.
     */
    public float getValor() {
        return valor;
    }

    /**
     * Sets the value of the Orcamento.
     *
     * @param valor The value to set for the Orcamento.
     */
    public void setValor(int valor) {
        this.valor = valor;
    }

    /**
     * Returns the estado_orcamento of the Orcamento.
     *
     * @return The estado_orcamento of the Orcamento.
     */
    public String getEstado_orcamento() {
        return estado_orcamento;
    }

    /**
     * Sets the estado_orcamento of the Orcamento.
     *
     * @param estado_orcamento The estado_orcamento to set for the Orcamento.
     */
    public void setEstado_orcamento(String estado_orcamento) {
        this.estado_orcamento = estado_orcamento;
    }

    /**
     * Returns the nome_cliente of the Orcamento.
     *
     * @return The nome_cliente of the Orcamento.
     */
    public String getNome_cliente() {
        return nome_cliente;
    }

    /**
     * Sets the nome_cliente of the Orcamento.
     *
     * @param nome_cliente The nome_cliente to set for the Orcamento.
     */
    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }

    /**
     * Returns the nome_funcionario of the Orcamento.
     *
     * @return The nome_funcionario of the Orcamento.
     */
    public String getNome_funcionario() {
        return nome_funcionario;
    }

    /**
     * Sets the nome_funcionario of the Orcamento.
     *
     * @param nome_funcionario The nome_funcionario to set for the Orcamento.
     */
    public void setNome_funcionario(String nome_funcionario) {
        this.nome_funcionario = nome_funcionario;
    }

    /**
     * Returns the matricula of the Orcamento.
     *
     * @return The matricula of the Orcamento.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Sets the matricula of the Orcamento.
     *
     * @param matricula The matricula to set for the Orcamento.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Returns the data_inic of the Orcamento.
     *
     * @return The data_inic of the Orcamento.
     */
    public LocalDate getData_inic() {
        return data_inic;
    }

    /**
     * Sets the data_inic of the Orcamento.
     *
     * @param data_inic The data_inic to set for the Orcamento.
     */
    public void setData_inic(LocalDate data_inic) {
        this.data_inic = data_inic;
    }

    /**
     * Returns the data_fim of the Orcamento.
     *
     * @return The data_fim of the Orcamento.
     */
    public LocalDate getData_fim() {
        return data_fim;
    }

    /**
     * Sets the data_fim of the Orcamento.
     *
     * @param data_fim The data_fim to set for the Orcamento.
     */
    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    /**
     * Returns the number of cars in the Orcamento.
     *
     * @return The number of cars in the Orcamento.
     */
    public int getN_carros() {
        return n_carros;
    }

    /**
     * Sets the number of cars in the Orcamento.
     *
     * @param n_carros The number of cars to set for the Orcamento.
     */
    public void setN_carros(int n_carros) {
        this.n_carros = n_carros;
    }
}
