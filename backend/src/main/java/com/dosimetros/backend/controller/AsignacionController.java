package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.asignacion.AsignacionMasivaRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionMasivaResponse;
import com.dosimetros.backend.dto.asignacion.AsignacionRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.asignacion.ImportacionAsignacionesResponse;
import com.dosimetros.backend.service.AsignacionService;
import com.dosimetros.backend.service.ImportacionAsignacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService service;
    private final ImportacionAsignacionService importacionService;

    public AsignacionController(AsignacionService service,
                               ImportacionAsignacionService importacionService) {
        this.service = service;
        this.importacionService = importacionService;
    }

    @GetMapping("/dosimetro/{dosimetroId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<AsignacionResponse>> listarPorDosimetro(@PathVariable Integer dosimetroId) {
        return ResponseEntity.ok(service.listarPorDosimetro(dosimetroId));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<AsignacionResponse>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<AsignacionResponse> crear(@Valid @RequestBody AsignacionRequest request) {
        AsignacionResponse response = service.crear(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/masivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<AsignacionMasivaResponse> asignarMasivo(
            @Valid @RequestBody AsignacionMasivaRequest request) {
        return ResponseEntity.status(201).body(service.asignarMasivo(request));
    }

    // #12: carga de asignaciones por archivo con upsert (clave = número).
    @PostMapping("/importacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ImportacionAsignacionesResponse> importarAsignaciones(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importacionService.importarExcel(file));
    }
}
