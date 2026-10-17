package org.example;

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
}
