package com.dosimetros.backend.dto.usuario;

import jakarta.validation.constraints.NotNull;

/**
 * Actualización de un usuario. La contraseña es opcional: si viene vacía,
 * se mantiene la actual.
 */
public class UsuarioUpdateRequest {

    @NotNull(message = "El rol es obligatorio")
    private Integer rolId;

    private Integer ejecutivoId;

    private String password;

    public UsuarioUpdateRequest() {
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Integer getEjecutivoId() {
        return ejecutivoId;
    }

    public void setEjecutivoId(Integer ejecutivoId) {
        this.ejecutivoId = ejecutivoId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
