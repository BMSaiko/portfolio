package com.example.rentacar.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;

public class InfoClienteModel {

    private int id;
    private int n_aluguers;
    private String modelo;
    private String marca;
    private LocalDate data_inic;
    private LocalDate data_fim;
    private int valor;

    public static ArrayList<InfoClienteModel> InfoclientesList = new ArrayList<>();
    public static ArrayList<InfoClienteModel> InfoclientesList2 = new ArrayList<>();


    /**

     Constructs a new InfoClienteModel with the specified parameters.
     @param n_aluguers The number of rentals for the client.
     @param modelo The model of the rented vehicle.
     @param marca The brand of the rented vehicle.
     @param id The ID of the client.
     */
    public InfoClienteModel(int n_aluguers, String modelo, String marca,int id) {
        this.id=id;
        this.n_aluguers = n_aluguers;
        this.modelo = modelo;
        this.marca = marca;
    }

    /**

     Constructs a new InfoClienteModel with the specified parameters.
     @param id The ID of the client.
     @param modelo The model of the rented vehicle.
     @param marca The brand of the rented vehicle.
     @param data_inic The start date of the rental.
     @param data_fim The end date of the rental.
     @param valor The rental value.
     */
    public InfoClienteModel(int id,String modelo, String marca, LocalDate data_inic, LocalDate data_fim, int valor) {
        this.id=id;
        this.modelo = modelo;
        this.marca = marca;
        this.data_inic = data_inic;
        this.data_fim = data_fim;
        this.valor = valor;
    }

    /**

     Returns the number of rentals for the client.
     @return The number of rentals.
     */
    public int getN_aluguers() {
        return n_aluguers;
    }

    /**

     Sets the number of rentals for the client.
     @param n_aluguers The number of rentals.
     */
    public void setN_aluguers(int n_aluguers) {
        this.n_aluguers = n_aluguers;
    }

    /**

     Returns the model of the rented vehicle.
     @return The model.
     */
    public String getModelo() {
        return modelo;
    }

    /**

     Sets the model of the rented vehicle.
     @param modelo The model.
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**

     Returns the brand of the rented vehicle.
     @return The brand.
     */
    public String getMarca() {
        return marca;
    }

    /**

     Sets the brand of the rented vehicle.
     @param Marca The brand.
     */
    public void setMarca(String Marca) {
        marca = Marca;
    }

    /**

     Returns the start date of the rental.
     @return The start date.
     */
    public LocalDate getData_inic() {
        return data_inic;
    }

    /**

     Sets the start date of the rental.
     @param data_inic The start date.
     */
    public void setData_inic(LocalDate data_inic) {
        this.data_inic = data_inic;
    }

    /**

     Returns the end date of the rental.
     @return The end date.
     */
    public LocalDate getData_fim() {
        return data_fim;
    }

    /**

     Sets the end date of the rental.
     @param data_fim The end date.
     */
    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    /**

     Returns the rental value.
     @return The rental value.
     */
    public int getValor() {
        return valor;
    }

    /**

     Sets the rental value.
     @param valor The rental value.
     */
    public void setValor(int valor) {
        this.valor = valor;
    }

    /**

     Returns the ID of the client.
     @return The client ID.
     */
    public int getId() {
        return id;
    }

    /**

     Sets the ID of the client.
     @param id The client ID.
     */
    public void setId(int id) {
        this.id = id;
    }
}
