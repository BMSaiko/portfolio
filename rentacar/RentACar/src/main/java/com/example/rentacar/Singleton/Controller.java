package com.example.rentacar.Singleton;

/**
 * A classe Controller representa um controlador que utiliza a classe SingletonData para obter informações sobre o nível de acesso.
 */
public class Controller {
    private SingletonData singleton;

    /**
     * Cria uma nova instância do Controller.
     * A instância do Controller utiliza a instância única de SingletonData obtida através do método getInstance().
     */
    public Controller() {
        singleton = SingletonData.getInstance();
    }

    /**
     * Obtém o nível de acesso atual.
     *
     * @return O nível de acesso como um valor inteiro.
     */
    public int getNivelAcesso() {
        return singleton.getNivelAcesso();
    }
}
