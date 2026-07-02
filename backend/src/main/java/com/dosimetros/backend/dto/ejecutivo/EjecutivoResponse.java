package com.dosimetros.backend.dto.ejecutivo;

public class EjecutivoResponse {

    private Integer id;
    private String nombre;
    private String email;
    private Boolean activo;

    public EjecutivoResponse() {
    }

    public EjecutivoResponse(Integer id, String nombre, String email, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}