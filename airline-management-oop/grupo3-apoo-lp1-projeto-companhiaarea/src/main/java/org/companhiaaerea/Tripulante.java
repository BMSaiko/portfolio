package org.companhiaaerea;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.companhiaaerea.utils.Console.*;
import static org.companhiaaerea.utils.Data.lerData;

/**
 * Classe que representa um tripulante.
 *
 * @author Bruno Silva
 */
public class Tripulante extends Pessoa {
    /**
     * Enum que define as diferentes categorias do tripulante
     */
    public enum Categoria {
        /**
         * Piloto
         */
        PILOTO,
        /**
         * Tripulante
         */
        TRIPULANTE,
        /**
         * Co-piloto
         */
        COPILOTO,
        /**
         * Manutenção
         */
        MANUTENCAO,
        /**
         * Segurança
         */
        SEGURANCA
    }

    /**
     * O número da licença do tripulante.
     */
    int numLicensa;

    /**
     * A data de validade da licença do tripulante.
     */
    LocalDate dataValidadeLicensa;

    /**
     * A lista de anotações do tripulante.
     */
    String anotacoes;

    /**
     * O tipo de tripulante.
     */
    Categoria categoria;

    /**
     * Construtor vazio
     */
    public Tripulante() {
    }

    /**
     * Cria um Tripulante com os dados especificados.
     *
     * @param numIdentificacao    o número de identificação do tripulante
     * @param nome                o nome do tripulante
     * @param nacionalidade       a nacionalidade do tripulante
     * @param morada              a morada do tripulante
     * @param telefone            o telefone do tripulante
     * @param dataNascimento      a data de nascimento do tripulante
     * @param numLicensa          o número da licença do tripulante
     * @param dataValidadeLicensa a data de validade da licença do tripulante
     * @param anotacoes           as anotações da licença do tripulante
     * @param categoria           a categoria da licença do tripulante
     */
    public Tripulante(int numIdentificacao, String nome, String nacionalidade, String morada, int telefone, LocalDate dataNascimento, int numLicensa, LocalDate dataValidadeLicensa, String anotacoes, Categoria categoria) {
        super(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento);
        this.numLicensa = numLicensa;
        this.dataValidadeLicensa = dataValidadeLicensa;
        this.anotacoes = anotacoes;
        this.categoria = categoria;
    }

    /**
     * Retorna o número da licença do tripulante.
     *
     * @return O número da licença do tripulante.
     */
    public int getNumLicensa() {
        return numLicensa;
    }

    /**
     * Atribui um novo número de licença ao tripulante.
     *
     * @param numLicensa O novo número de licença do tripulante.
     */
    public void setNumLicensa(int numLicensa) {
        this.numLicensa = numLicensa;
    }

    /**
     * Retorna a data de validade da licença do tripulante.
     *
     * @return A data de validade da licença do tripulante.
     */
    public LocalDate getDataValidadeLicensa() {
        return dataValidadeLicensa;
    }

    /**
     * Atribui uma nova data de validade da licença ao tripulante.
     *
     * @param dataValidadeLicensa A nova data de validade da licença do tripulante.
     */
    public void setDataValidadeLicensa(LocalDate dataValidadeLicensa) {
        this.dataValidadeLicensa = dataValidadeLicensa;
    }

    /**
     * Retorna a lista de anotações do tripulante.
     *
     * @return A lista de anotações do tripulante.
     */
    public String getAnotacoes() {
        return anotacoes;
    }

    /**
     * Atribui uma nova lista de anotações ao tripulante.
     *
     * @param anotacoes A nova lista de anotações do tripulante.
     */
    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    /**
     * Retorna a categoria do tripulante.
     *
     * @return A categoria do tripulante.
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Atribui uma nova categoria ao tripulante.
     *
     * @param categoria A nova categoria do tripulante.
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Método estático que lê os dados de um tripulante a partir de entrada do utilizador e os armazena numa lista.
     * O utilizador pode inserir vários tripulantes até que insira um número de identificação igual a 0.
     *
     * @return ArrayList<Tripulante> - lista de tripulantes lidos do utilizador
     */
    static ArrayList<Tripulante> ler() {
        ArrayList<Tripulante> tripulantes = new ArrayList<>();

        while (true) {
            System.out.println("Insira os dados do tripulante (0 para terminar): ");

            int numIdentificacao = lerInt(
                    "- Insira o número de identificação: ",
                    "O número de identificação deve ser um número inteiro positivo."
            );
            if (numIdentificacao == 0) break; // Termina o ciclo

            String nome = lerString("- Insira o nome: ");

            String nacionalidade = lerString("- Insira a nacionalidade: ");

            String morada = lerString("- Insira a morada: ");

            int telefone = lerInt(
                    "- Insira o número de telefone: ",
                    "[0-9]{9}", // Regex para validar o número de telefone (9 digitos inteiros)
                    "O número de telefone deve conter 9 dígitos inteiros."
            );

            LocalDate dataNascimento = lerData("- Insira a data de nascimento (dd/MM/aaaa): ");

            int numLicenca = lerInt(
                    "- Insira o número da licença: ",
                    "O número da licença deve conter apenas números."
            );

            LocalDate dataValidadeLicenca = lerData("- Insira a data de validade da licença (dd/MM/aaaa): ");

            String anotacoes = lerString("- Insira as anotações: ");

            Categoria categoria = lerCategoria("- Insira a categoria do tripulante: ", "Insira um valor válido.");

            Tripulante tripulante = new Tripulante(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento, numLicenca, dataValidadeLicenca, anotacoes, categoria);

            tripulantes.add(tripulante);
        }

        clearConsole();

        return tripulantes;
    }

    /**
     * Retorna uma representação textual do Tripulante.
     *
     * @return Uma representação textual do Tripulante.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Número de identificação: ").append(numIdentificacao).append("\n");
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Nacionalidade: ").append(nacionalidade).append("\n");
        sb.append("Morada: ").append(morada).append("\n");
        sb.append("Telefone: ").append(telefone).append("\n");
        sb.append("Data de nascimento: ").append(dataNascimento).append("\n");
        sb.append("Número de licença: ").append(numLicensa).append("\n");
        sb.append("Data de validade da licença: ").append(dataValidadeLicensa).append("\n");
        sb.append("Anotações: ").append(anotacoes).append("\n");
        sb.append("Categoria: ").append(categoria).append("\n");
        return sb.toString();
    }
}

