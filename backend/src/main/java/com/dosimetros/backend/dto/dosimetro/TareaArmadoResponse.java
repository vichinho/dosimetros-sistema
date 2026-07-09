package com.dosimetros.backend.dto.dosimetro;

/**
 * Resumen de armado de una tarea (#7): cuántos de sus dosímetros están armados
 * (con porta real) y cuántos pendientes (sin porta o porta "Sin armar …").
 */
public class TareaArmadoResponse {

    private Integer tareaId;
    private String numeroTarea;
    private long total;
    private long armados;
    private long pendientes;
    private String estado; // "armada" | "parcial" | "sin-armar"

    public TareaArmadoResponse() {
    }

    public TareaArmadoResponse(Integer tareaId, String numeroTarea, long total, long armados) {
        this.tareaId = tareaId;
        this.numeroTarea = numeroTarea;
        this.total = total;
        this.armados = armados;
        this.pendientes = total - armados;
        if (armados == 0) {
            this.estado = "sin-armar";
        } else if (this.pendientes == 0) {
            this.estado = "armada";
        } else {
            this.estado = "parcial";
        }
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getArmados() {
        return armados;
    }

    public void setArmados(long armados) {
        this.armados = armados;
    }

    public long getPendientes() {
        return pendientes;
    }

    public void setPendientes(long pendientes) {
        this.pendientes = pendientes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
