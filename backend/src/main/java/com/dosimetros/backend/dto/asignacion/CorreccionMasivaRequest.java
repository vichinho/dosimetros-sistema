package com.dosimetros.backend.dto.asignacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Corrección masiva de asignaciones (#14): cambia un mismo campo en varias
 * asignaciones a la vez (ej. corregir el link de Trello de 50 asignaciones).
 * campo ∈ {linkTrello, clienteId, ejecutivoId, empresaId, tipoPortaId}.
 */
public class CorreccionMasivaRequest {

    @NotEmpty(message = "Debes seleccionar al menos una asignación")
    private List<Integer> asignacionIds;

    @NotBlank(message = "Debes indicar el campo a corregir")
    private String campo;

    @NotBlank(message = "Debes indicar el nuevo valor")
    private String valor;

    public CorreccionMasivaRequest() {
    }

    public List<Integer> getAsignacionIds() {
        return asignacionIds;
    }

    public void setAsignacionIds(List<Integer> asignacionIds) {
        this.asignacionIds = asignacionIds;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
