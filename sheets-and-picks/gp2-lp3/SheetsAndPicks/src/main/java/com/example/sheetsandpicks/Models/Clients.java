package com.example.sheetsandpicks.Models;

import java.util.ArrayList;

public class Clients {

    private int id;
    private String ativo;
    private String nome;
    private String endereco;
    private String endereco2;
    private String cidade;
    private Integer telefone;
    private String email;
    private String cod_postal;
    private String data_nascimento;

    private static ArrayList<Clients> clientsList = new ArrayList<>();

    public Clients(int id, String ativo, String nome, String endereco, String endereco2, String cidade, int telefone, String email, String cod_postal, String data_nascimento) {
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
    }

    // Métodos para acessar os atributos da classe

    public int getId() {
        return id;
    }

    public String getAtivo() {
        return ativo;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEndereco2() {
        return endereco2;
    }

    public String getCidade() {
        return cidade;
    }

    public Integer getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCod_postal() {
        return cod_postal;
    }

    public String getData_nascimento() {
            return data_nascimento;
    }

    // Métodos para manipulação da lista de clientes

    public static ArrayList<Clients> getClientsList() {
        return clientsList;
    }

    public static void addClient(Clients client) {
        clientsList.add(client);
    }

    public static void removeClient(Clients client) {
        clientsList.remove(client);
    }
}
