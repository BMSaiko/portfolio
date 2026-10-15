package org.companhiaaerea;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.companhiaaerea.utils.Console.*;
import static org.companhiaaerea.utils.Data.lerData;

/**
 * Classe que representa um passageiro. Estende a classe {@link Pessoa}.
 *
 * @author Francisco Silva
 */
public class Passageiro extends Pessoa {
    /**
     * Enum que define as diferentes categorias de bilhete.
     */
    public enum TipoBilhete {
        /**
         * Bilhete de turismo.
         */
        TURISTICA,
        /**
         * Bilhete de negócios.
         */
        EXECUTIVA
    }

    /**
     * O endereço eletrónico do passageiro.
     */
    String email;

    /**
     * A categoria de bilhete do passageiro.
     */
    TipoBilhete tipoBilhete;

    /**
     * O lugar reservado pelo passageiro.
     */
    String lugarReservado;

    /**
     * Construtor vazio.
     */
    public Passageiro() {
    }

    /**
     * Construtor que inicializa os atributos do passageiro.
     *
     * @param numIdentificacao O número de identificação do passageiro.
     * @param nome             O nome do passageiro.
     * @param nacionalidade    A nacionalidade do passageiro.
     * @param morada           A morada do passageiro.
     * @param telefone         O telefone do passageiro.
     * @param dataNascimento   A data de nascimento do passageiro.
     * @param email            O endereço eletrónico do passageiro.
     * @param tipoBilhete      A categoria de bilhete do passageiro.
     * @param lugarReservado   O lugar reservado pelo passageiro.
     */
    public Passageiro(int numIdentificacao, String nome, String nacionalidade, String morada, int telefone, LocalDate dataNascimento, String email, TipoBilhete tipoBilhete, String lugarReservado) {
        super(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento);
        this.email = email;
        this.tipoBilhete = tipoBilhete;
        this.lugarReservado = lugarReservado;
    }

    /**
     * Retorna o endereço eletrónico do passageiro.
     *
     * @return O endereço eletrónico do passageiro.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Atribui um novo endereço eletrónico ao passageiro.
     *
     * @param email O novo endereço eletrónico do passageiro.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna a categoria de bilhete do passageiro.
     *
     * @return A categoria de bilhete do passageiro.
     */
    public TipoBilhete getTipoBilhete() {
        return tipoBilhete;
    }

    /**
     * Atribui uma nova categoria de bilhete ao passageiro.
     *
     * @param tipoBilhete O novo categoria de bilhete do passageiro.
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

    /**
     * Método que lê os dados dos passageiros e os adiciona a uma lista.
     * O utilizador deve inserir 0 no número de identificação para encerrar a entrada de dados.
     *
     * @return A lista de passageiros com os dados inseridos pelo utilizador.
     */
    static ArrayList<Passageiro> ler() {
        ArrayList<Passageiro> passageiros = new ArrayList<>();

        while (true) {
            System.out.println("Insira os dados do passageiro (0 no número de identificação para terminar): ");

            int numIdentificacao = lerInt(
                    "- Insira o número de identificação: ",
                    "O número de identificação deve ser um número inteiro positivo."
            );
            if (numIdentificacao == 0) break; // Terminar a entrada de dados

            String nome = lerString("- Insira o nome: ");
            String nacionalidade = lerString("- Insira a nacionalidade: ");
            String morada = lerString("- Insira a morada: ");

            int telefone = lerInt(
                    "- Insira o número de telefone: ",
                    "[0-9]{9}", // Regex para validar o número de telefone (9 digitos inteiros)
                    "O número de telefone deve conter 9 dígitos. Insira um número de telefone válido."
            );

            LocalDate dataNascimento = lerData("- Insira a data de nascimento (dd/MM/aaaa): ");

            String email = lerString("- Insira o email: ",
                    "^(.+)@(\\S+)$", // Regex para validar o e-mail
                    "O email inserido não é válido. Insira um email válido: "
            );

            TipoBilhete tipoBilhete = lerString(
                    "- Insira o tipo de bilhete (1 - Turística, 2 - Executiva): ",
                    "[1-2]", // Regex para validar o categoria de bilhete (1 ou 2)
                    "O tipo de bilhete deve ser 1 ou 2."
            ).equals("1") ? TipoBilhete.TURISTICA : TipoBilhete.EXECUTIVA;


            String lugarReservado = lerString(
                    "- Insira o lugar do passageiro (AXX): ",
                    "[A-Z][0-9]{2}", // Regex para validar o lugar reservado (letra seguida de 2 digitos inteiros)
                    "O lugar reservado deve ser do tipo AXX, onde A é uma letra e XX é um número."
            );

            Passageiro passageiro = new Passageiro(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento, email, tipoBilhete, lugarReservado);

            passageiros.add(passageiro);
        }

        clearConsole();

        return passageiros;
    }

    /**
     * Retorna uma representação textual do Passageiro.
     *
     * @return Uma representação textual do Passageiro.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Email: ").append(email).append("\n");
        sb.append("Tipo de bilhete: ").append(tipoBilhete).append("\n");
        sb.append("Lugar reservado: ").append(lugarReservado).append("\n");
        return sb.toString();
    }
}
