package com.dosimetros.backend.dto.asignacion;

/** Opción simple (id + nombre) para poblar selectores de filtro. */
public class OpcionResponse {

    private Integer id;
    private String nombre;

    public OpcionResponse() {
    }

    public OpcionResponse(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
