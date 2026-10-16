package com.example.rentacar.dash;

import java.time.LocalDate;
import java.util.ArrayList;

public class revisao {


    private String matricula;

    private LocalDate data_revisao;

    private int prox_revisao_kms;

    private String Descricao;

    public static ArrayList<revisao> revisaoList = new ArrayList<>();

    /**

     Constructs a Revisao object with the given parameters.
     @param matricula The matricula of the car.
     @param data_revisao The date of the revision.
     @param prox_revisao_kms The next revision kilometers.
     @param descricao The description of the revision.
     */
    public revisao(String matricula, LocalDate data_revisao, int prox_revisao_kms, String descricao) {
        this.matricula = matricula;
        this.data_revisao = data_revisao;
        this.prox_revisao_kms = prox_revisao_kms;
        Descricao = descricao;
    }

    /**

     Returns the matricula of the car.
     @return The matricula.
     */
    public String getMatricula() {
        return matricula;
    }

    /**

     Sets the matricula of the car.
     @param matricula The matricula to set.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**

     Returns the date of the revision.
     @return The data_revisao.
     */
    public LocalDate getData_revisao() {return data_revisao;}

    /**

     Sets the date of the revision.
     @param data_revisao The data_revisao to set.
     */
    public void setData_revisao(LocalDate data_revisao) {
        this.data_revisao = data_revisao;
    }

    /**

     Returns the next revision kilometers.
     @return The prox_revisao_kms.
     */
    public int getProx_revisao_kms() {
        return prox_revisao_kms;
    }

    /**

     Sets the next revision kilometers.
     @param prox_revisao_kms The prox_revisao_kms to set.
     */
    public void setProx_revisao_kms(int prox_revisao_kms) {
        this.prox_revisao_kms = prox_revisao_kms;
    }

    /**

     Returns the description of the revision.
     @return The Descricao.
     */
    public String getDescricao() {
        return Descricao;
    }

    /**

     Sets the description of the revision.
     @param descricao The Descricao to set.
     */
    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
