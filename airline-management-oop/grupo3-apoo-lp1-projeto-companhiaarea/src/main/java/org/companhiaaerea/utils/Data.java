package org.companhiaaerea.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.companhiaaerea.utils.Console.lerString;

/**
 * Classe Data que contém métodos estáticos para ler e retornar dados de data e hora.
 *
 * @author Francisco Silva
 */
public class Data {
    /**
     * Lê uma data no formato (dd/MM/aaaa) e retorna como um objeto LocalDate.
     *
     * @param mensagem mensagem a ser exibida ao utilizador solicitando a data.
     * @return a data lida como um objeto LocalDate.
     */
    public static LocalDate lerData(String mensagem) {
        return LocalDate.parse(
                lerString(
                        mensagem,
                        "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/([0-9]{4})$",
                        "Data inválida. Por favor insira no formato (dd/MM/aaaa): "
                ), DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }

    /**
     * Lê uma data e hora no formato (dd/MM/aaaa HH:mm) e retorna como um objeto LocalDateTime.
     *
     * @param partidaOuChegada String que define se a data e hora é de partida ou chegada.
     * @return a data e hora lidas como um objeto LocalDateTime.
     */
    public static LocalDateTime lerDataHora(String partidaOuChegada) {
        return LocalDateTime.parse(
                lerString(
                        "Insira a " + partidaOuChegada + " no formato (dd/MM/aaaa HH:mm): ",
                        "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/([0-9]{4}) (2[0-3]|[01][0-9]):([0-5][0-9])$",
                        "Data inválida. Por favor insira no formato (dd/MM/aaaa HH:mm): "
                ), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
}
