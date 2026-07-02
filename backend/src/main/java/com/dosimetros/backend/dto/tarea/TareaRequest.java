package com.dosimetros.backend.dto.tarea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TareaRequest {

    @NotBlank(message = "El número de tarea es obligatorio")
    @Size(max = 100, message = "El número de tarea no puede superar 100 caracteres")
    private String numeroTarea;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate fechaCreacion;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;

    public TareaRequest() {
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