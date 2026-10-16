package com.example.rentacar.dash;

import java.time.LocalDate;
import java.util.ArrayList;

public class Inspecao {
    private String matricula;

    private LocalDate inspecao;

    public static ArrayList<Inspecao> InspecaoList = new ArrayList<>();

    /**

     Constructs an Inspecao object with the given matricula and inspecao date.
     @param matricula The matricula of the vehicle.
     @param inspecao The date of the inspection.
     */
    public Inspecao(String matricula, LocalDate inspecao) {
        this.matricula = matricula;
        this.inspecao = inspecao;
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

     Returns the date of the inspection.
     @return The inspecao date.
     */
    public LocalDate getInspecao() {
        return inspecao;
    }

    /**

     Sets the date of the inspection.
     @param inspecao The inspecao date to set.
     */
    public void setInspecao(LocalDate inspecao) {
        this.inspecao = inspecao;
    }
}
