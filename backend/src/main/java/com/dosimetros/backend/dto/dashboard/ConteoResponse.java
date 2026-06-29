package com.dosimetros.backend.dto.dashboard;

/**
 * Conteo asociado a una entidad con id y nombre (empresa, ejecutivo, tipo).
 */
public class ConteoResponse {

    private Integer id;
    private String nombre;
    private Long cantidad;

    public ConteoResponse() {
    }

    public ConteoResponse(Integer id, String nombre, Long cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
