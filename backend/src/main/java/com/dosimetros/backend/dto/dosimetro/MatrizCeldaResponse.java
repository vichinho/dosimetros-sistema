package com.dosimetros.backend.dto.dosimetro;

/**
 * Celda de la vista dinámica de stock: cantidad de dosímetros de una tarea con
 * un tipo de porta (requerimiento #6). El frontend arma la matriz (tarea × porta).
 */
public class MatrizCeldaResponse {

    private Integer tareaId;
    private String numeroTarea;
    private Integer tipoPortaId;
    private String tipoPortaNombre;
    private Long cantidad;

    public MatrizCeldaResponse() {
    }

    public MatrizCeldaResponse(Integer tareaId, String numeroTarea,
                               Integer tipoPortaId, String tipoPortaNombre, Long cantidad) {
        this.tareaId = tareaId;
        this.numeroTarea = numeroTarea;
        this.tipoPortaId = tipoPortaId;
        this.tipoPortaNombre = tipoPortaNombre;
        this.cantidad = cantidad;
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

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
