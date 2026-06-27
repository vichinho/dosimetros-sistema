package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Tarea;
import com.dosimetros.backend.entity.TipoDosimetro;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.TareaRepository;
import com.dosimetros.backend.repository.TipoDosimetroRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Columnas esperadas en el Excel (fila 0 = encabezado, datos desde fila 1):
 *   A(0)  = NUMDOSIM       -> dosimetro.numero          (numerico)
 *   B(1)  = TIPO_DOSIMETRO -> tipoDosimetro.nombre      (texto: TLD, OSL, etc.)
 *   C(2)  = TIPO_PORTA     -> tipoPorta.nombre          (texto, opcional)
 *   D(3)  = NUM_BANDEJA    -> dosimetro.numeroBandeja   (numerico, opcional)
 *   E(4)  = SLOT_BANDEJA   -> dosimetro.slotBandeja     (numerico, opcional)
 *   F(5)  = NUM_REPOSITORIO -> tarea.numeroTarea        (texto, opcional)
 *   G(6)  = OBSERVACION    -> dosimetro.observacion     (texto, opcional)
 */
@Service
public class ImportacionDosimetroServiceImpl implements ImportacionDosimetroService {

    private static final int COL_NUMDOSIM       = 0;
    private static final int COL_TIPO_DOSIMETRO = 1;
    private static final int COL_TIPO_PORTA     = 2;
    private static final int COL_NUM_BANDEJA    = 3;
    private static final int COL_SLOT_BANDEJA   = 4;
    private static final int COL_NUM_REPOSITORIO = 5;
    private static final int COL_OBSERVACION    = 6;

    private final DosimetroRepository dosimetroRepository;
    private final TipoDosimetroRepository tipoDosimetroRepository;
    private final TipoPortaRepository tipoPortaRepository;
    private final TareaRepository tareaRepository;

    public ImportacionDosimetroServiceImpl(DosimetroRepository dosimetroRepository,
                                           TipoDosimetroRepository tipoDosimetroRepository,
                                           TipoPortaRepository tipoPortaRepository,
                                           TareaRepository tareaRepository) {
        this.dosimetroRepository = dosimetroRepository;
        this.tipoDosimetroRepository = tipoDosimetroRepository;
        this.tipoPortaRepository = tipoPortaRepository;
        this.tareaRepository = tareaRepository;
    }

    @Override
    @Transactional
    public ImportacionDosimetrosResponse importarExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar un archivo Excel");
        }
        if (file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Solo se permiten archivos .xlsx");
        }

        ImportacionDosimetrosResponse response = new ImportacionDosimetrosResponse();
        List<String> errores = new ArrayList<>();
        int exitosas = 0;
        int fallidas = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            int totalFilas = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // saltar encabezado

                // Ignorar filas completamente vacías
                if (esFilaVacia(row)) continue;

                totalFilas++;
                int fila = row.getRowNum() + 1; // numero legible para errores (1-based)

                try {
                    // --- NUMDOSIM (obligatorio) ---
                    String numDosimStr = formatter.formatCellValue(row.getCell(COL_NUMDOSIM)).trim();
                    if (numDosimStr.isBlank()) {
                        throw new IllegalArgumentException("NUMDOSIM vacío");
                    }
                    int numeroDosimetro;
                    try {
                        numeroDosimetro = (int) Double.parseDouble(numDosimStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("NUMDOSIM no es un número válido: '" + numDosimStr + "'");
                    }

                    // --- TIPO_DOSIMETRO (obligatorio) ---
                    String tipoDosimNombre = formatter.formatCellValue(row.getCell(COL_TIPO_DOSIMETRO)).trim();
                    if (tipoDosimNombre.isBlank()) {
                        throw new IllegalArgumentException("TIPO_DOSIMETRO vacío");
                    }
                    TipoDosimetro tipoDosimetro = tipoDosimetroRepository.findByNombre(tipoDosimNombre)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Tipo de dosímetro no encontrado: '" + tipoDosimNombre + "'"));

                    boolean esOSL = "OSL".equalsIgnoreCase(tipoDosimetro.getNombre());

                    // --- TIPO_PORTA (opcional, ignorado para OSL) ---
                    TipoPorta tipoPorta = null;
                    if (!esOSL) {
                        String tipoPortaNombre = formatter.formatCellValue(row.getCell(COL_TIPO_PORTA)).trim();
                        if (!tipoPortaNombre.isBlank()) {
                            tipoPorta = tipoPortaRepository
                                    .findByNombreAndTipoDosimetroId(tipoPortaNombre, tipoDosimetro.getId())
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Tipo de porta '" + tipoPortaNombre + "' no compatible con '" + tipoDosimNombre + "'"));
                        }
                    }

                    // --- NUM_BANDEJA (opcional, ignorado para OSL) ---
                    Integer numeroBandeja = null;
                    if (!esOSL) {
                        String bandStr = formatter.formatCellValue(row.getCell(COL_NUM_BANDEJA)).trim();
                        if (!bandStr.isBlank()) {
                            numeroBandeja = (int) Double.parseDouble(bandStr);
                        }
                    }

                    // --- SLOT_BANDEJA (opcional, ignorado para OSL) ---
                    Integer slotBandeja = null;
                    if (!esOSL) {
                        String slotStr = formatter.formatCellValue(row.getCell(COL_SLOT_BANDEJA)).trim();
                        if (!slotStr.isBlank()) {
                            slotBandeja = (int) Double.parseDouble(slotStr);
                        }
                    }

                    // --- NUM_REPOSITORIO / Tarea (opcional, ignorado para OSL) ---
                    Tarea tarea = null;
                    if (!esOSL) {
                        String numRepo = formatter.formatCellValue(row.getCell(COL_NUM_REPOSITORIO)).trim();
                        if (!numRepo.isBlank()) {
                            tarea = tareaRepository.findByNumeroTarea(numRepo)
                                    .orElseGet(() -> {
                                        Tarea nueva = new Tarea();
                                        nueva.setNumeroTarea(numRepo);
                                        nueva.setFechaCreacion(LocalDate.now());
                                        return tareaRepository.save(nueva);
                                    });
                        }
                    }

                    // --- OBSERVACION (opcional) ---
                    String observacion = formatter.formatCellValue(row.getCell(COL_OBSERVACION)).trim();

                    // --- Crear o actualizar dosímetro ---
                    Dosimetro dosimetro = dosimetroRepository
                            .findByNumeroAndEstado(numeroDosimetro, "disponible")
                            .orElse(new Dosimetro());

                    dosimetro.setNumero(numeroDosimetro);
                    dosimetro.setTipoDosimetro(tipoDosimetro);
                    dosimetro.setTipoPorta(tipoPorta);
                    dosimetro.setTarea(tarea);
                    dosimetro.setNumeroBandeja(numeroBandeja);
                    dosimetro.setSlotBandeja(slotBandeja);
                    dosimetro.setEstado("disponible");
                    if (!observacion.isBlank()) {
                        dosimetro.setObservacion(observacion);
                    }

                    dosimetroRepository.save(dosimetro);
                    exitosas++;

                } catch (Exception e) {
                    fallidas++;
                    errores.add("Fila " + fila + ": " + e.getMessage());
                }
            }

            response.setTotalFilas(totalFilas);
            response.setExitosas(exitosas);
            response.setFallidas(fallidas);
            response.setErrores(errores);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }

        return response;
    }

    private boolean esFilaVacia(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = new DataFormatter().formatCellValue(cell).trim();
                if (!val.isEmpty()) return false;
            }
        }
        return true;
    }
}
