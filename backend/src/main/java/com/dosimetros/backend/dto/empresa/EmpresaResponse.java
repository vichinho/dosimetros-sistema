package com.dosimetros.backend.dto.empresa;

public class EmpresaResponse {

    private Integer id;
    private String nombre;
    private Boolean activa;

    public EmpresaResponse() {
    }

    public EmpresaResponse(Integer id, String nombre, Boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.activa = activa;
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

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}
