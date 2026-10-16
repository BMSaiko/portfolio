package com.example.rentacar.Aluguer;

import java.time.LocalDate;
import java.util.ArrayList;

public class Aluguer extends Orcamento {

    private String estado;

    public static ArrayList<Aluguer> aluguerList = new ArrayList<>();

    /**
     * Construtor da classe Aluguer.
     *
     * @param id               O ID do aluguer.
     * @param valor            O valor do aluguer.
     * @param estado_orcamento O estado do orçamento.
     * @param nome_cliente     O nome do cliente.
     * @param nome_funcionario O nome do funcionário.
     * @param matricula        A matrícula do carro alugado.
     * @param data_inic        A data de início do aluguer.
     * @param data_fim         A data de fim do aluguer.
     * @param n_carros         O número de carros alugados.
     * @param estado           O estado do aluguer.
     */
    public Aluguer(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario, String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros, String estado) {
        super(id, valor, estado_orcamento, nome_cliente, nome_funcionario, matricula, data_inic, data_fim, n_carros);
        this.estado = estado;
    }

    /**
     * Obtém o estado do aluguer.
     *
     * @return O estado do aluguer.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define o estado do aluguer.
     *
     * @param estado O estado do aluguer.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
