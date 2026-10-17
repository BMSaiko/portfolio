package com.example.sheetsandpicks.Models;

import java.util.ArrayList;
import java.util.Date;

public class Admins {

    private int id;
    private String nome;
    private String email;
    private String cod_postal;
    private int id_permissao;
    private String endereco;
    private Integer telefone;
    private String endereco2;
    private String cidade;
    private String ativo;
    private Date data_nascimento;

    private static ArrayList<Admins> adminList = new ArrayList<>();

    public Admins(int id, String ativo,String nome, String email, String cod_postal, String endereco2,String cidade ,int id_permissao, String endereco, Date data_nascimento ,int telefone) {
        this.id = id;
        this.ativo = ativo;
        this.nome = nome;
        this.endereco = endereco;
        this.endereco2 = endereco2;
        this.cidade = cidade;
        this.telefone = telefone;
        this.email = email;
        this.cod_postal = cod_postal;
        this.data_nascimento = data_nascimento;

        adminList.add(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCod_postal() {
        return cod_postal;
    }

    public void setCod_postal(String cod_postal) {
        this.cod_postal = cod_postal;
    }

    public int getId_permissao() {
        return id_permissao;
    }

    public void setId_permissao(int id_permissao) {
        this.id_permissao = id_permissao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }
    @Override
    public String toString() {
        return "Admins{" +
                "nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
    // Getters e Setters

    // Exemplo de um getter para obter a lista de fornecedores
    public static ArrayList<Admins> getAdminsList() {
        return adminList;
    }

    public String getEndereco2() {
        return endereco2;
    }

    public String getCidade() {
        return cidade;
    }

    public Date getData_nascimento() {
        return data_nascimento;
    }
}
