package com.dosimetros.backend.controller;

import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import com.dosimetros.backend.service.ImportacionDosimetroService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ImportacionDosimetrosResponse> importarExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importacionDosimetroService.importarExcel(file));
    }
}