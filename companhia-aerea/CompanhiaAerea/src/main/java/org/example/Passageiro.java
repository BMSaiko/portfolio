package org.example;

import java.time.LocalDate;

/**
 * Classe que representa um passageiro. Estende a classe {@link Pessoa}.
 *
 * @author Francisco Silva
 */
public class Passageiro extends Pessoa {

    /**
     * Enum que representa o tipo de bilhete de um passageiro.
     */
    enum TipoBilhete {
        TURISTICA, EXECUTIVA
    }

    /**
     * O email do passageiro.
     */
    String email;

    /**
     * O tipo de bilhete do passageiro.
     */
    TipoBilhete tipoBilhete;

    /**
     * O lugar reservado pelo passageiro.
     */
    String lugarReservado;

    /**
     * Construtor que inicializa os atributos do passageiro.
     *
     * @param numIdentificacao O número de identificação do passageiro.
     * @param nome             O nome do passageiro.
     * @param nacionalidade    A nacionalidade do passageiro.
     * @param morada           A morada do passageiro.
     * @param telefone         O telefone do passageiro.
     * @param dataNascimento   A data de nascimento do passageiro.
     * @param email            O email do passageiro.
     * @param tipoBilhete      O tipo de bilhete do passageiro.
     * @param lugarReservado   O lugar reservado pelo passageiro.
     */
    public Passageiro(int numIdentificacao, String nome, String nacionalidade, String morada, int telefone, LocalDate dataNascimento, String email, TipoBilhete tipoBilhete, String lugarReservado) {
        super(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento);
        this.email = email;
        this.tipoBilhete = tipoBilhete;
        this.lugarReservado = lugarReservado;
    }

    /**
     * Retorna o email do passageiro.
     *
     * @return O email do passageiro.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Atribui um novo email ao passageiro.
     *
     * @param email O novo email do passageiro.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna o tipo de bilhete do passageiro.
     *
     * @return O tipo de bilhete do passageiro.
     */
    public TipoBilhete getTipoBilhete() {
        return tipoBilhete;
    }

    /**
     * Atribui um novo tipo de bilhete ao passageiro.
     *
     * @param tipoBilhete O novo tipo de bilhete do passageiro.
     */
    public void setTipoBilhete(TipoBilhete tipoBilhete) {
        this.tipoBilhete = tipoBilhete;
    }

    /**
     * Retorna o lugar reservado pelo passageiro.
     *
     * @return O lugar reservado pelo passageiro.
     */
    public String getLugarReservado() {
        return lugarReservado;
    }

    /**
     * Atribui um novo lugar reservado ao passageiro.
     *
     * @param lugarReservado O novo lugar reservado pelo passageiro.
     */
    public void setLugarReservado(String lugarReservado) {
        this.lugarReservado = lugarReservado;
    }
}
