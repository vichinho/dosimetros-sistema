package com.dosimetros.backend.dto.dosimetro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Edita las especificaciones de un dosímetro (#14) sin tocar tarea, bandeja ni
 * slot (esos se gestionan en Armar/Asignar).
 */
public class EditarEspecificacionesRequest {

    @NotNull(message = "El número es obligatorio")
    private Integer numero;

    @NotNull(message = "El tipo de dosímetro es obligatorio")
    private Integer tipoDosimetroId;

    // Opcional: null = dejar sin porta.
    private Integer tipoPortaId;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;

    public EditarEspecificacionesRequest() {
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getTipoDosimetroId() {
        return tipoDosimetroId;
    }

    public void setTipoDosimetroId(Integer tipoDosimetroId) {
        this.tipoDosimetroId = tipoDosimetroId;
    }

    public Integer getTipoPortaId() {
        return tipoPortaId;
    }

    public void setTipoPortaId(Integer tipoPortaId) {
        this.tipoPortaId = tipoPortaId;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
