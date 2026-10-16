package com.example.rentacar.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;

public class Clientes {
    private int id;
    private String email;
    private String nome;
    private int telefone;
    private int NIF;
    private LocalDate dataCliente;
    private LocalDate nascimento;
    private String morada;

    private int CConducao;

    private int CC;

    public static ArrayList<Clientes> clientesList = new ArrayList<>();

    /**

     Constructs a new Clientes object with the specified parameters.
     @param id The ID of the client.
     @param email The email address of the client.
     @param nome The name of the client.
     @param telefone The telephone number of the client.
     @param NIF The NIF (Tax Identification Number) of the client.
     @param dataCliente The registration date of the client.
     @param nascimento The birthdate of the client.
     @param morada The address of the client.
     @param CConducao The driving license number of the client.
     @param CC The ID card number of the client.
     */
    public Clientes(int id, String email, String nome, int telefone, int NIF, LocalDate dataCliente, LocalDate nascimento,
                    String morada, int CConducao, int CC) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.telefone = telefone;
        this.NIF = NIF;
        this.dataCliente = dataCliente;
        this.nascimento = nascimento;
        this.morada = morada;
        this.CConducao = CConducao;
        this.CC = CC;
    }
    /**

     Returns the ID of the client.
     @return The ID of the client.
     */
    public int getId() {
        return id;
    }
    /**

     Sets the ID of the client.
     @param id The ID to set for the client.
     */
    public void setId(int id) {
        this.id = id;
    }
    /**

     Returns the email address of the client.
     @return The email address of the client.
     */
    public String getEmail() {
        return email;
    }
    /**

     Sets the email address of the client.
     @param email The email address to set for the client.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**

     Returns the name of the client.
     @return The name of the client.
     */
    public String getNome() {
        return nome;
    }
    /**

     Sets the name of the client.
     @param nome The name to set for the client.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    /**

     Returns the telephone number of the client.
     @return The telephone number of the client.
     */
    public int getTelefone() {
        return telefone;
    }
    /**

     Sets the telephone number of the client.
     @param telefone The telephone number to set for the client.
     */
    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }
    /**

     Returns the NIF (Tax Identification Number) of the client.
     @return The NIF of the client.
     */
    public int getNIF() {
        return NIF;
    }
    /**

     Sets the NIF (Tax Identification Number) of the client.
     @param NIF The NIF to set for the client.
     */
    public void setNIF(int NIF) {
        this.NIF = NIF;
    }
    /**

     Returns the registration date of the client.
     @return The registration date of the client.
     */
    public LocalDate getDataCliente() {
        return dataCliente;
    }
    /**

     Sets the registration date of the client.
     @param dataCliente The registration date to set for the client.
     */
    public void setDataCliente(LocalDate dataCliente) {
        this.dataCliente = dataCliente;
    }
    /**

     Returns the birth date of the client.
     @return The birth date of the client.
     */
    public LocalDate getNascimento() {
        return nascimento;
    }
    /**

     Sets the birth date of the client.
     @param nascimento The birth date to set for the client.
     */
    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }
    /**

     Returns the address of the client.
     @return The address of the client.
     */
    public String getMorada() {
        return morada;
    }
    /**

     Sets the address of the client.
     @param morada The address to set for the client.
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }
    /**

     Returns the driving license number of the client.
     @return The driving license number of the client.
     */
    public int getCConducao() {
        return CConducao;
    }
    /**

     Sets the driving license number of the client.
     @param CConducao The driving license number to set for the client.
     */
    public void setCConducao(int CConducao) {
        this.CConducao = CConducao;
    }
    /**

     Returns the ID card number of the client.
     @return The ID card number of the client.
     */
    public int getCC() {
        return CC;
    }
    /**

     Sets the ID card number of the client.
     @param CC The ID card number to set for the client.
     */
    public void setCC(int CC) {
        this.CC = CC;
    }
}
