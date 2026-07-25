package com.dosimetros.backend.dto.dosimetro;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de una actualización de stock por archivo con upsert (#8).
 * Clave = número de dosímetro: si no existe se crea, si existe y cambió se
 * actualiza, y si es idéntico se cuenta como "sin cambios".
 */
public class ActualizacionStockResponse {

    private int totalFilas;
    private int creados;
    private int actualizados;
    private int sinCambios;
    private int fallidas;
    // Si el archivo tenía errores no se guarda nada (todo o nada): aplicado = false.
    private boolean aplicado = true;
    private List<String> errores = new ArrayList<>();

    public ActualizacionStockResponse() {
    }

    public boolean isAplicado() {
        return aplicado;
    }

    public void setAplicado(boolean aplicado) {
        this.aplicado = aplicado;
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
