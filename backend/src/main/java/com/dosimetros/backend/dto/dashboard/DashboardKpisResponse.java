package com.dosimetros.backend.dto.dashboard;

import java.util.List;

/**
 * KPIs para el dashboard del administrador (HU #17).
 */
public class DashboardKpisResponse {

    private long totalDosimetros;
    private long totalAsignaciones;
    private List<ConteoClaveResponse> dosimetrosPorEstado;
    private List<ConteoResponse> dosimetrosPorTipo;
    private List<ConteoResponse> asignacionesPorEmpresa;
    private List<ConteoResponse> asignacionesPorEjecutivo;
    private List<ConteoClaveResponse> asignacionesPorTrimestre;

    public DashboardKpisResponse() {
    }

    public long getTotalDosimetros() {
        return totalDosimetros;
    }

    public void setTotalDosimetros(long totalDosimetros) {
        this.totalDosimetros = totalDosimetros;
    }

    public long getTotalAsignaciones() {
        return totalAsignaciones;
    }

    public void setTotalAsignaciones(long totalAsignaciones) {
        this.totalAsignaciones = totalAsignaciones;
    }

    public List<ConteoClaveResponse> getDosimetrosPorEstado() {
        return dosimetrosPorEstado;
    }

    public void setDosimetrosPorEstado(List<ConteoClaveResponse> dosimetrosPorEstado) {
        this.dosimetrosPorEstado = dosimetrosPorEstado;
    }

    public List<ConteoResponse> getDosimetrosPorTipo() {
        return dosimetrosPorTipo;
    }

    public void setDosimetrosPorTipo(List<ConteoResponse> dosimetrosPorTipo) {
        this.dosimetrosPorTipo = dosimetrosPorTipo;
    }

    public List<ConteoResponse> getAsignacionesPorEmpresa() {
        return asignacionesPorEmpresa;
    }

    public void setAsignacionesPorEmpresa(List<ConteoResponse> asignacionesPorEmpresa) {
        this.asignacionesPorEmpresa = asignacionesPorEmpresa;
    }

    public List<ConteoResponse> getAsignacionesPorEjecutivo() {
        return asignacionesPorEjecutivo;
    }

    public void setAsignacionesPorEjecutivo(List<ConteoResponse> asignacionesPorEjecutivo) {
        this.asignacionesPorEjecutivo = asignacionesPorEjecutivo;
    }

    public List<ConteoClaveResponse> getAsignacionesPorTrimestre() {
        return asignacionesPorTrimestre;
    }

    public void setAsignacionesPorTrimestre(List<ConteoClaveResponse> asignacionesPorTrimestre) {
        this.asignacionesPorTrimestre = asignacionesPorTrimestre;
    }
}
