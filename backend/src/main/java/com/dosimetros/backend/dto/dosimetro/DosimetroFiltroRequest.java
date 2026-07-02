package com.dosimetros.backend.dto.dosimetro;

public class DosimetroFiltroRequest {
    private Integer tipoDosimetroId;
    private String estado; // "disponible", "asignado", "baja", o null para todos

    public DosimetroFiltroRequest() {}

    public Integer getTipoDosimetroId() { return tipoDosimetroId; }
    public void setTipoDosimetroId(Integer tipoDosimetroId) { this.tipoDosimetroId = tipoDosimetroId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}