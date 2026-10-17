package org.companhiaaerea.utils;

import org.companhiaaerea.Tripulante;

import java.util.Scanner;

/**
 * Classe Console que contém métodos estáticos para interação com o utilizador através do terminal.
 *
 * @author Francisco Silva
 */
public class Console {
    /**
     * Scanner para leitura de dados do utilizador.
     */
    public static Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    /**
     * Constante que contém a sequência de escape ANSI para definir a cor do texto como vermelho.
     */
    public static final String TEXTO_VERMELHO = "\u001B[31m";

    /**
     * Constante que contém a sequência de escape ANSI para redefinir a cor do texto para o padrão.
     */
    public static final String RESET_COR_TEXTO = "\u001B[0m";

    /**
     * Método que lê uma String do utilizador
     *
     * @param mensagem mensagem a ser exibida para o utilizador
     * @return a String lida do utilizador
     */
    public static String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.next();
    }

    /**
     * Método que lê uma String do utilizador, validando a entrada com uma expressão regular
     *
     * @param mensagem     mensagem a ser exibida para o utilizador
     * @param regex        expressão regular para validação da entrada
     * @param mensagemErro mensagem a ser exibida caso a entrada seja inválida
     * @return a String lida e validada do utilizador
     */
    public static String lerString(String mensagem, String regex, String mensagemErro) {
        String valor;
        do {
            valor = lerString(mensagem);
            if (!valor.matches(regex)) {
                System.out.println(mensagemErro);
            }
        } while (!valor.matches(regex));
        return valor;
    }

    /**
     * Método que lê um inteiro do utilizador, validando a entrada como número
     *
     * @param mensagem     mensagem a ser exibida para o utilizador
     * @param mensagemErro mensagem a ser exibida caso a entrada seja inválida
     * @return o inteiro lido e validado do utilizador
     */
    public static int lerInt(String mensagem, String mensagemErro) {
        return Integer.parseInt(lerString(mensagem, "[0-9]+", mensagemErro));
    }

    /**
     * Método que lê um inteiro do utilizador, validando a entrada com uma expressão regular
     *
     * @param mensagem     mensagem a ser exibida para o utilizador
     * @param regex        expressão regular para validação da entrada
     * @param mensagemErro mensagem a ser exibida caso a entrada seja inválida
     * @return o inteiro lido e validado do utilizador
     */
    public static int lerInt(String mensagem, String regex, String mensagemErro) {
        return Integer.parseInt(lerString(mensagem, regex, mensagemErro));
    }

    /**
     * Método que limpa o terminal
     */
    public static void clearConsole() {
        System.out.println(System.lineSeparator().repeat(50));
    }

    /**
     * Este método lê a categoria do tripulante a partir da entrada do utilizador.
     * Imprime todas as categorias disponíveis e pede que o utilizador escolha uma opção.
     * Se a entrada do utilizador for inválida, o método exibirá a mensagem de erro especificada.
     *
     * @param mensagem     String que será exibida ao utilizador antes de pedir a entrada.
     * @param mensagemErro String que será exibida ao utilizador caso a entrada seja inválida.
     * @return a categoria do tripulante escolhida pelo usuário.
     */
    public static Tripulante.Categoria lerCategoria(String mensagem, String mensagemErro) {
        for (Tripulante.Categoria categoria : Tripulante.Categoria.values()) {
            System.out.println(categoria.ordinal() + " - " + categoria);
        }
        int op = lerInt(mensagem, "[0-" + (Tripulante.Categoria.values().length - 1) + "]", mensagemErro);
        return Tripulante.Categoria.values()[op];
    }
}
