package com.example.rentacar.Aluguer;

import java.time.LocalDate;

public class fatura extends pagamento {
    private int dias;
    private float custos_reparacoes;

    private LocalDate data_emissao;

    private int numero_fatura;

    private int NIF_empresa;

    private int taxa;

    /**
     Constrói um novo objeto fatura com os parâmetros especificados.
     @param id O ID da fatura.
     @param valor O valor da fatura.
     @param estado_orcamento O estado_orcamento da fatura.
     @param nome_cliente O nome_cliente da fatura.
     @param nome_funcionario O nome_funcionario da fatura.
     @param matricula A matricula da fatura.
     @param data_inic A data_inic da fatura.
     @param data_fim A data_fim da fatura.
     @param n_carros O n_carros da fatura.
     @param estado O estado da fatura.
     @param id1 O id1 da fatura.
     @param estado_pagamento O estado_pagamento da fatura.
     @param tipo_pagamento O tipo_pagamento da fatura.
     @param dias Os dias da fatura.
     @param custos_reparacoes Os custos_reparacoes da fatura.
     @param data_emissao A data_emissao da fatura.
     @param numero_fatura O numero_fatura da fatura.
     @param NIF_empresa O NIF_empresa da fatura.
     @param taxa A taxa da fatura.
     */
    public fatura(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario, String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros, String estado, int id1, String estado_pagamento, String tipo_pagamento, int dias, float custos_reparacoes, LocalDate data_emissao, int numero_fatura, int NIF_empresa, int taxa) {
        super(id, valor, estado_orcamento, nome_cliente, nome_funcionario, matricula, data_inic, data_fim, n_carros, estado, id1, estado_pagamento, tipo_pagamento);
        this.dias = dias;
        this.custos_reparacoes = custos_reparacoes;
        this.data_emissao = data_emissao;
        this.numero_fatura = numero_fatura;
        this.NIF_empresa = NIF_empresa;
        this.taxa = taxa;
    }

    /**
     Obtém os dias da fatura.
     @return Os dias da fatura.
     */
    public int getDias() {
        return dias;
    }

    /**
     Define os dias da fatura.
     @param dias Os dias da fatura.
     */
    public void setDias(int dias) {
        this.dias = dias;
    }

    /**
     Obtém os custos_reparacoes da fatura.
     @return Os custos_reparacoes da fatura.
     */
    public float getCustos_reparacoes() {
        return custos_reparacoes;
    }

    /**
     Define os custos_reparacoes da fatura.
     @param custos_reparacoes Os custos_reparacoes da fatura.
     */
    public void setCustos_reparacoes(float custos_reparacoes) {
        this.custos_reparacoes = custos_reparacoes;
    }

    /**
     Obtém a data_emissao da fatura.
     @return A data_emissao da fatura.
     */
    public LocalDate getData_emissao() {
        return data_emissao;
    }

    /**
     Define a data_emissao da fatura.
     @param data_emissao A data_emissao da fatura.
     */
    public void setData_emissao(LocalDate data_emissao) {
        this.data_emissao = data_emissao;
    }

    /**
     Obtém o numero_fatura da fatura.
     @return O numero_fatura da fatura.
     */
    public int getNumero_fatura() {
        return numero_fatura;
    }

    /**
     Define o numero_fatura da fatura.
     @param numero_fatura O numero_fatura da fatura.
     */
    public void setNumero_fatura(int numero_fatura) {
        this.numero_fatura = numero_fatura;
    }

    /**
     Obtém o NIF_empresa da fatura.
     @return O NIF_empresa da fatura.
     */
    public int getNIF_empresa() {
        return NIF_empresa;
    }

    /**
     Define o NIF_empresa da fatura.
     @param NIF_empresa O NIF_empresa da fatura.
     */
    public void setNIF_empresa(int NIF_empresa) {
        this.NIF_empresa = NIF_empresa;
    }

    /**
     Obtém a taxa da fatura.
     @return A taxa da fatura.
     */
    public int getTaxa() {
        return taxa;
    }

    /**
     Define a taxa da fatura.
     @param taxa A taxa da fatura.
     */
    public void setTaxa(int taxa) {
        this.taxa = taxa;
    }
}
