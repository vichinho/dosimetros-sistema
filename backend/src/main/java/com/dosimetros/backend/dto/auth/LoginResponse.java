package com.dosimetros.backend.dto.auth;

public class LoginResponse {

    private String token;
    private String type;
    private String username;
    private String rol;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String rol) {
        this.token = token;
        this.type = "Bearer";
        this.username = username;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}