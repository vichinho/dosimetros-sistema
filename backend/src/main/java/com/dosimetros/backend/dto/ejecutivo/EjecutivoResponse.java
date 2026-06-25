package com.dosimetros.backend.dto.ejecutivo;

public class EjecutivoResponse {

    private Integer id;
    private String nombre;
    private String email;
    private Integer empresaId;
    private String empresaNombre;
    private Boolean activo;

    public EjecutivoResponse() {
    }

    public EjecutivoResponse(Integer id, String nombre, String email,
                              Integer empresaId, String empresaNombre, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
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

    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}