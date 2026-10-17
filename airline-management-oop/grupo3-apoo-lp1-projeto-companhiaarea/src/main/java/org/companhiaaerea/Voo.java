package org.companhiaaerea;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.companhiaaerea.Main.voos;
import static org.companhiaaerea.utils.Console.lerString;
import static org.companhiaaerea.utils.Data.lerDataHora;

/**
 * Classe que representa um voo de avião.
 *
 * @author Ricardo Pereira
 */
public class Voo {

    /**
     * Número do voo
     */
    String numVoo;

    /**
     * Objeto Avião que fará o voo
     */
    Aviao aviao;

    /**
     * Aeroporto de origem do voo
     */
    Aeroporto aeroportoOrigem;

    /**
     * Aeroporto de destino do voo
     */
    Aeroporto aeroportoDestino;

    /**
     * Data e hora de partida do voo
     */
    LocalDateTime dataHoraPartida;

    /**
     * Data e hora de chegada do voo
     */
    LocalDateTime dataHoraChegada;

    /**
     * Lista de passageiros que estarão presentes no voo
     */
    ArrayList<Passageiro> passageiros;

    /**
     * Lista de tripulantes que estarão presentes no voo
     */
    ArrayList<Tripulante> tripulacao;

    /**
     * Construtor vazio.
     */
    public Voo() {
    }

    /**
     * Constrói um objeto Voo com os atributos especificados.
     *
     * @param numVoo           O número do voo.
     * @param aviao            O objeto Avião que fará o voo.
     * @param aeroportoOrigem  O aeroporto de origem do voo.
     * @param aeroportoDestino O aeroporto de destino do voo.
     * @param dataHoraPartida  A data e hora de partida do voo.
     * @param dataHoraChegada  A data e hora de chegada do voo.
     * @param passageiros      A lista de passageiros que estarão presentes no voo.
     * @param tripulacao       A lista de tripulantes que estarão presentes no voo.
     */
    public Voo(String numVoo, Aviao aviao, Aeroporto aeroportoOrigem, Aeroporto aeroportoDestino, LocalDateTime dataHoraPartida, LocalDateTime dataHoraChegada, ArrayList<Passageiro> passageiros, ArrayList<Tripulante> tripulacao) {
        this.numVoo = numVoo;
        this.aviao = aviao;
        this.aeroportoOrigem = aeroportoOrigem;
        this.aeroportoDestino = aeroportoDestino;
        this.dataHoraPartida = dataHoraPartida;
        this.dataHoraChegada = dataHoraChegada;
        this.passageiros = passageiros;
        this.tripulacao = tripulacao;
    }

    /**
     * Retorna o número do voo.
     *
     * @return O número do voo.
     */
    public String getNumVoo() {
        return numVoo;
    }

    /**
     * Altera o número do voo.
     *
     * @param numVoo O novo número do voo.
     */
    public void setNumVoo(String numVoo) {
        this.numVoo = numVoo;
    }

    /**
     * Retorna o objeto Avião que fará o voo.
     *
     * @return O objeto Avião que fará o voo.
     */
    public Aviao getAviao() {
        return aviao;
    }

    /**
     * Altera o objeto Avião que fará o voo.
     *
     * @param aviao O novo objeto Avião que fará o voo.
     */
    public void setAviao(Aviao aviao) {
        this.aviao = aviao;
    }

    /**
     * Retorna o aeroporto de origem do voo.
     *
     * @return O aeroporto de origem do voo.
     */
    public Aeroporto getAeroportoOrigem() {
        return aeroportoOrigem;
    }

    /**
     * Altera o aeroporto de origem do voo.
     *
     * @param aeroportoOrigem O novo aeroporto de origem do voo.
     */
    public void setAeroportoOrigem(Aeroporto aeroportoOrigem) {
        this.aeroportoOrigem = aeroportoOrigem;
    }

