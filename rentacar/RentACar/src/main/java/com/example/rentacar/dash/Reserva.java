package com.example.rentacar.dash;

import com.example.rentacar.Aluguer.Orcamento;

import java.time.LocalDate;

public class Reserva extends Orcamento {

    private LocalDate data_inicio;

    private LocalDate data_fim;

    private String name_funcionario;

    private String name_cliente;

    private String matricula;

    /**

     Constructs a Reserva object with the given parameters.
     @param id The ID of the reservation.
     @param valor The value of the reservation.
     @param estado_orcamento The state of the reservation.
     @param nome_cliente The name of the client.
     @param nome_funcionario The name of the employee.
     @param matricula The matricula of the car.
     @param data_inic The start date of the reservation.
     @param data_fim The end date of the reservation.
     @param n_carros The number of cars reserved.
     @param data_inicio The start date of the rental period.
     @param data_fim1 The end date of the rental period.
     @param name_funcionario The name of the employee handling the reservation.
     @param name_cliente The name of the client making the reservation.
     @param matricula1 The matricula of the reserved car.
     */
    public Reserva(int id, int valor, String estado_orcamento, String nome_cliente, String nome_funcionario, String matricula, LocalDate data_inic, LocalDate data_fim, int n_carros, LocalDate data_inicio, LocalDate data_fim1, String name_funcionario, String name_cliente, String matricula1) {
        super(id, valor, estado_orcamento, nome_cliente, nome_funcionario, matricula, data_inic, data_fim, n_carros);
        this.data_inicio = data_inicio;
        this.data_fim = data_fim1;
        this.name_funcionario = name_funcionario;
        this.name_cliente = name_cliente;
        this.matricula = matricula1;
    }

    /**

     Returns the start date of the rental period.
     @return The data_inicio.
     */
    public LocalDate getData_inicio() {
        return data_inicio;
    }

    /**

     Sets the start date of the rental period.
     @param data_inicio The data_inicio to set.
     */
    public void setData_inicio(LocalDate data_inicio) {
        this.data_inicio = data_inicio;
    }

    /**

     Returns the end date of the rental period.
     @return The data_fim.
     */
    public LocalDate getData_fim() {
        return data_fim;
    }

    /**

     Sets the end date of the rental period.
     @param data_fim The data_fim to set.
     */
    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    /**

     Returns the name of the employee handling the reservation.
     @return The name_funcionario.
     */
    public String getName_funcionario() {
        return name_funcionario;
    }

    /**

     Sets the name of the employee handling the reservation.
     @param name_funcionario The name_funcionario to set.
     */
    public void setName_funcionario(String name_funcionario) {
        this.name_funcionario = name_funcionario;
    }

    /**

     Returns the name of the client making the reservation.
     @return The name_cliente.
     */
    public String getName_cliente() {
        return name_cliente;
    }

    /**

     Sets the name of the client making the reservation.
     @param name_cliente The name_cliente to set.
     */
    public void setName_cliente(String name_cliente) {
        this.name_cliente = name_cliente;
    }

    /**

     Returns the matricula of the reserved car.
     @return The matricula.
     */
    public String getMatricula() {
        return matricula;
    }

    /**

     Sets the matricula of the reserved car.
     @param matricula The matricula to set.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
