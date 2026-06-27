package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroRequest;
import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroResponse;
import com.dosimetros.backend.service.TipoDosimetroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-dosimetro")
public class TipoDosimetroController {

    private final TipoDosimetroService tipoDosimetroService;

    public TipoDosimetroController(TipoDosimetroService tipoDosimetroService) {
        this.tipoDosimetroService = tipoDosimetroService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<TipoDosimetroResponse>> listar() {
        return ResponseEntity.ok(tipoDosimetroService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TipoDosimetroResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoDosimetroService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoDosimetroResponse> crear(@Valid @RequestBody TipoDosimetroRequest request) {
        return ResponseEntity.ok(tipoDosimetroService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TipoDosimetroResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TipoDosimetroRequest request) {
        return ResponseEntity.ok(tipoDosimetroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tipoDosimetroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
