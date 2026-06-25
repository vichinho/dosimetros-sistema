package com.dosimetros.backend.dto.tarea;

import java.time.LocalDate;

public class TareaResponse {

    private Integer id;
    private String numeroTarea;
    private LocalDate fechaCreacion;
    private String observacion;

    public TareaResponse() {
    }

    public TareaResponse(Integer id, String numeroTarea, LocalDate fechaCreacion, String observacion) {
        this.id = id;
        this.numeroTarea = numeroTarea;
        this.fechaCreacion = fechaCreacion;
        this.observacion = observacion;
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

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}