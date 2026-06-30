package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.tipoporta.TipoPortaRequest;
import com.dosimetros.backend.dto.tipoporta.TipoPortaResponse;
import com.dosimetros.backend.service.TipoPortaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-porta")
public class TipoPortaController {

    private final TipoPortaService tipoPortaService;

    public TipoPortaController(TipoPortaService tipoPortaService) {
        this.tipoPortaService = tipoPortaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<TipoPortaResponse>> listar() {
        return ResponseEntity.ok(tipoPortaService.listar());
    }

    @GetMapping("/por-tipo-dosimetro/{tipoDosimetroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<TipoPortaResponse>> listarPorTipoDosimetro(@PathVariable Integer tipoDosimetroId) {
        return ResponseEntity.ok(tipoPortaService.listarPorTipoDosimetro(tipoDosimetroId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TipoPortaResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoPortaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TipoPortaResponse> crear(@Valid @RequestBody TipoPortaRequest request) {
        return ResponseEntity.ok(tipoPortaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TipoPortaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TipoPortaRequest request) {
        return ResponseEntity.ok(tipoPortaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tipoPortaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
