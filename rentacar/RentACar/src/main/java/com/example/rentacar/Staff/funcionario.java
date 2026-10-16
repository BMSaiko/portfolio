package com.example.rentacar.Staff;

import java.util.ArrayList;

/**
 * A classe "funcionario" representa um funcionário.
 */
public class funcionario {

    private int id;
    private String nome;
    private String email;
    private int telefone;
    private String password;
    private int nv_acesso;

    /**
     * Lista de funcionários.
     */
    public static ArrayList<funcionario> funcionariosList = new ArrayList<>();

    /**
     * Cria um objeto "funcionario" com os parâmetros fornecidos.
     *
     * @param id O ID do funcionário.
     * @param nome O nome do funcionário.
     * @param email O email do funcionário.
     * @param password A senha do funcionário.
     * @param telefone O telefone do funcionário.
     * @param nv_acesso O nível de acesso do funcionário.
     */
    public funcionario(int id, String nome, String email, String password, int telefone, int nv_acesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.telefone = telefone;
        this.nv_acesso = nv_acesso;
    }

    /**
     * Obtém o ID do funcionário.
     *
     * @return O ID do funcionário.
     */
    public int getId() {
        return id;
    }

    /**
     * Define o ID do funcionário.
     *
     * @param id O ID do funcionário.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o nome do funcionário.
     *
     * @return O nome do funcionário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do funcionário.
     *
     * @param nome O nome do funcionário.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o email do funcionário.
     *
     * @return O email do funcionário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtém a senha do funcionário.
     *
     * @return A senha do funcionário.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do funcionário.
     *
     * @param password A senha do funcionário.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Define o email do funcionário.
     *
     * @param email O email do funcionário.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtém o telefone do funcionário.
     *
     * @return O telefone do funcionário.
     */
    public int getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone do funcionário.
     *
     * @param telefone O telefone do funcionário.
     */
    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    /**
     * Obtém o nível de acesso do funcionário.
     *
     * @return O nível de acesso do funcionário.
     */
    public int getNv_acesso() {
        return nv_acesso;
    }

    /**
     * Define o nível de acesso do funcionário.
     *
     * @param nv_acesso O nível de acesso do funcionário.
     */
    public void setNv_acesso(int nv_acesso) {
        this.nv_acesso = nv_acesso;
    }
}
