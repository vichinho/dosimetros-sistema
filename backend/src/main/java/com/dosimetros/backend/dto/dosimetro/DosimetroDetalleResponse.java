package com.dosimetros.backend.dto.dosimetro;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;

import java.util.List;

public class DosimetroDetalleResponse {

    private Integer id;
    private Integer numero;
    private Integer tipoDosimetroId;
    private String tipoDosimetroNombre;
    private Integer tipoPortaId;
    private String tipoPortaNombre;
    private Integer tareaId;
    private String numeroTarea;
    private Integer numeroBandeja;
    private Integer slotBandeja;
    private String estado;
    private String observacion;
    private List<AsignacionResponse> asignaciones;

    public DosimetroDetalleResponse() {
    }

    public DosimetroDetalleResponse(Integer id, Integer numero, Integer tipoDosimetroId, String tipoDosimetroNombre,
                                    Integer tipoPortaId, String tipoPortaNombre, Integer tareaId, String numeroTarea,
                                    Integer numeroBandeja, Integer slotBandeja, String estado, String observacion,
                                    List<AsignacionResponse> asignaciones) {
        this.id = id;
        this.numero = numero;
        this.tipoDosimetroId = tipoDosimetroId;
        this.tipoDosimetroNombre = tipoDosimetroNombre;
        this.tipoPortaId = tipoPortaId;
        this.tipoPortaNombre = tipoPortaNombre;
        this.tareaId = tareaId;
        this.numeroTarea = numeroTarea;
        this.numeroBandeja = numeroBandeja;
        this.slotBandeja = slotBandeja;
        this.estado = estado;
        this.observacion = observacion;
        this.asignaciones = asignaciones;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTipoDosimetroNombre() {
        return tipoDosimetroNombre;
    }

    public void setTipoDosimetroNombre(String tipoDosimetroNombre) {
        this.tipoDosimetroNombre = tipoDosimetroNombre;
    }

    public Integer getTipoPortaId() {
        return tipoPortaId;
    }

    public void setTipoPortaId(Integer tipoPortaId) {
        this.tipoPortaId = tipoPortaId;
    }

    public String getTipoPortaNombre() {
        return tipoPortaNombre;
    }

    public void setTipoPortaNombre(String tipoPortaNombre) {
        this.tipoPortaNombre = tipoPortaNombre;
    }

    public Integer getTareaId() {
        return tareaId;
    }

    public void setTareaId(Integer tareaId) {
        this.tareaId = tareaId;
    }

    public String getNumeroTarea() {
        return numeroTarea;
    }

    public void setNumeroTarea(String numeroTarea) {
        this.numeroTarea = numeroTarea;
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

    public List<AsignacionResponse> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<AsignacionResponse> asignaciones) {
        this.asignaciones = asignaciones;
    }
}