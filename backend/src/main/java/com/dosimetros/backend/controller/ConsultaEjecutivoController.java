package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.asignacion.AsignacionResponse;
import com.dosimetros.backend.dto.asignacion.LoteAsignacionResponse;
import com.dosimetros.backend.dto.cliente.ClienteResponse;
import com.dosimetros.backend.service.AsignacionService;
import com.dosimetros.backend.service.ClienteService;
import com.dosimetros.backend.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consultas acotadas al ejecutivo autenticado (HU #3 y #15).
 * Cada endpoint resuelve el ejecutivo del usuario logueado, de modo que
 * solo ve sus propios clientes y asignaciones.
 */
@RestController
@RequestMapping("/api/ejecutivo")
@PreAuthorize("hasRole('EJECUTIVO')")
public class ConsultaEjecutivoController {

    private final CurrentUserService currentUserService;
    private final AsignacionService asignacionService;
    private final ClienteService clienteService;

    public ConsultaEjecutivoController(CurrentUserService currentUserService,
                                       AsignacionService asignacionService,
                                       ClienteService clienteService) {
        this.currentUserService = currentUserService;
        this.asignacionService = asignacionService;
        this.clienteService = clienteService;
    }

    @GetMapping("/mis-clientes")
    public ResponseEntity<List<ClienteResponse>> misClientes() {
        Integer ejecutivoId = currentUserService.requireEjecutivoId();
        return ResponseEntity.ok(clienteService.listarPorEjecutivo(ejecutivoId));
    }

    @GetMapping("/mis-asignaciones")
    public ResponseEntity<List<AsignacionResponse>> misAsignaciones() {
        Integer ejecutivoId = currentUserService.requireEjecutivoId();
        return ResponseEntity.ok(asignacionService.listarPorEjecutivo(ejecutivoId));
    }

    @GetMapping("/mis-lotes")
    public ResponseEntity<List<LoteAsignacionResponse>> misLotes() {
        Integer ejecutivoId = currentUserService.requireEjecutivoId();
        return ResponseEntity.ok(asignacionService.listarLotesPorEjecutivo(ejecutivoId));
    }
}
