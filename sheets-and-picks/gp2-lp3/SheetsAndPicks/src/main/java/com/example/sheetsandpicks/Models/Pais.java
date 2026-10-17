package com.example.sheetsandpicks.Models;

public class Pais {
    private String Pais;

    private float taxa;

    private String Tipo_moeda;

    public Pais(String pais, float taxa, String tipo_moeda) {
        Pais = pais;
        this.taxa = taxa;
        Tipo_moeda = tipo_moeda;
    }

    public String getPais() {
        return Pais;
    }

    public void setPais(String pais) {
        Pais = pais;
    }

    public float getTaxa() {
        return taxa;
    }

    public void setTaxa(float taxa) {
        this.taxa = taxa;
    }

    public String getTipo_moeda() {
        return Tipo_moeda;
    }

    public void setTipo_moeda(String tipo_moeda) {
        Tipo_moeda = tipo_moeda;
    }
}
