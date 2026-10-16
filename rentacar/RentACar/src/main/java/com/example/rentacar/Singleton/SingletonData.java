package com.example.rentacar.Singleton;

/**
 * A classe SingletonData implementa o padrão Singleton e representa uma classe que contém dados compartilhados.
 * Apenas uma instância de SingletonData pode existir em todo o sistema.
 */
public class SingletonData {
    private static SingletonData instance;
    private String data;

    private int id;
    private int nivelAcesso;

    /**
     * Construtor privado da classe SingletonData.
     * Impede a criação de instâncias diretas de SingletonData.
     */
    private SingletonData() {

    }

    /**
     * Obtém a instância única de SingletonData.
     *
     * @return A instância única de SingletonData.
     */
    public static SingletonData getInstance() {
        if (instance == null) {
            instance = new SingletonData();
        }
        return instance;
    }

    /**
     * Obtém o nível de acesso atual.
     *
     * @return O nível de acesso como um valor inteiro.
     */
    public int getNivelAcesso() {
        return nivelAcesso;
    }

    /**
     * Define o nível de acesso.
     *
     * @param nivelAcesso O novo nível de acesso a ser definido.
     */
    public void setNivelAcesso(int nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    /**
     * Obtém os dados armazenados.
     *
     * @return Os dados como uma string.
     */
    public String getData() {
        return data;
    }

    /**
     * Define os dados a serem armazenados.
     *
     * @param data Os dados a serem definidos.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Obtém o ID.
     *
     * @return O ID como um valor inteiro.
     */
    public int getId() {
        return id;
    }

    /**
     * Define o ID.
     *
     * @param id O novo ID a ser definido.
     */
    public void setId(int id) {
        this.id = id;
    }
}
