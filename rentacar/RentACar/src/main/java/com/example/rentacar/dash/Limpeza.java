package com.example.rentacar.dash;

import java.time.LocalDate;
import java.util.ArrayList;

public class Limpeza {

    private String matricula;

    private LocalDate data_inic;

    private String Limpeza;

    private String Descricao;

    public static ArrayList<Limpeza> LimpezaList = new ArrayList<>();

    /**

     Constructs a Limpeza object with the given matricula, data_inic, limpeza, and descricao.
     @param matricula The matricula of the vehicle.
     @param data_inic The start date of the cleaning task.
     @param limpeza The status of the cleaning task.
     @param descricao The description of the cleaning task.
     */
    public Limpeza(String matricula, LocalDate data_inic, String limpeza, String descricao) {
        this.matricula = matricula;
        this.data_inic = data_inic;
        Limpeza = limpeza;
        Descricao = descricao;
    }

    /**

     Returns the matricula of the vehicle.
     @return The matricula.
     */
    public String getMatricula() {
        return matricula;
    }

    /**

     Sets the matricula of the vehicle.
     @param matricula The matricula to set.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**

     Returns the start date of the cleaning task.
     @return The data_inic.
     */
    public LocalDate getData_inic() {
        return data_inic;
    }

    /**

     Sets the start date of the cleaning task.
     @param data_inic The data_inic to set.
     */
    public void setData_inic(LocalDate data_inic) {
        this.data_inic = data_inic;
    }

    /**

     Returns the status of the cleaning task.
     @return The limpeza.
     */
    public String getLimpeza() {
        return Limpeza;
    }

    /**

     Sets the status of the cleaning task.
     @param limpeza The limpeza to set.
     */
    public void setLimpeza(String limpeza) {
        Limpeza = limpeza;
    }

    /**

     Returns the description of the cleaning task.
     @return The descricao.
     */
    public String getDescricao() {
        return Descricao;
    }

    /**

     Sets the description of the cleaning task.
     @param descricao The descricao to set.
     */
    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
