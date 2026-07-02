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
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Columnas de la plantilla PLANTILLA_CARGA_MASIVA
 * (fila 0 = encabezado, datos desde fila 1):
 *   A(0) = numero_dosimetro -> dosimetro.numero        (numerico, obligatorio)
 *   B(1) = tipo_dosimetro   -> tipoDosimetro.nombre    (texto: TLD, OSL, Cristal; obligatorio)
 *   C(2) = tipo_porta       -> tipoPorta.nombre        (texto, opcional)
 *   D(3) = numero_tarea     -> tarea.numeroTarea       (texto, opcional; vacío para OSL)
 *   E(4) = numero_bandeja   -> dosimetro.numeroBandeja (numerico, opcional; vacío para OSL)
 *   F(5) = slot_bandeja     -> dosimetro.slotBandeja   (numerico, opcional; vacío para OSL)
 */
@Service
public class ImportacionDosimetroServiceImpl implements ImportacionDosimetroService {

    private static final int COL_NUMDOSIM        = 0;
    private static final int COL_TIPO_DOSIMETRO  = 1;
    private static final int COL_TIPO_PORTA      = 2;
    private static final int COL_NUM_REPOSITORIO = 3;
    private static final int COL_NUM_BANDEJA     = 4;
    private static final int COL_SLOT_BANDEJA    = 5;

    // Límite de filas de datos por archivo (evita agotar memoria con archivos gigantes).
    private static final int MAX_FILAS = 200_000;

    static {
        // Defensa ante "zip bombs": exige un ratio mínimo de compresión al descomprimir.
        ZipSecureFile.setMinInflateRatio(0.01);
    }

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
        int omitidas = 0;
        // Para detectar números repetidos dentro del mismo archivo.
        Set<Integer> numerosEnArchivo = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            int totalFilas = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // saltar encabezado

                // Ignorar filas completamente vacías
                if (esFilaVacia(row)) continue;

                totalFilas++;
                if (totalFilas > MAX_FILAS) {
                    throw new IllegalArgumentException(
                            "El archivo supera el máximo de " + MAX_FILAS + " filas permitidas");
                }
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

                    // --- Validación de duplicados ---
                    // Mismo número repetido dentro del archivo.
                    if (!numerosEnArchivo.add(numeroDosimetro)) {
                        omitidas++;
                        errores.add("Fila " + fila + ": número " + numeroDosimetro
                                + " repetido dentro del archivo (omitido)");
                        continue;
                    }
                    // Número ya existente en el sistema: no se duplica el dosímetro.
                    // (La reasignación entre trimestres se maneja por asignaciones.)
                    if (dosimetroRepository.existsByNumero(numeroDosimetro)) {
                        omitidas++;
                        errores.add("Fila " + fila + ": número " + numeroDosimetro
                                + " ya existe en el sistema (omitido, no se duplica)");
                        continue;
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

                    // --- TIPO_PORTA (opcional; si viene, debe ser compatible con el tipo) ---
                    TipoPorta tipoPorta = null;
                    String tipoPortaNombre = formatter.formatCellValue(row.getCell(COL_TIPO_PORTA)).trim();
                    if (!tipoPortaNombre.isBlank()) {
                        tipoPorta = tipoPortaRepository
                                .findByNombreAndTipoDosimetroId(tipoPortaNombre, tipoDosimetro.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Tipo de porta '" + tipoPortaNombre + "' no compatible con '" + tipoDosimNombre + "'"));
                    }

                    // Los OSL se cargan sin tarea, bandeja ni slot.
                    Tarea tarea = null;
                    Integer numeroBandeja = null;
                    Integer slotBandeja = null;
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

                        String bandStr = formatter.formatCellValue(row.getCell(COL_NUM_BANDEJA)).trim();
                        if (!bandStr.isBlank()) {
                            numeroBandeja = (int) Double.parseDouble(bandStr);
                        }

                        String slotStr = formatter.formatCellValue(row.getCell(COL_SLOT_BANDEJA)).trim();
                        if (!slotStr.isBlank()) {
                            slotBandeja = (int) Double.parseDouble(slotStr);
                        }
                    }

                    // --- Crear dosímetro (los duplicados ya fueron filtrados) ---
                    Dosimetro dosimetro = new Dosimetro();

                    dosimetro.setNumero(numeroDosimetro);
                    dosimetro.setTipoDosimetro(tipoDosimetro);
                    dosimetro.setTipoPorta(tipoPorta);
                    dosimetro.setTarea(tarea);
                    dosimetro.setNumeroBandeja(numeroBandeja);
                    dosimetro.setSlotBandeja(slotBandeja);
                    dosimetro.setEstado("disponible");

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
            response.setOmitidas(omitidas);
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
