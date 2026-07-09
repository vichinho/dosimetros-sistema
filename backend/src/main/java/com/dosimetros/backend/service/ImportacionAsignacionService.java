package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.asignacion.ImportacionAsignacionesResponse;
import com.dosimetros.backend.entity.Asignacion;
import com.dosimetros.backend.entity.Cliente;
import com.dosimetros.backend.entity.Dosimetro;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.entity.Empresa;
import com.dosimetros.backend.entity.TipoPorta;
import com.dosimetros.backend.repository.AsignacionRepository;
import com.dosimetros.backend.repository.ClienteRepository;
import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.EjecutivoRepository;
import com.dosimetros.backend.repository.EmpresaRepository;
import com.dosimetros.backend.repository.TipoPortaRepository;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Carga de asignaciones por archivo con upsert (#12). Clave = número de
 * dosímetro. Columnas (fila 0 = encabezado, datos desde fila 1):
 *   A(0) numero_dosimetro (obligatorio)
 *   B(1) cliente          (razón social, obligatorio)
 *   C(2) ejecutivo        (nombre, obligatorio)
 *   D(3) empresa          (nombre, obligatorio)
 *   E(4) tipo_porta       (nombre, obligatorio; compatible con el dosímetro)
 *   F(5) trimestre        (obligatorio, formato 1T2026)
 *   G(6) link_trello      (obligatorio)
 */
@Service
public class ImportacionAsignacionService {

    private static final int COL_NUMERO    = 0;
    private static final int COL_CLIENTE   = 1;
    private static final int COL_EJECUTIVO = 2;
    private static final int COL_EMPRESA   = 3;
    private static final int COL_PORTA     = 4;
    private static final int COL_TRIMESTRE = 5;
    private static final int COL_LINK      = 6;

    private static final int MAX_FILAS = 200_000;
    private static final Pattern TRIMESTRE = Pattern.compile("^[1-4]T\\d{4}$");

    static {
        ZipSecureFile.setMinInflateRatio(0.01);
    }

    private final AsignacionRepository asignacionRepository;
    private final DosimetroRepository dosimetroRepository;
    private final ClienteRepository clienteRepository;
    private final EjecutivoRepository ejecutivoRepository;
    private final EmpresaRepository empresaRepository;
    private final TipoPortaRepository tipoPortaRepository;

    public ImportacionAsignacionService(AsignacionRepository asignacionRepository,
                                        DosimetroRepository dosimetroRepository,
                                        ClienteRepository clienteRepository,
                                        EjecutivoRepository ejecutivoRepository,
                                        EmpresaRepository empresaRepository,
                                        TipoPortaRepository tipoPortaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.dosimetroRepository = dosimetroRepository;
        this.clienteRepository = clienteRepository;
        this.ejecutivoRepository = ejecutivoRepository;
        this.empresaRepository = empresaRepository;
        this.tipoPortaRepository = tipoPortaRepository;
    }

    // Datos de una fila ya resueltos a entidades.
    private record Fila(Dosimetro dosimetro, Cliente cliente, Ejecutivo ejecutivo,
                        Empresa empresa, TipoPorta tipoPorta, String trimestre, String link) {
    }

    @Transactional
    public ImportacionAsignacionesResponse importarExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar un archivo Excel");
        }
        if (file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Solo se permiten archivos .xlsx");
        }

        ImportacionAsignacionesResponse response = new ImportacionAsignacionesResponse();
        List<String> errores = response.getErrores();
        int creados = 0, actualizados = 0, sinCambios = 0, fallidas = 0;
        Set<Integer> numerosEnArchivo = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            int totalFilas = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (esFilaVacia(row, fmt)) continue;

                totalFilas++;
                if (totalFilas > MAX_FILAS) {
                    throw new IllegalArgumentException(
                            "El archivo supera el máximo de " + MAX_FILAS + " filas permitidas");
                }
                int nFila = row.getRowNum() + 1;

                try {
                    Fila f = parseFila(row, fmt);

                    if (!numerosEnArchivo.add(f.dosimetro().getNumero())) {
                        fallidas++;
                        errores.add("Fila " + nFila + ": número " + f.dosimetro().getNumero()
                                + " repetido dentro del archivo (omitido)");
                        continue;
                    }

                    List<Asignacion> previas = asignacionRepository
                            .findByDosimetroIdOrderByFechaAsignacionDesc(f.dosimetro().getId());

                    if (previas.isEmpty()) {
                        crearAsignacion(f);
                        creados++;
                    } else {
                        Asignacion actual = previas.get(0); // la más reciente
                        if (mismaAsignacion(actual, f)) {
                            sinCambios++;
                        } else {
                            aplicar(actual, f);
                            asignacionRepository.save(actual);
                            // por si el dosímetro no estaba marcado como asignado
                            if (!"asignado".equalsIgnoreCase(f.dosimetro().getEstado())) {
                                f.dosimetro().setEstado("asignado");
                                dosimetroRepository.save(f.dosimetro());
                            }
                            actualizados++;
                        }
                    }
                } catch (Exception e) {
                    fallidas++;
                    errores.add("Fila " + nFila + ": " + e.getMessage());
                }
            }

            response.setTotalFilas(totalFilas);
            response.setCreados(creados);
            response.setActualizados(actualizados);
            response.setSinCambios(sinCambios);
            response.setFallidas(fallidas);

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }

        return response;
    }

    private Fila parseFila(Row row, DataFormatter fmt) {
        String numStr = fmt.formatCellValue(row.getCell(COL_NUMERO)).trim();
        if (numStr.isBlank()) throw new IllegalArgumentException("número de dosímetro vacío");
        int numero;
        try {
            numero = (int) Double.parseDouble(numStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("número de dosímetro inválido: '" + numStr + "'");
        }

        List<Dosimetro> dosimetros = dosimetroRepository.findByNumeroOrderByIdAsc(numero);
        if (dosimetros.isEmpty()) {
            throw new IllegalArgumentException("no existe un dosímetro con número " + numero);
        }
        if (dosimetros.size() > 1) {
            throw new IllegalArgumentException("el número " + numero
                    + " está duplicado en el sistema; no se puede asignar por archivo");
        }
        Dosimetro dosimetro = dosimetros.get(0);

        Cliente cliente = unico(
                clienteRepository.findByRazonSocialIgnoreCaseAndActivoTrue(texto(row, COL_CLIENTE, fmt, "cliente")),
                "cliente", texto(row, COL_CLIENTE, fmt, "cliente"));
        Ejecutivo ejecutivo = unico(
                ejecutivoRepository.findByNombreIgnoreCaseAndActivoTrue(texto(row, COL_EJECUTIVO, fmt, "ejecutivo")),
                "ejecutivo", texto(row, COL_EJECUTIVO, fmt, "ejecutivo"));
        Empresa empresa = unico(
                empresaRepository.findByNombreIgnoreCaseAndActivaTrue(texto(row, COL_EMPRESA, fmt, "empresa")),
                "empresa", texto(row, COL_EMPRESA, fmt, "empresa"));

        String portaNombre = texto(row, COL_PORTA, fmt, "tipo de porta");
        TipoPorta tipoPorta = tipoPortaRepository
                .findByNombreAndTipoDosimetroId(portaNombre, dosimetro.getTipoDosimetro().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "tipo de porta '" + portaNombre + "' no compatible con el dosímetro #" + dosimetro.getNumero()));

        String trimestre = fmt.formatCellValue(row.getCell(COL_TRIMESTRE)).trim().toUpperCase();
        if (!TRIMESTRE.matcher(trimestre).matches()) {
            throw new IllegalArgumentException("trimestre inválido: '" + trimestre + "' (formato 1T2026)");
        }

        String link = fmt.formatCellValue(row.getCell(COL_LINK)).trim();
        if (link.isBlank()) throw new IllegalArgumentException("el link de Trello es obligatorio");

        return new Fila(dosimetro, cliente, ejecutivo, empresa, tipoPorta, trimestre, link);
    }

    private String texto(Row row, int col, DataFormatter fmt, String campo) {
        String v = fmt.formatCellValue(row.getCell(col)).trim();
        if (v.isBlank()) throw new IllegalArgumentException(campo + " vacío");
        return v;
    }

    private <T> T unico(List<T> encontrados, String campo, String valor) {
        if (encontrados.isEmpty()) {
            throw new IllegalArgumentException(campo + " no encontrado: '" + valor + "'");
        }
        if (encontrados.size() > 1) {
            throw new IllegalArgumentException(campo + " ambiguo (hay varios): '" + valor + "'");
        }
        return encontrados.get(0);
    }

    private boolean mismaAsignacion(Asignacion a, Fila f) {
        return Objects.equals(a.getCliente().getId(), f.cliente().getId())
                && Objects.equals(a.getEjecutivo().getId(), f.ejecutivo().getId())
                && Objects.equals(a.getEmpresa().getId(), f.empresa().getId())
                && Objects.equals(a.getTipoPorta().getId(), f.tipoPorta().getId())
                && Objects.equals(a.getTrimestre(), f.trimestre())
                && Objects.equals(a.getLinkTrello(), f.link());
    }

    private void aplicar(Asignacion a, Fila f) {
        a.setCliente(f.cliente());
        a.setEjecutivo(f.ejecutivo());
        a.setEmpresa(f.empresa());
        a.setTipoPorta(f.tipoPorta());
        a.setTrimestre(f.trimestre());
        a.setLinkTrello(f.link());
    }

    private void crearAsignacion(Fila f) {
        Asignacion a = new Asignacion();
        a.setDosimetro(f.dosimetro());
        a.setTarea(f.dosimetro().getTarea());
        a.setNumeroBandeja(f.dosimetro().getNumeroBandeja());
        a.setSlotBandeja(f.dosimetro().getSlotBandeja());
        a.setFechaAsignacion(LocalDate.now());
        aplicar(a, f);
        asignacionRepository.save(a);

        f.dosimetro().setEstado("asignado");
        dosimetroRepository.save(f.dosimetro());
    }

    private boolean esFilaVacia(Row row, DataFormatter fmt) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (!fmt.formatCellValue(cell).trim().isEmpty()) return false;
            }
        }
        return true;
    }
}
