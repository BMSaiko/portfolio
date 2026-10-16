package com.example.rentacar.Aluguer;

import com.example.rentacar.Aluguer.pagamento;

import java.time.LocalDate;

public class recibo extends pagamento {

    private int Data_transacao;

    private String morada;


    /**

     Constructs a new Recibo object with the specified parameters.
     @param id The ID of the recibo.
     @param valor The value of the recibo.
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
     @param data_transacao The transaction date of the recibo.
     @param morada The address associated with the recibo.
     */
    public recibo(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario,
                  String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros, String estado,
                  int id1, String estado_pagamento, String tipo_pagamento, int data_transacao, String morada) {
        super(id, valor, estado_orcamento, nome_cliente, nome_funcionario, matricula, data_inic, data_fim,
                n_carros, estado, id1, estado_pagamento, tipo_pagamento);
        Data_transacao = data_transacao;
        this.morada = morada;
    }
    /**

     Returns the transaction date of the recibo.
     @return The transaction date of the recibo.
     */
    public int getData_transacao() {
        return Data_transacao;
    }
    /**

     Sets the transaction date of the recibo.
     @param data_transacao The transaction date of the recibo to set.
     */
    public void setData_transacao(int data_transacao) {
        Data_transacao = data_transacao;
    }
    /**

     Returns the address associated with the recibo.
     @return The address associated with the recibo.
     */
    public String getMorada() {
        return morada;
    }
    /**

     Sets the address associated with the recibo.
     @param morada The address to set for the recibo.
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }
}
