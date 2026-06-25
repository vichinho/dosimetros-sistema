package com.dosimetros.backend.dto.asignacion;

import java.time.LocalDate;

public class AsignacionResponse {

    private Integer id;
    private Integer dosimetroId;
    private Integer dosimetroNumero;
    private Integer clienteId;
    private String clienteNombre;
    private Integer ejecutivoId;
    private String ejecutivoNombre;
    private Integer empresaId;
    private String empresaNombre;
    private Integer tipoPortaId;
    private String tipoPortaNombre;
    private String trimestre;
    private LocalDate fechaAsignacion;
    private String linkTrello;

    public AsignacionResponse() {
    }

    public AsignacionResponse(Integer id, Integer dosimetroId, Integer dosimetroNumero,
                              Integer clienteId, String clienteNombre,
                              Integer ejecutivoId, String ejecutivoNombre,
                              Integer empresaId, String empresaNombre,
                              Integer tipoPortaId, String tipoPortaNombre,
                              String trimestre, LocalDate fechaAsignacion, String linkTrello) {
        this.id = id;
        this.dosimetroId = dosimetroId;
        this.dosimetroNumero = dosimetroNumero;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.ejecutivoId = ejecutivoId;
        this.ejecutivoNombre = ejecutivoNombre;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.tipoPortaId = tipoPortaId;
        this.tipoPortaNombre = tipoPortaNombre;
        this.trimestre = trimestre;
        this.fechaAsignacion = fechaAsignacion;
        this.linkTrello = linkTrello;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDosimetroId() { return dosimetroId; }
    public void setDosimetroId(Integer dosimetroId) { this.dosimetroId = dosimetroId; }
    public Integer getDosimetroNumero() { return dosimetroNumero; }
    public void setDosimetroNumero(Integer dosimetroNumero) { this.dosimetroNumero = dosimetroNumero; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Integer getEjecutivoId() { return ejecutivoId; }
    public void setEjecutivoId(Integer ejecutivoId) { this.ejecutivoId = ejecutivoId; }
    public String getEjecutivoNombre() { return ejecutivoNombre; }
    public void setEjecutivoNombre(String ejecutivoNombre) { this.ejecutivoNombre = ejecutivoNombre; }
    public Integer getEmpresaId() { return empresaId; }
    public void setEmpresaId(Integer empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Integer getTipoPortaId() { return tipoPortaId; }
    public void setTipoPortaId(Integer tipoPortaId) { this.tipoPortaId = tipoPortaId; }
    public String getTipoPortaNombre() { return tipoPortaNombre; }
    public void setTipoPortaNombre(String tipoPortaNombre) { this.tipoPortaNombre = tipoPortaNombre; }
    public String getTrimestre() { return trimestre; }
    public void setTrimestre(String trimestre) { this.trimestre = trimestre; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public String getLinkTrello() { return linkTrello; }
    public void setLinkTrello(String linkTrello) { this.linkTrello = linkTrello; }
}