package com.dosimetros.backend.dto.dosimetro;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Arma (asigna tipo de porta) a una selección concreta de dosímetros
 * (modo preciso "sala de cine" del requerimiento #7).
 */
public class ArmarSeleccionRequest {

    @NotEmpty(message = "Debes seleccionar al menos un dosímetro")
    private List<Integer> dosimetroIds;

    @NotNull(message = "tipoPortaId es obligatorio")
    private Integer tipoPortaId;

    public ArmarSeleccionRequest() {
    }

    public List<Integer> getDosimetroIds() {
        return dosimetroIds;
    }

    public void setDosimetroIds(List<Integer> dosimetroIds) {
        this.dosimetroIds = dosimetroIds;
    }

    public Integer getTipoPortaId() {
        return tipoPortaId;
    }

    public void setTipoPortaId(Integer tipoPortaId) {
        this.tipoPortaId = tipoPortaId;
    }
}
