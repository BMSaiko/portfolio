package com.example.sheetsandpicks.Models;

import java.time.LocalDate;

public class Encomenda {
    private String N_Encomenda;
    private LocalDate data;

    public Encomenda(String n_Encomenda, LocalDate data) {
        N_Encomenda = n_Encomenda;
        this.data = data;
    }

    public String getN_Encomenda() {
        return N_Encomenda;
    }

    public void setN_Encomenda(String n_Encomenda) {
        N_Encomenda = n_Encomenda;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
