package com.dosimetros.backend.dto.dashboard;

/**
 * Conteo asociado a una clave de texto (estado, trimestre).
 */
public class ConteoClaveResponse {

    private String clave;
    private Long cantidad;

    public ConteoClaveResponse() {
    }

    public ConteoClaveResponse(String clave, Long cantidad) {
        this.clave = clave;
        this.cantidad = cantidad;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
