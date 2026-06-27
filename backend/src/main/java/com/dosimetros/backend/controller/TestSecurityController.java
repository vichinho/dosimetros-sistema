package com.dosimetros.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSecurityController {

    @GetMapping("/api/test/public")
    public String publico() {
        return "publico";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/test/admin")
    public String admin() {
        return "admin";
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    @GetMapping("/api/test/operador")
    public String operador() {
        return "operador";
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR','EJECUTIVO')")
    @GetMapping("/api/test/auth")
    public String autenticado() {
        return "autenticado";
    }
}