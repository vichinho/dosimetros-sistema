package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.tipoporta.TipoPortaRequest;
import com.dosimetros.backend.dto.tipoporta.TipoPortaResponse;
import com.dosimetros.backend.service.TipoPortaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-porta")
public class TipoPortaController {

    private final TipoPortaService service;

    public TipoPortaController(TipoPortaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoPortaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/tipo-dosimetro/{tipoDosimetroId}")
    public ResponseEntity<List<TipoPortaResponse>> listarPorTipoDosimetro(@PathVariable Integer tipoDosimetroId) {
        return ResponseEntity.ok(service.listarPorTipoDosimetro(tipoDosimetroId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPortaResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoPortaResponse> crear(@Valid @RequestBody TipoPortaRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoPortaResponse> actualizar(@PathVariable Integer id,
                                                         @Valid @RequestBody TipoPortaRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}