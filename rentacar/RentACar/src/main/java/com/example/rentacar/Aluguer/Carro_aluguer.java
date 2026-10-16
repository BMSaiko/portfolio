package com.example.rentacar.Aluguer;

import com.example.rentacar.Veiculo.Veiculo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Carro_aluguer {

    public static ArrayList<Carro_aluguer> carrosAluguerList = new ArrayList<>();
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
    private String seguro;
    private LocalDate data_inic;
    private LocalDate data_fim;
    private int valor;

    public static ArrayList<Carro_aluguer> veiculList = new ArrayList<>();

    /**
     * Construtor da classe Carro_aluguer.
     *
     * @param matricula      A matrícula do carro de aluguer.
     * @param marca          A marca do carro de aluguer.
     * @param modelo         O modelo do carro de aluguer.
     * @param cilindrada     A cilindrada do carro de aluguer.
     * @param cv             Os cavalos de potência do carro de aluguer.
     * @param combustivel    O tipo de combustível do carro de aluguer.
     * @param kms            O número de quilómetros percorridos pelo carro de aluguer.
     * @param prox_revisao   A data da próxima revisão do carro de aluguer.
     * @param ult_inspecao   A data da última inspeção do carro de aluguer.
     * @param limpeza        O estado de limpeza do carro de aluguer.
     * @param categoria      A categoria do carro de aluguer.
     * @param seguro         O estado do seguro do carro de aluguer.
     * @param data_inic      A data de início do aluguer do carro.
     * @param data_fim       A data de fim do aluguer do carro.
     * @param valor          O valor do aluguer do carro.
     */
    public Carro_aluguer(String matricula, String marca, String modelo, int cilindrada, int cv, String combustivel, int kms, LocalDate prox_revisao, LocalDate ult_inspecao, String limpeza, String categoria, String seguro, LocalDate data_inic, LocalDate data_fim, int valor) {
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
        this.seguro = seguro;
        this.data_inic = data_inic;
        this.data_fim = data_fim;
        this.valor = valor;
    }

    /**
     * Obtém a matrícula do carro de aluguer.
     *
     * @return A matrícula do carro de aluguer.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula do carro de aluguer.
     *
     * @param matricula A matrícula do carro de aluguer.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém a marca do carro de aluguer.
     *
     * @return A marca do carro de aluguer.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Define a marca do carro de aluguer.
     *
     * @param marca A marca do carro de aluguer.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtém o modelo do carro de aluguer.
     *
     * @return O modelo do carro de aluguer.
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Define o modelo do carro de aluguer.
     *
     * @param modelo O modelo do carro de aluguer.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * Obtém a cilindrada do carro de aluguer.
     *
     * @return A cilindrada do carro de aluguer.
     */
    public int getCilindrada() {
        return cilindrada;
    }

    /**
     * Define a cilindrada do carro de aluguer.
     *
     * @param cilindrada A cilindrada do carro de aluguer.
     */
    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }

    /**
     * Obtém os cavalos de potência do carro de aluguer.
     *
     * @return Os cavalos de potência do carro de aluguer.
     */
    public int getCv() {
        return cv;
    }

    /**
     * Define os cavalos de potência do carro de aluguer.
     *
     * @param cv Os cavalos de potência do carro de aluguer.
     */
    public void setCv(int cv) {
        this.cv = cv;
    }

    /**
     * Obtém o tipo de combustível do carro de aluguer.
     *
     * @return O tipo de combustível do carro de aluguer.
     */
    public String getCombustivel() {
        return combustivel;
    }

    /**
     * Define o tipo de combustível do carro de aluguer.
     *
     * @param combustivel O tipo de combustível do carro de aluguer.
     */
    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    /**
     * Obtém o número de quilómetros percorridos pelo carro de aluguer.
     *
     * @return O número de quilómetros percorridos pelo carro de aluguer.
     */
    public int getKms() {
        return kms;
    }

    /**
     * Define o número de quilómetros percorridos pelo carro de aluguer.
     *
     * @param kms O número de quilómetros percorridos pelo carro de aluguer.
     */
    public void setKms(int kms) {
        this.kms = kms;
    }

    /**
     * Obtém a data da próxima revisão do carro de aluguer.
     *
     * @return A data da próxima revisão do carro de aluguer.
     */
    public LocalDate getProx_revisao() {
        return prox_revisao;
    }

    /**
     * Define a data da próxima revisão do carro de aluguer.
     *
     * @param prox_revisao A data da próxima revisão do carro de aluguer.
     */
    public void setProx_revisao(LocalDate prox_revisao) {
        this.prox_revisao = prox_revisao;
    }

    /**
     * Obtém a data da última inspeção do carro de aluguer.
     *
     * @return A data da última inspeção do carro de aluguer.
     */
    public LocalDate getUlt_inspecao() {
        return ult_inspecao;
    }

    /**
     * Define a data da última inspeção do carro de aluguer.
     *
     * @param ult_inspecao A data da última inspeção do carro de aluguer.
     */
    public void setUlt_inspecao(LocalDate ult_inspecao) {
        this.ult_inspecao = ult_inspecao;
    }

    /**
     * Obtém o estado de limpeza do carro de aluguer.
     *
     * @return O estado de limpeza do carro de aluguer.
     */
    public String getLimpeza() {
        return limpeza;
    }

    /**
     * Define o estado de limpeza do carro de aluguer.
     *
     * @param limpeza O estado de limpeza do carro de aluguer.
     */
    public void setLimpeza(String limpeza) {
        this.limpeza = limpeza;
    }

    /**
     * Obtém a categoria do carro de aluguer.
     *
     * @return A categoria do carro de aluguer.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Define a categoria do carro de aluguer.
     *
     * @param categoria A categoria do carro de aluguer.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtém o estado do seguro do carro de aluguer.
     *
     * @return O estado do seguro do carro de aluguer.
     */
    public String getSeguro() {
        return seguro;
    }

    /**
     * Define o estado do seguro do carro de aluguer.
     *
     * @param seguro O estado do seguro do carro de aluguer.
     */
    public void setSeguro(String seguro) {
        this.seguro = seguro;
    }

    /**
     * Obtém a data de início do aluguer do carro.
     *
     * @return A data de início do aluguer do carro.
     */
    public LocalDate getData_inic() {
        return data_inic;
    }

    /**
     * Define a data de início do aluguer do carro.
     *
     * @param data_inic A data de início do aluguer do carro.
     */
    public void setData_inic(LocalDate data_inic) {
        this.data_inic = data_inic;
    }

    /**
     * Obtém a data de fim do aluguer do carro.
     *
     * @return A data de fim do aluguer do carro.
     */
    public LocalDate getData_fim() {
        return data_fim;
    }

    /**
     * Define a data de fim do aluguer do carro.
     *
     * @param data_fim A data de fim do aluguer do carro.
     */
    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    /**
     * Obtém o valor do aluguer do carro.
     *
     * @return O valor do aluguer do carro.
     */
    public int getValor() {
        return valor;
    }

    /**
     * Define o valor do aluguer do carro.
     *
     * @param valor O valor do aluguer do carro.
     */
    public void setValor(int valor) {
        this.valor = valor;
    }
}
