package com.dosimetros.backend.controller;

import com.dosimetros.backend.comparador.model.ColumnMapping;
import com.dosimetros.backend.comparador.model.UserRecord;
import com.dosimetros.backend.comparador.service.ComparatorService;
import com.dosimetros.backend.comparador.service.ExcelExporterService;
import com.dosimetros.backend.comparador.service.ExcelReaderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Comparador de listados de dosimetría (integrado desde la herramienta
 * dosimetry-comparator). Recibe dos Excel — el del software y el del cliente —
 * y devuelve un Excel con la comparación. No usa base de datos.
 */
@RestController
@RequestMapping("/api/ejecutivo/comparador")
@PreAuthorize("hasRole('EJECUTIVO')")
public class ComparadorController {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ExcelReaderService excelReaderService;
    private final ComparatorService comparatorService;
    private final ExcelExporterService excelExporterService;

    public ComparadorController(ExcelReaderService excelReaderService,
                                ComparatorService comparatorService,
                                ExcelExporterService excelExporterService) {
        this.excelReaderService = excelReaderService;
        this.comparatorService = comparatorService;
        this.excelExporterService = excelExporterService;
    }

    // Sube ambos archivos y devuelve el Excel comparado en un solo paso. Si el
    // archivo del cliente es la plantilla fija se lee directo; si no, se detecta
    // el mapeo de columnas automáticamente.
    @PostMapping
    public ResponseEntity<byte[]> comparar(
            @RequestParam("softwareFile") MultipartFile softwareFile,
            @RequestParam("clientFile") MultipartFile clientFile) throws IOException {

        if (softwareFile == null || softwareFile.isEmpty()) {
            throw new IllegalArgumentException("Falta el archivo del software.");
        }
        if (clientFile == null || clientFile.isEmpty()) {
            throw new IllegalArgumentException("Falta el archivo del cliente.");
        }

        List<UserRecord> software = excelReaderService.readSoftwareFile(softwareFile);
        byte[] clientBytes = clientFile.getBytes();
        Map<String, Object> headers = excelReaderService.readClientFileHeaders(clientFile);

        List<UserRecord> cliente;
        if (esPlantillaFija(headers)) {
            cliente = excelReaderService.readClientTemplateFromBytes(clientBytes);
        } else {
            ColumnMapping mapping = excelReaderService.autoDetectMappingFromBytes(clientBytes);
            cliente = excelReaderService.readClientFileFromBytes(
                    clientBytes, clientFile.getOriginalFilename(), mapping);
        }

        List<UserRecord> resultados = comparatorService.compare(software, cliente);
        Map<String, Long> resumen = comparatorService.buildSummary(resultados);
        byte[] excel = excelExporterService.exportComparison(resultados, resumen);

        String filename = "comparativo_dosimetria_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(XLSX))
                .body(excel);
    }

    // Plantilla fija para el listado del cliente.
    @GetMapping("/plantilla")
    public ResponseEntity<byte[]> plantilla() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"plantilla_listado_cliente.xlsx\"")
                .contentType(MediaType.parseMediaType(XLSX))
                .body(excelExporterService.generateTemplate());
    }

    // Detecta si el archivo del cliente usa la plantilla fija (hoja
    // LISTADO_CLIENTE o encabezados RUT/NOMBRE/APELLIDO_P…), en cuyo caso no
    // hace falta detectar el mapeo de columnas.
    @SuppressWarnings("unchecked")
    private boolean esPlantillaFija(Map<String, Object> headers) {
        try {
            var sheets = (List<Map<String, Object>>) headers.get("sheets");
            if (sheets == null || sheets.isEmpty()) return false;
            var firstSheet = sheets.get(0);
            String sheetName = (String) firstSheet.get("nombre");
            if ("LISTADO_CLIENTE".equalsIgnoreCase(sheetName)) return true;
            var headerList = (List<String>) firstSheet.get("headers");
            if (headerList == null) return false;
            return headerList.size() >= 5
                    && "RUT".equalsIgnoreCase(headerList.get(0))
                    && "NOMBRE".equalsIgnoreCase(headerList.get(1))
                    && "APELLIDO_P".equalsIgnoreCase(headerList.get(2));
        } catch (Exception e) {
            return false;
        }
    }
}
