package com.dosimetros.backend.dto.usuario;

public class UsuarioResponse {

    private Integer id;
    private String username;
    private String rol;
    private Integer ejecutivoId;
    private String ejecutivoNombre;
    private Boolean activo;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Integer id, String username, String rol,
                           Integer ejecutivoId, String ejecutivoNombre, Boolean activo) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.ejecutivoId = ejecutivoId;
        this.ejecutivoNombre = ejecutivoNombre;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getEjecutivoId() {
        return ejecutivoId;
    }

    public void setEjecutivoId(Integer ejecutivoId) {
        this.ejecutivoId = ejecutivoId;
    }

    public String getEjecutivoNombre() {
        return ejecutivoNombre;
    }

    public void setEjecutivoNombre(String ejecutivoNombre) {
        this.ejecutivoNombre = ejecutivoNombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}