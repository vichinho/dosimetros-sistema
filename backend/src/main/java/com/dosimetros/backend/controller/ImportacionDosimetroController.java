package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.dosimetro.ActualizacionStockResponse;
import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import com.dosimetros.backend.service.ImportacionDosimetroService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/dosimetros/importacion")
public class ImportacionDosimetroController {

    private final ImportacionDosimetroService importacionDosimetroService;

    public ImportacionDosimetroController(ImportacionDosimetroService importacionDosimetroService) {
        this.importacionDosimetroService = importacionDosimetroService;
    }

    @PostMapping("/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportacionDosimetrosResponse> importarExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importacionDosimetroService.importarExcel(file));
    }

    // #8: actualización de stock por archivo con upsert (clave = número).
    @PostMapping("/actualizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActualizacionStockResponse> actualizarStock(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importacionDosimetroService.actualizarStockExcel(file));
    }

    // #17: descargar la plantilla .xlsx con encabezados y una fila de ejemplo.
    @GetMapping("/plantilla")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> plantilla() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"plantilla_carga_dosimetros.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(importacionDosimetroService.plantillaExcel());
    }
}
