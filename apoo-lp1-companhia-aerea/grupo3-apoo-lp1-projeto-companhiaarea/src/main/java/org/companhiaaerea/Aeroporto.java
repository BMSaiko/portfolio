package org.companhiaaerea;

import static org.companhiaaerea.utils.Console.lerString;
import static org.companhiaaerea.utils.Console.clearConsole;

/**
 * Classe que representa um aeroporto.
 *
 * @author Ricardo Pereira
 */
public class Aeroporto {
    /**
     * A sigla do aeroporto.
     */
    String sigla;

    /**
     * O país onde o aeroporto está localizado.
     */
    String pais;

    /**
     * A cidade mais próxima do aeroporto.
     */
    String cidadeMaisProxima;

    /**
     * Construtor vazio.
     */
    public Aeroporto() {
    }

    /**
     * Construtor que inicializa os atributos do aeroporto.
     *
     * @param sigla             A sigla do aeroporto.
     * @param pais              O país onde o aeroporto está localizado.
     * @param cidadeMaisProxima A cidade mais próxima do aeroporto.
     */
    public Aeroporto(String sigla, String pais, String cidadeMaisProxima) {
        this.sigla = sigla;
        this.pais = pais;
        this.cidadeMaisProxima = cidadeMaisProxima;
    }

    /**
     * Retorna a sigla do aeroporto.
     *
     * @return A sigla do aeroporto.
     */
    public String getSigla() {
        return sigla;
    }

    /**
     * Atribui uma nova sigla ao aeroporto.
     *
     * @param sigla A nova sigla do aeroporto.
     */
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    /**
     * Retorna o país onde o aeroporto está localizado.
     *
     * @return O país onde o aeroporto está localizado.
     */
    public String getPais() {
        return pais;
    }

    /**
     * Atribui um novo país ao aeroporto.
     *
     * @param pais O novo país onde o aeroporto está localizado.
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Retorna a cidade mais próxima do aeroporto.
     *
     * @return A cidade mais próxima do aeroporto.
     */
    public String getCidadeMaisProxima() {
        return cidadeMaisProxima;
    }

    /**
     * Atribui uma nova cidade mais próxima ao aeroporto.
     *
     * @param cidadeMaisProxima A nova cidade mais próxima do aeroporto.
     */
    public void setCidadeMaisProxima(String cidadeMaisProxima) {
        this.cidadeMaisProxima = cidadeMaisProxima;
    }

    /**
     * Método para ler os dados de um aeroporto.
     *
     * @param partidaOuChegada ‘String’ indicando se o aeroporto é de partida ou de chegada.
     * @return Um objeto Aeroporto com os dados lidos.
     */
    static Aeroporto ler(String partidaOuChegada) {
        System.out.println("Insira os dados do aeroporto de " + partidaOuChegada + ":");

        String sigla = lerString(
                "Insira a sigla (AAA): ",
                "^[A-Za-z]{3}$", // Expressão regular para 3 letras maiúsculas.
                "A sigla deve ter 3 letras maiúsculas."
        ).toUpperCase();

        String pais = lerString(
                "Insira o país: ",
                "^[A-Za-z ]+$", // Expressão regular para letras e espaços.
                "O país deve ter apenas letras."
        );

        String cidadeMaisProx = lerString(
                "Insira a cidade mais próxima: ",
                "^[A-Za-z ]+$", // Expressão regular para letras e espaços.
                "A cidade mais próxima deve ter apenas letras."
        );

        clearConsole();

        return new Aeroporto(sigla, pais, cidadeMaisProx);
    }

    /**
     * Método para imprimir os dados de um aeroporto.
     *
     * @return Uma String com os dados do aeroporto.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sigla do aeroporto: ").append(sigla).append("\n");
        sb.append("País: ").append(pais).append("\n");
        sb.append("Cidade mais próxima: ").append(cidadeMaisProxima).append("\n");
        return sb.toString();
    }
}
