package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.dosimetro.ActualizarTipoPortaRangoRequest;
import com.dosimetros.backend.dto.dosimetro.ActualizarTipoPortaRangoResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroDetalleResponse;
import com.dosimetros.backend.dto.dosimetro.DosimetroRequest;
import com.dosimetros.backend.dto.dosimetro.DosimetroResponse;
import com.dosimetros.backend.service.AsignacionService;
import com.dosimetros.backend.service.DosimetroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<DosimetroResponse>> listarDisponibles() {
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<DosimetroResponse>> filtrarStock(
            @RequestParam(required = false) Integer tipoDosimetroId,
            @RequestParam(required = false) Integer tipoPortaId,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.filtrarStock(tipoDosimetroId, tipoPortaId, estado));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<DosimetroDetalleResponse>> buscarPorNumero(@RequestParam Integer numero) {
        return ResponseEntity.ok(service.buscarDetallePorNumero(numero));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<DosimetroResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<AsignacionResponse>> historial(@PathVariable Integer id) {
        return ResponseEntity.ok(asignacionService.listarPorDosimetro(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DosimetroResponse> crear(@Valid @RequestBody DosimetroRequest request) {
        DosimetroResponse response = service.crear(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DosimetroResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DosimetroRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/rango-porta")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<ActualizarTipoPortaRangoResponse> actualizarTipoPortaPorRango(
            @Valid @RequestBody ActualizarTipoPortaRangoRequest request) {
        return ResponseEntity.ok(service.actualizarTipoPortaPorRango(request));
    }

    @PatchMapping("/{id}/liberar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<Void> liberar(@PathVariable Integer id) {
        service.liberar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/baja")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> darDeBaja(
            @PathVariable Integer id,
            @RequestParam(required = false) String observacion) {
        service.darDeBaja(id, observacion);
        return ResponseEntity.noContent().build();
    }
}
