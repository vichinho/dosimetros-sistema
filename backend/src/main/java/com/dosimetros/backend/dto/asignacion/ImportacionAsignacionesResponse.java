package com.dosimetros.backend.dto.asignacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de la carga de asignaciones por archivo con upsert (#12).
 * Clave = número de dosímetro: si no tiene asignación se crea, si cambió algún
 * dato se actualiza, y si es idéntica se cuenta como "sin cambios".
 */
public class ImportacionAsignacionesResponse {

    private int totalFilas;
    private int creados;
    private int actualizados;
    private int sinCambios;
    private int fallidas;
    private List<String> errores = new ArrayList<>();

    public ImportacionAsignacionesResponse() {
    }

    public int getTotalFilas() {
        return totalFilas;
    }

    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    public int getCreados() {
        return creados;
    }

    public void setCreados(int creados) {
        this.creados = creados;
    }

    public int getActualizados() {
        return actualizados;
    }

    public void setActualizados(int actualizados) {
        this.actualizados = actualizados;
    }

    public int getSinCambios() {
        return sinCambios;
    }

    public void setSinCambios(int sinCambios) {
        this.sinCambios = sinCambios;
    }

    public int getFallidas() {
        return fallidas;
    }

    public void setFallidas(int fallidas) {
        this.fallidas = fallidas;
    }

    public List<String> getErrores() {
        return errores;
    }

    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
}
