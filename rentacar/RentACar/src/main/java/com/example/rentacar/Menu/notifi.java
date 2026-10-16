package com.example.rentacar.Menu;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class notifi {
    private int id;
    private String matricula;
    private String tipo;
    private String descricao;
    private LocalDateTime hora;

    /**
     * Cria uma nova instância de Notificacao.
     *
     * @param id        O ID da notificação.
     * @param matricula A matrícula associada à notificação.
     * @param tipo      O tipo da notificação.
     * @param descricao A descrição da notificação.
     * @param hora      A hora da notificação.
     */
    public notifi(int id, String matricula, String tipo, String descricao, LocalDateTime hora) {
        this.id = id;
        this.matricula = matricula;
        this.tipo = tipo;
        this.descricao = descricao;
        this.hora = hora;
    }

    /**
     * Obtém o ID da notificação.
     *
     * @return O ID da notificação.
     */
    public int getId() {
        return id;
    }

    /**
     * Define o ID da notificação.
     *
     * @param id O ID da notificação.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a matrícula associada à notificação.
     *
     * @return A matrícula.
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula associada à notificação.
     *
     * @param matricula A matrícula.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém o tipo da notificação.
     *
     * @return O tipo da notificação.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define o tipo da notificação.
     *
     * @param tipo O tipo da notificação.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtém a descrição da notificação.
     *
     * @return A descrição da notificação.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da notificação.
     *
     * @param descricao A descrição da notificação.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém a hora da notificação.
     *
     * @return A hora da notificação.
     */
    public LocalDateTime getHora() {
        return hora;
    }

    /**
     * Define a hora da notificação.
     *
     * @param hora A hora da notificação.
     */
    public void setHora(LocalDateTime hora) {
        this.hora = hora;
    }
}
