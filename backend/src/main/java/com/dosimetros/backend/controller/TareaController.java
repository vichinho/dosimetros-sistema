package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.tarea.TareaRequest;
import com.dosimetros.backend.dto.tarea.TareaResponse;
import com.dosimetros.backend.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<TareaResponse>> listar() {
        return ResponseEntity.ok(tareaService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TareaResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        return ResponseEntity.ok(tareaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<TareaResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TareaRequest request) {
        return ResponseEntity.ok(tareaService.actualizar(id, request));
    }
}
