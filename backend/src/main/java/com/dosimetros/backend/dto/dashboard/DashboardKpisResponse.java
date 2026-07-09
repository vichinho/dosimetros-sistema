package com.dosimetros.backend.dto.dashboard;

import java.util.List;

/**
 * KPIs para el dashboard del administrador (HU #17).
 * Las métricas de asignaciones pueden venir filtradas por un trimestre.
 */
public class DashboardKpisResponse {

    // Trimestre aplicado al filtro (null = todos)
    private String trimestre;
    private List<String> trimestresDisponibles;

    // Totales de stock (estado actual, no dependen del trimestre)
    private long totalDosimetros;
    private long disponibles;
    private long asignados;
    private long danados;
    private long baja;

    // Total de asignaciones (según el filtro de trimestre)
    private long totalAsignaciones;

    private List<ConteoClaveResponse> dosimetrosPorEstado;
    private List<ConteoResponse> dosimetrosPorTipo;
    private List<ConteoResponse> disponiblesPorPorta;

    private List<ConteoResponse> asignacionesPorEmpresa;
    private List<ConteoResponse> asignacionesPorEjecutivo;
    private List<ConteoResponse> asignacionesPorTipoPorta;
    private List<ConteoResponse> topClientes;
    private List<ConteoClaveResponse> asignacionesPorTrimestre;
    // Desglose por mes del trimestre filtrado (null si no hay filtro).
    private List<ConteoClaveResponse> asignacionesPorMes;

    public DashboardKpisResponse() {
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    public List<String> getTrimestresDisponibles() {
        return trimestresDisponibles;
    }

    public void setTrimestresDisponibles(List<String> trimestresDisponibles) {
        this.trimestresDisponibles = trimestresDisponibles;
    }

    public long getTotalDosimetros() {
        return totalDosimetros;
    }

    public void setTotalDosimetros(long totalDosimetros) {
        this.totalDosimetros = totalDosimetros;
    }

    public long getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(long disponibles) {
        this.disponibles = disponibles;
    }

    public long getAsignados() {
        return asignados;
    }

    public void setAsignados(long asignados) {
        this.asignados = asignados;
    }

    public long getDanados() {
        return danados;
    }

    public void setDanados(long danados) {
        this.danados = danados;
    }

    public long getBaja() {
        return baja;
    }

    public void setBaja(long baja) {
        this.baja = baja;
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

    public List<ConteoResponse> getDisponiblesPorPorta() {
        return disponiblesPorPorta;
    }

    public void setDisponiblesPorPorta(List<ConteoResponse> disponiblesPorPorta) {
        this.disponiblesPorPorta = disponiblesPorPorta;
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

    public List<ConteoResponse> getAsignacionesPorTipoPorta() {
        return asignacionesPorTipoPorta;
    }

    public void setAsignacionesPorTipoPorta(List<ConteoResponse> asignacionesPorTipoPorta) {
        this.asignacionesPorTipoPorta = asignacionesPorTipoPorta;
    }

    public List<ConteoResponse> getTopClientes() {
        return topClientes;
    }

    public void setTopClientes(List<ConteoResponse> topClientes) {
        this.topClientes = topClientes;
    }

    public List<ConteoClaveResponse> getAsignacionesPorTrimestre() {
        return asignacionesPorTrimestre;
    }

    public void setAsignacionesPorTrimestre(List<ConteoClaveResponse> asignacionesPorTrimestre) {
        this.asignacionesPorTrimestre = asignacionesPorTrimestre;
    }

    public List<ConteoClaveResponse> getAsignacionesPorMes() {
        return asignacionesPorMes;
    }

    public void setAsignacionesPorMes(List<ConteoClaveResponse> asignacionesPorMes) {
        this.asignacionesPorMes = asignacionesPorMes;
    }
}
