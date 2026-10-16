package com.example.rentacar.Veiculo;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A classe Veiculo representa um veículo disponível para aluguel.
 */
public class Veiculo {

    private String Imagem;
    private int valor_dia;
    private String matricula;
    private String marca;
    private String modelo;
    private int cilindrada;
    private int cv;
    private String combustivel;
    private int kms;
    private LocalDate prox_revisao;
    private LocalDate ult_inspecao;
    private String limpeza;
    private String categoria;
    private int id_manutencao;
    private String disponibilizado;
    private String seguro;

    /**
     * Lista de veículos disponíveis.
     */
    public static ArrayList<Veiculo> veiculoList = new ArrayList<>();

    /**
     * Construtor da classe Veiculo.
     *
     * @param imagem          A imagem do veículo.
     * @param valor_dia       O valor do aluguel diário do veículo.
     * @param matricula       A matrícula do veículo.
     * @param marca           A marca do veículo.
     * @param modelo          O modelo do veículo.
     * @param cilindrada      A cilindrada do veículo.
     * @param cv              O número de cavalos de potência do veículo.
     * @param combustivel     O tipo de combustível do veículo.
     * @param kms             O número de quilômetros percorridos pelo veículo.
     * @param prox_revisao    A data da próxima revisão do veículo.
     * @param ult_inspecao    A data da última inspeção do veículo.
     * @param limpeza         O estado de limpeza do veículo.
     * @param categoria       A categoria do veículo.
     * @param id_manutencao   O ID da manutenção do veículo.
     * @param disponibilizado O status de disponibilidade do veículo.
     * @param seguro          O status do seguro do veículo.
     */
    public Veiculo(String imagem, int valor_dia, String matricula, String marca, String modelo, int cilindrada, int cv, String combustivel, int kms, LocalDate prox_revisao, LocalDate ult_inspecao, String limpeza, String categoria, int id_manutencao, String disponibilizado, String seguro) {
        this.Imagem = imagem;
        this.valor_dia = valor_dia;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.cilindrada = cilindrada;
        this.cv = cv;
        this.combustivel = combustivel;
        this.kms = kms;
        this.prox_revisao = prox_revisao;
        this.ult_inspecao = ult_inspecao;
        this.limpeza = limpeza;
        this.categoria = categoria;
        this.id_manutencao = id_manutencao;
        this.disponibilizado = disponibilizado;
        this.seguro = seguro;
    }

    /**
     * Obtém a matrícula do veículo.
     *
     * @return A matrícula do veículo.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula do veículo.
     *
     * @param matricula A matrícula do veículo.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém a marca do veículo.
     *
     * @return A marca do veículo.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Define a marca do veículo.
     *
     * @param marca A marca do veículo.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtém o modelo do veículo.
     *
     * @return O modelo do veículo.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define o modelo do veículo.
     *
     * @param modelo O modelo do veículo.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtém a cilindrada do veículo.
     *
     * @return A cilindrada do veículo.
     */
    public int getCilindrada() {
        return cilindrada;
    }

    /**
     * Define a cilindrada do veículo.
     *
     * @param cilindrada A cilindrada do veículo.
     */
    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }

    /**
     * Obtém o número de cavalos de potência do veículo.
     *
     * @return O número de cavalos de potência do veículo.
     */
    public int getCv() {
        return cv;
    }

    /**
     * Define o número de cavalos de potência do veículo.
     *
     * @param cv O número de cavalos de potência do veículo.
     */
    public void setCv(int cv) {
        this.cv = cv;
    }

    /**
     * Obtém o tipo de combustível do veículo.
     *
     * @return O tipo de combustível do veículo.
     */
    public String getCombustivel() {
        return combustivel;
    }

    /**
     * Define o tipo de combustível do veículo.
     *
     * @param combustivel O tipo de combustível do veículo.
     */
    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    /**
     * Obtém o número de quilômetros percorridos pelo veículo.
     *
     * @return O número de quilômetros percorridos pelo veículo.
     */
    public int getKms() {
        return kms;
    }

    /**
     * Define o número de quilômetros percorridos pelo veículo.
     *
     * @param kms O número de quilômetros percorridos pelo veículo.
     */
    public void setKms(int kms) {
        this.kms = kms;
    }

    /**
     * Obtém a data da próxima revisão do veículo.
     *
     * @return A data da próxima revisão do veículo.
     */
    public LocalDate getProx_revisao() {
        return prox_revisao;
    }

    /**
     * Define a data da próxima revisão do veículo.
     *
     * @param prox_revisao A data da próxima revisão do veículo.
     */
    public void setProx_revisao(LocalDate prox_revisao) {
        this.prox_revisao = prox_revisao;
    }

    /**
     * Obtém a data da última inspeção do veículo.
     *
     * @return A data da última inspeção do veículo.
     */
    public LocalDate getUlt_inspecao() {
        return ult_inspecao;
    }

    /**
     * Define a data da última inspeção do veículo.
     *
     * @param ult_inspecao A data da última inspeção do veículo.
     */
    public void setUlt_inspecao(LocalDate ult_inspecao) {
        this.ult_inspecao = ult_inspecao;
    }

    /**
     * Obtém o estado de limpeza do veículo.
     *
     * @return O estado de limpeza do veículo.
     */
    public String getLimpeza() {
        return limpeza;
    }

    /**
     * Define o estado de limpeza do veículo.
     *
     * @param limpeza O estado de limpeza do veículo.
     */
    public void setLimpeza(String limpeza) {
        this.limpeza = limpeza;
    }

    /**
     * Obtém a categoria do veículo.
     *
     * @return A categoria do veículo.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Define a categoria do veículo.
     *
     * @param categoria A categoria do veículo.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtém o ID da manutenção do veículo.
     *
     * @return O ID da manutenção do veículo.
     */
    public int getId_manutencao() {
        return id_manutencao;
    }

    /**
     * Define o ID da manutenção do veículo.
     *
     * @param id_manutencao O ID da manutenção do veículo.
     */
    public void setId_manutencao(int id_manutencao) {
        this.id_manutencao = id_manutencao;
    }

    /**
     * Obtém o status de disponibilidade do veículo.
     *
     * @return O status de disponibilidade do veículo.
     */
    public String getDisponibilizado() {
        return disponibilizado;
    }

    /**
     * Define o status de disponibilidade do veículo.
     *
     * @param disponibilizado O status de disponibilidade do veículo.
     */
    public void setDisponibilizado(String disponibilizado) {
        this.disponibilizado = disponibilizado;
    }

    /**
     * Obtém o status do seguro do veículo.
     *
     * @return O status do seguro do veículo.
     */
    public String getSeguro() {
        return seguro;
    }

    /**
     * Define o status do seguro do veículo.
     *
     * @param seguro O status do seguro do veículo.
     */
    public void setSeguro(String seguro) {
        this.seguro = seguro;
    }

    /**
     * Obtém a imagem do veículo.
     *
     * @return A imagem do veículo.
     */
    public String getImagem() {
        return Imagem;
    }

    /**
     * Define a imagem do veículo.
     *
     * @param imagem A imagem do veículo.
     */
    public void setImagem(String imagem) {
        Imagem = imagem;
    }

    /**
     * Obtém o valor do aluguel diário do veículo.
     *
     * @return O valor do aluguel diário do veículo.
     */
    public int getValor_dia() {
        return valor_dia;
    }

    /**
     * Define o valor do aluguel diário do veículo.
     *
     * @param valor_dia O valor do aluguel diário do veículo.
     */
    public void setValor_dia(int valor_dia) {
        this.valor_dia = valor_dia;
    }

}
