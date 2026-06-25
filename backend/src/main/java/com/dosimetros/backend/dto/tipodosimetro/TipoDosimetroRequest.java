package com.dosimetros.backend.dto.tipodosimetro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TipoDosimetroRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String nombre;

    public TipoDosimetroRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}