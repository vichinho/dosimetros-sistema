package com.dosimetros.backend.dto.asignacion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class AsignacionMasivaRequest {

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

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotEmpty(message = "Debe seleccionar al menos una tarea")
    private List<Integer> tareaIds;

    private LocalDate fechaAsignacion;

    @NotBlank(message = "El link de Trello es obligatorio")
    @Size(max = 500, message = "El link de Trello no puede superar 500 caracteres")
    private String linkTrello;

    public AsignacionMasivaRequest() {
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<Integer> getTareaIds() {
        return tareaIds;
    }

    public void setTareaIds(List<Integer> tareaIds) {
        this.tareaIds = tareaIds;
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
