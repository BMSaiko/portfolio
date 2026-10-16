package com.example.rentacar.Aluguer;

import com.example.rentacar.Aluguer.Aluguer;

import java.time.LocalDate;

public class pagamento extends Aluguer {

    private int id;

    private String estado_pagamento;

    private String tipo_pagamento;

    /**

     Constructs a new Pagamento object with the specified parameters.
     @param id The ID of the pagamento.
     @param valor The value of the pagamento.
     @param estado_orcamento The state of the orcamento.
     @param nome_cliente The name of the client.
     @param nome_funcionario The name of the employee.
     @param matricula The vehicle registration number.
     @param data_inic The start date of the orcamento.
     @param data_fim The end date of the orcamento.
     @param n_carros The number of cars in the orcamento.
     @param estado The state of the orcamento.
     @param id1 The ID of the pagamento.
     @param estado_pagamento The state of the pagamento.
     @param tipo_pagamento The type of payment.
     */
    public pagamento(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario,
                     String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros, String estado,
                     int id1, String estado_pagamento, String tipo_pagamento) {
        super(id, valor, estado_orcamento, nome_cliente, nome_funcionario, matricula, data_inic, data_fim, n_carros, estado);
        this.id = id1;
        this.estado_pagamento = estado_pagamento;
        this.tipo_pagamento = tipo_pagamento;
    }
    /**

     Returns the ID of the pagamento.
     @return The ID of the pagamento.
     */
    public int getId() {
        return id;
    }
    /**

     Sets the ID of the pagamento.
     @param id The ID of the pagamento to set.
     */
    public void setId(int id) {
        this.id = id;
    }
    /**

     Returns the state of the pagamento.
     @return The state of the pagamento.
     */
    public String getEstado_pagamento() {
        return estado_pagamento;
    }
    /**

     Sets the state of the pagamento.
     @param estado_pagamento The state of the pagamento to set.
     */
    public void setEstado_pagamento(String estado_pagamento) {
        this.estado_pagamento = estado_pagamento;
    }
    /**

     Returns the type of payment.
     @return The type of payment.
     */
    public String getTipo_pagamento() {
        return tipo_pagamento;
    }
    /**

     Sets the type of payment.
     @param tipo_pagamento The type of payment to set.
     */
    public void setTipo_pagamento(String tipo_pagamento) {
        this.tipo_pagamento = tipo_pagamento;
    }
}