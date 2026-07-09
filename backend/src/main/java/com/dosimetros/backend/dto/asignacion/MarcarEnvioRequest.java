package com.dosimetros.backend.dto.asignacion;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Marca un conjunto de asignaciones como enviadas (despachadas) o revierte el
 * envío (#18).
 */
public class MarcarEnvioRequest {

    @NotEmpty(message = "Debes seleccionar al menos una asignación")
    private List<Integer> asignacionIds;

    // true = marcar como enviado; false = revertir a pendiente.
    private boolean enviado = true;

    public MarcarEnvioRequest() {
    }

    public List<Integer> getAsignacionIds() {
        return asignacionIds;
    }

    public void setAsignacionIds(List<Integer> asignacionIds) {
        this.asignacionIds = asignacionIds;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
}
