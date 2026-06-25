package com.dosimetros.backend.dto.tipoporta;

public class TipoPortaResponse {

    private Integer id;
    private String nombre;
    private Integer tipoDosimetroId;
    private String tipoDosimetroNombre;

    public TipoPortaResponse() {
    }

    public TipoPortaResponse(Integer id, String nombre, Integer tipoDosimetroId, String tipoDosimetroNombre) {
        this.id = id;
        this.nombre = nombre;
        this.tipoDosimetroId = tipoDosimetroId;
        this.tipoDosimetroNombre = tipoDosimetroNombre;
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

    public Integer getTipoDosimetroId() {
        return tipoDosimetroId;
    }

    public void setTipoDosimetroId(Integer tipoDosimetroId) {
        this.tipoDosimetroId = tipoDosimetroId;
    }

    public String getTipoDosimetroNombre() {
        return tipoDosimetroNombre;
    }

    public void setTipoDosimetroNombre(String tipoDosimetroNombre) {
        this.tipoDosimetroNombre = tipoDosimetroNombre;
    }
}