    /**
     * Retorna o aeroporto de destino do voo.
     *
     * @return O aeroporto de destino do voo.
     */
    public Aeroporto getAeroportoDestino() {
        return aeroportoDestino;
    }

    /**
     * Altera o aeroporto de destino do voo.
     *
     * @param aeroportoDestino O novo aeroporto de destino do voo.
     */
    public void setAeroportoDestino(Aeroporto aeroportoDestino) {
        this.aeroportoDestino = aeroportoDestino;
    }

    /**
     * Retorna a data e hora de partida do voo.
     *
     * @return A data e hora de partida do voo.
     */
    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    /**
     * Altera a data e hora de partida do voo.
     *
     * @param dataHoraPartida A nova data e hora de partida do voo.
     */
    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    /**
     * Retorna a data e hora de chegada do voo.
     *
     * @return A data e hora de chegada do voo.
     */
    public LocalDateTime getDataHoraChegada() {
        return dataHoraChegada;
    }

    /**
     * Altera a data e hora de chegada do voo.
     *
     * @param dataHoraChegada A nova data e hora de chegada do voo.
     */
    public void setDataHoraChegada(LocalDateTime dataHoraChegada) {
        this.dataHoraChegada = dataHoraChegada;
    }

    /**
     * Retorna a lista de passageiros que estarão presentes no voo.
     *
     * @return A lista de passageiros que estarão presentes no voo.
     */
    public ArrayList<Passageiro> getPassageiros() {
        return passageiros;
    }

    /**
     * Altera a lista de passageiros que estarão presentes no voo.
     *
     * @param passageiros A nova lista de passageiros que estarão presentes no voo.
     */
    public void setPassageiros(ArrayList<Passageiro> passageiros) {
        this.passageiros = passageiros;
    }

    /**
     * Retorna a lista de tripulantes que estarão presentes no voo.
     *
     * @return A lista de tripulantes que estarão presentes no voo.
     */
    public ArrayList<Tripulante> getTripulacao() {
        return tripulacao;
    }

    /**
     * Altera a lista de tripulantes que estarão presentes no voo.
     *
     * @param tripulacao A nova lista de tripulantes que estarão presentes no voo.
     */
    public void setTripulacao(ArrayList<Tripulante> tripulacao) {
        this.tripulacao = tripulacao;
    }

    /**
     * Método que lê as informações de um voo.
     *
     * @return o objeto Voo com as informações lidas.
     */
    public static Voo ler() {
        String numVoo = lerString(
                "Insira número de voo (VXXX): ",
                "^[Vv]\\d{3}$", // Regex que verifica se o número de voo está no formato VXXX
                "Número de voo inválido. Insira um número de voo válido (VXXX)."
        );

        if (numVoo != null) numVoo = numVoo.toUpperCase();

        for (Voo voo : voos) {
            if (voo.getNumVoo().equals(numVoo)) {
                System.out.println("Número de voo já registado. Insira um número de voo válido.");
                return ler();
            }
        }

        return new Voo(
                numVoo,
                Aviao.ler(),
                Aeroporto.ler("partida"),
                Aeroporto.ler("chegada"),
                lerDataHora("partida"),
                lerDataHora("chegada"),
                Passageiro.ler(),
                Tripulante.ler()
        );
    }

    /**
     * Retorna uma representação textual do Voo.
     *
     * @return Uma representação textual do Voo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Numero do Voo: ").append(numVoo).append("\n");
        sb.append("Avião: ").append(aviao).append("\n");
        sb.append("Aeroporto de origem: ").append(aeroportoOrigem).append("\n");
        sb.append("Aeroporto de destino: ").append(aeroportoDestino).append("\n");
        sb.append("Data e hora de partida: ").append(dataHoraPartida).append("\n");
        sb.append("Data e hora de chegada: ").append(dataHoraChegada).append("\n");
        sb.append("Passageiros: ").append(passageiros).append("\n");
        sb.append("Tripulantes: ").append(tripulacao).append("\n");
        return sb.toString();
    }
}