package org.example;

/**
 * Classe que representa um avião.
 *
 * @author Francisco Silva
 */
public class Aviao {

    /**
     * O número de registro do avião.
     */
    String numRegisto;

    /**
     * A marca e modelo do avião.
     */
    String marcaModelo;

    /**
     * O número de lugares turísticos no avião.
     */
    int numLugaresTuristica;

    /**
     * O número de lugares executivos no avião.
     */
    int numLugaresExecutiva;

    /**
     * Construtor que inicializa os atributos do avião.
     *
     * @param numRegisto          O número de registro do avião.
     * @param marcaModelo         A marca e modelo do avião.
     * @param numLugaresTuristica O número de lugares turísticos no avião.
     * @param numLugaresExecutiva O número de lugares executivos no avião.
     */
    public Aviao(String numRegisto, String marcaModelo, int numLugaresTuristica, int numLugaresExecutiva) {
        this.numRegisto = numRegisto;
        this.marcaModelo = marcaModelo;
        this.numLugaresTuristica = numLugaresTuristica;
        this.numLugaresExecutiva = numLugaresExecutiva;
    }

    /**
     * Retorna o número de registro do avião.
     *
     * @return O número de registro do avião.
     */
    public String getNumRegisto() {
        return numRegisto;
    }

    /**
     * Atribui um novo número de registro ao avião.
     *
     * @param numRegisto O novo número de registro do avião.
     */
    public void setNumRegisto(String numRegisto) {
        this.numRegisto = numRegisto;
    }

    /**
     * Retorna a marca e modelo do avião.
     *
     * @return A marca e modelo do avião.
     */
    public String getMarcaModelo() {
        return marcaModelo;
    }

    /**
     * Atribui uma nova marca e modelo ao avião.
     *
     * @param marcaModelo A nova marca e modelo do avião.
     */
    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    /**
     * Retorna o número de lugares turísticos no avião.
     *
     * @return O número de lugares turísticos no avião.
     */
    public int getNumLugaresTuristica() {
        return numLugaresTuristica;
    }

    /**
     * Atribui um novo número de lugares turísticos ao avião.
     *
     * @param numLugaresTuristica O novo número de lugares turísticos no avião.
     */
    public void setNumLugaresTuristica(int numLugaresTuristica) {
        this.numLugaresTuristica = numLugaresTuristica;
    }

    /**
     * Retorna o número de lugares executivos no avião.
     *
     * @return O número de lugares executivos no avião.
     */
    public int getNumLugaresExecutiva() {
        return numLugaresExecutiva;
    }

    /**
     * Atribui um novo número de lugares executivos ao avião.
     *
     * @param numLugaresExecutiva O novo número de lugares executivos no avião.
     */
    public void setNumLugaresExecutiva(int numLugaresExecutiva) {
        this.numLugaresExecutiva = numLugaresExecutiva;
    }
}
