package com.dosimetros.backend.dto.tarea;

/** Tarea con la cantidad de dosímetros disponibles (para asignación). */
public class TareaDisponibleResponse {

    private Integer id;
    private String numeroTarea;
    private Long disponibles;

    public TareaDisponibleResponse() {
    }

    public TareaDisponibleResponse(Integer id, String numeroTarea, Long disponibles) {
        this.id = id;
        this.numeroTarea = numeroTarea;
        this.disponibles = disponibles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroTarea() {
        return numeroTarea;
    }

    public void setNumeroTarea(String numeroTarea) {
        this.numeroTarea = numeroTarea;
    }

    public Long getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(Long disponibles) {
        this.disponibles = disponibles;
    }
}
