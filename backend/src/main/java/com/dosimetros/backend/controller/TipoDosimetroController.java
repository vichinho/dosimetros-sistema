package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroRequest;
import com.dosimetros.backend.dto.tipodosimetro.TipoDosimetroResponse;
import com.dosimetros.backend.service.TipoDosimetroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-dosimetro")
public class TipoDosimetroController {

    private final TipoDosimetroService service;

    public TipoDosimetroController(TipoDosimetroService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoDosimetroResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoDosimetroResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoDosimetroResponse> crear(@Valid @RequestBody TipoDosimetroRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoDosimetroResponse> actualizar(@PathVariable Integer id,
                                                            @Valid @RequestBody TipoDosimetroRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}