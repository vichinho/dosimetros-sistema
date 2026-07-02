package com.dosimetros.backend.dto.tipoporta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TipoPortaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de dosímetro es obligatorio")
    private Integer tipoDosimetroId;

    public TipoPortaRequest() {
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
}