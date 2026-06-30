package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.dashboard.DashboardKpisResponse;
import com.dosimetros.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * KPIs para el dashboard del administrador (HU #17).
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/kpis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardKpisResponse> kpis(
            @RequestParam(required = false) String trimestre) {
        return ResponseEntity.ok(dashboardService.obtenerKpis(trimestre));
    }
}
