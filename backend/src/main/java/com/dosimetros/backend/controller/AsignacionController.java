package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.asignacion.AsignacionMasivaRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionMasivaResponse;
import com.dosimetros.backend.dto.asignacion.AsignacionRequest;
import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.asignacion.CorreccionMasivaRequest;
import com.dosimetros.backend.dto.asignacion.ImportacionAsignacionesResponse;
import com.dosimetros.backend.dto.asignacion.MarcarEnvioRequest;
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

    // #14 (Correcciones): buscar asignaciones con filtros para corregir en lote.
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<AsignacionResponse>> buscarAsignaciones(
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer ejecutivoId,
            @RequestParam(required = false) Integer empresaId,
            @RequestParam(required = false) String trimestre,
            @RequestParam(required = false) Integer tipoPortaId,
            @RequestParam(required = false) String link) {
        return ResponseEntity.ok(
                service.buscarAsignaciones(clienteId, ejecutivoId, empresaId, trimestre, tipoPortaId, link));
    }

    // #14 (Correcciones): corregir un mismo campo en varias asignaciones a la vez.
    @PatchMapping("/correccion-masiva")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<Integer> correccionMasiva(@Valid @RequestBody CorreccionMasivaRequest request) {
        return ResponseEntity.ok(service.correccionMasiva(request));
    }

    // #18: asignaciones pendientes de envío (admin/operador, con filtro de ejecutivo).
    @GetMapping("/pendientes-envio")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<AsignacionResponse>> pendientesEnvio(
            @RequestParam(required = false) Integer ejecutivoId,
            @RequestParam(required = false) String trimestre) {
        return ResponseEntity.ok(service.pendientesEnvio(ejecutivoId, trimestre));
    }

    // #18: marcar asignaciones como enviadas (o revertir).
    @PatchMapping("/envio")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<Integer> marcarEnvio(@Valid @RequestBody MarcarEnvioRequest request) {
        return ResponseEntity.ok(service.marcarEnvio(request.getAsignacionIds(), request.isEnviado()));
    }
}
