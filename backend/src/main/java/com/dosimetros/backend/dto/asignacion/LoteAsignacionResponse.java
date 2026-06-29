package com.dosimetros.backend.dto.asignacion;

import java.time.LocalDate;
import java.util.List;

/**
 * Lote enviado: agrupación de asignaciones por trimestre y fecha de asignación
 * para la vista del ejecutivo (HU #15).
 */
public class LoteAsignacionResponse {

    private String trimestre;
    private LocalDate fechaAsignacion;
    private Integer cantidad;
    private List<AsignacionResponse> asignaciones;

    public LoteAsignacionResponse() {
    }

    public LoteAsignacionResponse(String trimestre,
                                  LocalDate fechaAsignacion,
                                  Integer cantidad,
                                  List<AsignacionResponse> asignaciones) {
        this.trimestre = trimestre;
        this.fechaAsignacion = fechaAsignacion;
        this.cantidad = cantidad;
        this.asignaciones = asignaciones;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<AsignacionResponse> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<AsignacionResponse> asignaciones) {
        this.asignaciones = asignaciones;
    }
}
