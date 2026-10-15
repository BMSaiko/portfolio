package org.companhiaaerea;

import static org.companhiaaerea.utils.Console.*;

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
     * Construtor vazio.
     */
    public Aviao() {
    }

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

    /**
     * Método para ler os dados do avião.
     *
     * @return um objeto Avião com os dados lidos.
     */
    static Aviao ler() {
        System.out.println("Insira agora os dados do avião: ");

        String numRegisto = lerString(
                "- Insira o número de registo (ex: CS-TUJ): ",
                "^[A-Za-z]{2}-[A-Za-z]{3}$", // regex que valida o formato AA-AAA
                "O número de registo deve seguir o formato AA-AAA."
        ).toUpperCase();

        String marcaModelo = lerString(
                "- Insira a marca e modelo (Marca, Modelo): ",
                "^[A-Za-z]+, [A-Za-z0-9]+$", // regex que valida letras seguidas de uma vírgula seguida de mais letras
                "A marca e modelo devem ser separados por uma vírgula."
        );

        int numLugaresTuristica = lerInt(
                "- Insira o número de lugares na classe turística: ",
                "O número de lugares deve ser um número inteiro."
        );

        int numLugaresExecutiva = lerInt(
                "- Insira o número de lugares na classe executiva: ",
                "O número de lugares deve ser um número inteiro."
        );

        clearConsole();

        return new Aviao(numRegisto, marcaModelo, numLugaresTuristica, numLugaresExecutiva);
    }

    /**
     * Retorna uma representação textual do Avião.
     *
     * @return Uma representação textual do Avião.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Número de registro: ").append(numRegisto).append("\n");
        sb.append("Marca e modelo: ").append(marcaModelo).append("\n");
        sb.append("Número de lugares turísticos: ").append(numLugaresTuristica).append("\n");
        sb.append("Número de lugares executivos: ").append(numLugaresExecutiva).append("\n");
        return sb.toString();
    }
}
