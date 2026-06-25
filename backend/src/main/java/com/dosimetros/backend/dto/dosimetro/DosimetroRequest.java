package com.dosimetros.backend.dto.dosimetro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class DosimetroRequest {

    @NotNull(message = "El número es obligatorio")
    @PositiveOrZero(message = "El número debe ser mayor o igual a 0")
    private Integer numero;

    @NotNull(message = "El tipo de dosímetro es obligatorio")
    private Integer tipoDosimetroId;

    private Integer tipoPortaId;
    private Integer tareaId;
    private Integer numeroBandeja;
    private Integer slotBandeja;

    @Size(max = 50, message = "El estado no puede superar 50 caracteres")
    private String estado;

    @Size(max = 500, message = "La observación no puede superar 500 caracteres")
    private String observacion;

    public DosimetroRequest() {
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

    public Integer getTareaId() {
        return tareaId;
    }

    public void setTareaId(Integer tareaId) {
        this.tareaId = tareaId;
    }

    public Integer getNumeroBandeja() {
        return numeroBandeja;
    }

    public void setNumeroBandeja(Integer numeroBandeja) {
        this.numeroBandeja = numeroBandeja;
    }

    public Integer getSlotBandeja() {
        return slotBandeja;
    }

    public void setSlotBandeja(Integer slotBandeja) {
        this.slotBandeja = slotBandeja;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}