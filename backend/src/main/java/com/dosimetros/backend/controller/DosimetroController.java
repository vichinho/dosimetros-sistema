package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroRequest;
import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.service.AsignacionService;
import com.dosimetros.backend.service.DosimetroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dosimetros")
public class DosimetroController {

    private final DosimetroService service;
    private final AsignacionService asignacionService;

    public DosimetroController(DosimetroService service, AsignacionService asignacionService) {
        this.service = service;
        this.asignacionService = asignacionService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<DosimetroResponse>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/stock")
    public ResponseEntity<List<DosimetroResponse>> filtrarStock(
            @RequestParam(required = false) Integer tipoDosimetroId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.filtrarStock(tipoDosimetroId, estado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<DosimetroResponse>> buscarPorNumero(@RequestParam Integer numero) {
        return ResponseEntity.ok(service.buscarPorNumero(numero));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DosimetroResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<AsignacionResponse>> historial(@PathVariable Integer id) {
        return ResponseEntity.ok(asignacionService.listarPorDosimetro(id));
    }

    @PostMapping
    public ResponseEntity<DosimetroResponse> crear(@Valid @RequestBody DosimetroRequest request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DosimetroResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DosimetroRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(
            @PathVariable Integer id,
            @RequestParam(required = false) String observacion) {
        service.darDeBaja(id, observacion);
        return ResponseEntity.noContent().build();
    }
}