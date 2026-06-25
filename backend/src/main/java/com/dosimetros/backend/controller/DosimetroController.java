package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.dosimetro.DosimetroRequest;
import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.service.DosimetroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dosimetros")
public class DosimetroController {

    private final DosimetroService service;

    public DosimetroController(DosimetroService service) {
        this.service = service;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<DosimetroResponse>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<DosimetroResponse>> buscarPorNumero(@RequestParam Integer numero) {
        return ResponseEntity.ok(service.buscarPorNumero(numero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DosimetroResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DosimetroResponse> crear(@Valid @RequestBody DosimetroRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DosimetroResponse> actualizar(@PathVariable Integer id,
                                                         @Valid @RequestBody DosimetroRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(@PathVariable Integer id,
                                          @RequestParam(required = false) String observacion) {
        service.darDeBaja(id, observacion);
        return ResponseEntity.noContent().build();
    }
}