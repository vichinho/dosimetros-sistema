package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.ejecutivo.EjecutivoRequest;
import com.dosimetros.backend.dto.ejecutivo.EjecutivoResponse;
import com.dosimetros.backend.service.EjecutivoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ejecutivos")
public class EjecutivoController {

    private final EjecutivoService ejecutivoService;

    public EjecutivoController(EjecutivoService ejecutivoService) {
        this.ejecutivoService = ejecutivoService;
    }

    @GetMapping
    public ResponseEntity<List<EjecutivoResponse>> listarActivos() {
        return ResponseEntity.ok(ejecutivoService.listarActivos());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<EjecutivoResponse>> listarPorEmpresa(@PathVariable Integer empresaId) {
        return ResponseEntity.ok(ejecutivoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjecutivoResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ejecutivoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EjecutivoResponse> crear(@Valid @RequestBody EjecutivoRequest request) {
        return ResponseEntity.ok(ejecutivoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EjecutivoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody EjecutivoRequest request) {
        return ResponseEntity.ok(ejecutivoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        ejecutivoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}