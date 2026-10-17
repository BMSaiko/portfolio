package com.example.sheetsandpicks.Models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Stock extends Encomenda {

    private int id;
    private String Nome;
    private int quantidade;

    private String Fornecedor;

    private  float v_base;

    private String UOM;

    private float Preco_uni;

    private String moeda;

    private String pais;

    private String aprov_rejeitado;

    public static ArrayList<Stock> StockList = new ArrayList<>();

    public Stock(String n_Encomenda, LocalDate data, int id, String nome, int quantidade, String fornecedor, float v_base, String UOM, float preco_uni, String moeda, String pais,String aprov_rejeitado) {
        super(n_Encomenda, data);
        this.id = id;
        Nome = nome;
        this.quantidade = quantidade;
        Fornecedor = fornecedor;
        this.v_base = v_base;
        this.UOM = UOM;
        Preco_uni = preco_uni;
        this.moeda = moeda;
        this.pais = pais;
        this.aprov_rejeitado=aprov_rejeitado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public float getPreco_uni() {
        return Preco_uni;
    }

    public void setPreco_uni(float precol_uni) {
        Preco_uni = precol_uni;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public String getFornecedor() {
        return Fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        Fornecedor = fornecedor;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public float getV_base() {
        return v_base;
    }

    public void setV_base(float v_base) {
        this.v_base = v_base;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAprov_rejeitado() {
        return aprov_rejeitado;
    }

    public void setAprov_rejeitado(String aprov_rejeitado) {
        this.aprov_rejeitado = aprov_rejeitado;
    }
}
