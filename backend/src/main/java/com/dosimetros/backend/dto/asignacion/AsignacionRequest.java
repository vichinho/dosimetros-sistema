package com.dosimetros.backend.dto.asignacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AsignacionRequest {

    @NotNull(message = "El dosímetro es obligatorio")
    private Integer dosimetroId;

    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El ejecutivo es obligatorio")
    private Integer ejecutivoId;

    @NotNull(message = "La empresa es obligatoria")
    private Integer empresaId;

    @NotNull(message = "El tipo de porta es obligatorio")
    private Integer tipoPortaId;

    @NotBlank(message = "El trimestre es obligatorio")
    @Size(max = 20, message = "El trimestre no puede superar 20 caracteres")
    private String trimestre;

    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fechaAsignacion;

    @Size(max = 500, message = "El link de Trello no puede superar 500 caracteres")
    private String linkTrello;

    public AsignacionRequest() {
    }

    public Integer getDosimetroId() {
        return dosimetroId;
    }

    public void setDosimetroId(Integer dosimetroId) {
        this.dosimetroId = dosimetroId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getEjecutivoId() {
        return ejecutivoId;
    }

    public void setEjecutivoId(Integer ejecutivoId) {
        this.ejecutivoId = ejecutivoId;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }

    public Integer getTipoPortaId() {
        return tipoPortaId;
    }

    public void setTipoPortaId(Integer tipoPortaId) {
        this.tipoPortaId = tipoPortaId;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getLinkTrello() {
        return linkTrello;
    }

    public void setLinkTrello(String linkTrello) {
        this.linkTrello = linkTrello;
    }
}