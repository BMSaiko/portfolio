package org.companhiaaerea;

import java.time.LocalDate;

/**
 * Classe que representa uma pessoa.
 *
 * @author Bruno Silva
 */
public class Pessoa {

    /**
     * O número de identificação da pessoa.
     */
    int numIdentificacao;

    /**
     * O nome da pessoa.
     */
    String nome;

    /**
     * A nacionalidade da pessoa.
     */
    String nacionalidade;

    /**
     * A morada da pessoa.
     */
    String morada;

    /**
     * O telefone da pessoa.
     */
    int telefone;

    /**
     * A data de nascimento da pessoa.
     */
    LocalDate dataNascimento;

    /**
     * Construtor vazio
     */
    public Pessoa() {
    }

    /**
     * Construtor que inicializa os atributos da pessoa.
     *
     * @param numIdentificacao O número de identificação da pessoa.
     * @param nome             O nome da pessoa.
     * @param nacionalidade    A nacionalidade da pessoa.
     * @param morada           A morada da pessoa.
     * @param telefone         O telefone da pessoa.
     * @param dataNascimento   A data de nascimento da pessoa.
     */
    public Pessoa(int numIdentificacao, String nome, String nacionalidade, String morada, int telefone, LocalDate dataNascimento) {
        this.numIdentificacao = numIdentificacao;
        this.nome = nome;
        this.nacionalidade = nacionalidade;
        this.morada = morada;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }

    /**
     * Retorna o número de identificação da pessoa.
     *
     * @return O número de identificação da pessoa.
     */
    public int getNumIdentificacao() {
        return numIdentificacao;
    }

    /**
     * Atribui um novo número de identificação à pessoa.
     *
     * @param numIdentificacao O novo número de identificação da pessoa.
     */
    public void setNumIdentificacao(int numIdentificacao) {
        this.numIdentificacao = numIdentificacao;
    }

    /**
     * Retorna o nome da pessoa.
     *
     * @return O nome da pessoa.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Atribui um novo nome à pessoa.
     *
     * @param nome O novo nome da pessoa.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Retorna a nacionalidade da pessoa.
     *
     * @return A nacionalidade da pessoa.
     */
    public String getNacionalidade() {
        return nacionalidade;
    }

    /**
     * Atribui uma nova nacionalidade à pessoa.
     *
     * @param nacionalidade A nova nacionalidade da pessoa.
     */
    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    /**
     * Retorna a morada da pessoa.
     *
     * @return A morada da pessoa.
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Atribui uma nova morada à pessoa.
     *
     * @param morada A nova morada da pessoa.
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * Retorna o telefone da pessoa.
     *
     * @return O telefone da pessoa.
     */
    public int getTelefone() {
        return telefone;
    }

    /**
     * Atribui um novo telefone à pessoa.
     *
     * @param telefone O novo telefone da pessoa.
     */
    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    /**
     * Retorna a data de nascimento da pessoa.
     *
     * @return A data de nascimento da pessoa.
     */
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Atribui uma nova data de nascimento à pessoa.
     *
     * @param dataNascimento A nova data de nascimento da pessoa.
     */
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Retorna uma representação textual da Pessoa.
     *
     * @return Uma representação textual da Pessoa.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Número de identificação: ").append(numIdentificacao).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Nacionalidade: ").append(nacionalidade).append("\n");
        sb.append("Morada: ").append(morada).append("\n");
        sb.append("Telefone: ").append(telefone).append("\n");
        sb.append("Data de Nascimento: ").append(dataNascimento).append("\n");
        return sb.toString();
    }
}
