package com.dosimetros.backend.comparador.service;

import com.dosimetros.backend.comparador.model.UserRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Exporta el resultado de la comparación a un archivo Excel con colores.
 *
 * Colores:
 *   Verde    (#C6EFCE) → MANTENER
 *   Rojo     (#FFC7CE) → QUITAR
 *   Amarillo (#FFEB9C) → NUEVO
 *   Naranja  (#FFCC99) → MANTENER con alertas
 */
@Service
public class ExcelExporterService {

    public byte[] exportComparison(List<UserRecord> results, Map<String, Long> summary) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ── Hoja 1: Resumen ──
            createSummarySheet(workbook, summary);

            // ── Hoja 2: Comparativo completo ──
            createComparisonSheet(workbook, results);

            // ── Hoja 3: Solo QUITAR ──
            createFilteredSheet(workbook, results, UserRecord.ComparisonResult.QUITAR, "QUITAR");

            // ── Hoja 4: Solo NUEVO ──
            createFilteredSheet(workbook, results, UserRecord.ComparisonResult.NUEVO, "NUEVO");

            // ── Hoja 5: Alertas ──
            createAlertsSheet(workbook, results);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ─────────────────────────────────────────────
    //  HOJA RESUMEN
    // ─────────────────────────────────────────────

    private void createSummarySheet(XSSFWorkbook workbook, Map<String, Long> summary) {
        Sheet sheet = workbook.createSheet("RESUMEN");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 3000);

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook, new XSSFColor(new byte[]{(byte)68,(byte)114,(byte)196}, null));
        CellStyle mantenerStyle = createColorStyle(workbook, "C6EFCE", "375623");
        CellStyle quitarStyle   = createColorStyle(workbook, "C00000", "FFFFFF");
        CellStyle nuevoStyle    = createColorStyle(workbook, "FFEB9C", "7D6608");
        CellStyle alertaStyle   = createColorStyle(workbook, "FFCC99", "974706");

        int rowNum = 0;
        Row title = sheet.createRow(rowNum++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("RESUMEN COMPARATIVO DE DOSIMETRÍA");
        titleCell.setCellStyle(titleStyle);

        rowNum++; // espacio

        String[][] rows = {
                {"TOTAL REGISTROS",    String.valueOf(summary.get("total"))},
                {"✅ MANTENER",        String.valueOf(summary.get("mantener"))},
                {"❌ QUITAR",          String.valueOf(summary.get("quitar"))},
                {"🆕 NUEVO",           String.valueOf(summary.get("nuevo"))},
                {"⚠ CON ALERTAS",     String.valueOf(summary.get("conAlertas"))}
        };

        CellStyle[] styles = {headerStyle, mantenerStyle, quitarStyle, nuevoStyle, alertaStyle};

        for (int i = 0; i < rows.length; i++) {
            Row row = sheet.createRow(rowNum++);
            Cell c0 = row.createCell(0);
            Cell c1 = row.createCell(1);
            c0.setCellValue(rows[i][0]);
            c1.setCellValue(rows[i][1]);
            c0.setCellStyle(styles[i]);
            c1.setCellStyle(styles[i]);
        }
    }

    // ─────────────────────────────────────────────
    //  HOJA COMPARATIVO COMPLETO
    // ─────────────────────────────────────────────

    private void createComparisonSheet(XSSFWorkbook workbook, List<UserRecord> results) {
        Sheet sheet = workbook.createSheet("COMPARATIVO");

        // Anchos de columna
        int[] widths = {4000, 8000, 6000, 8000, 4000, 8000, 4000, 8000, 6000, 10000};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        // Estilos
        CellStyle headerStyle   = createHeaderStyle(workbook, new XSSFColor(new byte[]{(byte)68,(byte)114,(byte)196}, null));
        CellStyle mantenerStyle      = createColorStyle(workbook, "C6EFCE", "375623");
        CellStyle quitarStyle        = createColorStyle(workbook, "C00000", "FFFFFF");
        CellStyle nuevoStyle         = createColorStyle(workbook, "FFEB9C", "7D6608");
        CellStyle verificarStyle     = createColorStyle(workbook, "F8BBD0", "880E4F");
        CellStyle alertaMantenerStyle = createColorStyle(workbook, "FFCC99", "974706");
        CellStyle defaultStyle       = workbook.createCellStyle();

        // Encabezados
        String[] headers = {
                "ID SERVICIO", "SEDE (SOFTWARE)", "ÁREA (SOFTWARE)", "UBICACION (SOFTWARE)",
                "RESULTADO", "USUARIO (SOFTWARE)", "RUT (SOFTWARE)",
                "RUT (CLIENTE)", "NOMBRE COMPLETO (CLIENTE)", "ALERTAS",
                "ÁREA (CLIENTE)", "SEDE (CLIENTE)", "UBICACION (CLIENTE)"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos — con fila vacía al final de cada (sede + área) para facilitar lectura
        int rowNum = 1;
        String areaActual = null;
        String sedeActual = null;
        for (UserRecord r : results) {
            String areaR = nvl(r.getArea()).isBlank() ? nvl(r.getAreaCliente()) : nvl(r.getArea());
            String sedeR = nvl(r.getSede()).isBlank() ? nvl(r.getSedeCliente()) : nvl(r.getSede());
            String claveActual = sedeR + "||" + areaR;
            String claveAnterior = sedeActual + "||" + areaActual;

            // Si cambió sede o área respecto a la fila anterior, dejar fila vacía
            if (areaActual != null && !claveActual.equals(claveAnterior)) {
                rowNum++; // saltar una fila vacía
            }
            areaActual = areaR;
            sedeActual = sedeR;

            Row row = sheet.createRow(rowNum++);

            // Elegir estilo según resultado y alertas
            CellStyle rowStyle;
            boolean tieneAlerta = r.getAlerta() != null && !r.getAlerta().isBlank();
            rowStyle = switch (r.getResultado()) {
                case MANTENER  -> tieneAlerta ? alertaMantenerStyle : mantenerStyle;
                case QUITAR    -> quitarStyle;
                case NUEVO     -> nuevoStyle;
                case VERIFICAR -> verificarStyle;
                default        -> defaultStyle;
            };

            String[] values = {
                    nvl(r.getServicio()),
                    nvl(r.getSede()),
                    nvl(r.getArea()),
                    nvl(r.getUbicacion()),
                    r.getResultado() != null ? r.getResultado().name() : "",
                    nvl(r.getUsuario()),
                    nvl(r.getRut()),
                    nvl(r.getRutCliente()),
                    nvl(r.getNombreCompletoCliente()),
                    nvl(r.getAlerta()),
                    nvl(r.getAreaCliente()),
                    nvl(r.getSedeCliente()),
                    nvl(r.getUbicacionCliente())
            };

            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(rowStyle);
            }
        }

        // Autofilter
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        sheet.createFreezePane(0, 1);
    }

    // ─────────────────────────────────────────────
    //  HOJAS FILTRADAS (solo QUITAR o solo NUEVO)
    // ─────────────────────────────────────────────

    private void createFilteredSheet(XSSFWorkbook workbook, List<UserRecord> results,
                                     UserRecord.ComparisonResult filter, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);
        int[] widths = {8000, 6000, 8000, 4000, 10000};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        CellStyle headerStyle = createHeaderStyle(workbook,
                new XSSFColor(new byte[]{(byte)68,(byte)114,(byte)196}, null));
        CellStyle rowStyle = filter == UserRecord.ComparisonResult.QUITAR
                ? createColorStyle(workbook, "C00000", "FFFFFF")
                : createColorStyle(workbook, "FFEB9C", "7D6608");

        String[] headers = filter == UserRecord.ComparisonResult.QUITAR
                ? new String[]{"ID SERVICIO", "SEDE", "ÁREA", "USUARIO (SOFTWARE)", "RUT (SOFTWARE)", "ALERTAS"}
                : new String[]{"SEDE CLIENTE", "ÁREA CLIENTE", "NOMBRE (CLIENTE)", "RUT (CLIENTE)", "ALERTAS"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (UserRecord r : results) {
            if (r.getResultado() != filter) continue;
            Row row = sheet.createRow(rowNum++);
            String[] values = filter == UserRecord.ComparisonResult.QUITAR
                    ? new String[]{nvl(r.getServicio()), nvl(r.getSede()), nvl(r.getArea()), nvl(r.getUsuario()), nvl(r.getRut()), nvl(r.getAlerta())}
                    : new String[]{nvl(r.getSedeCliente()), nvl(r.getAreaCliente()), nvl(r.getNombreCompletoCliente()), nvl(r.getRutCliente()), nvl(r.getAlerta())};
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(rowStyle);
            }
        }

        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(rowNum - 1, 1), 0, headers.length - 1));
        sheet.createFreezePane(0, 1);
    }

    // ─────────────────────────────────────────────
    //  HOJA ALERTAS
    // ─────────────────────────────────────────────

    private void createAlertsSheet(XSSFWorkbook workbook, List<UserRecord> results) {
        Sheet sheet = workbook.createSheet("ALERTAS");
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 12000);

        CellStyle headerStyle = createHeaderStyle(workbook,
                new XSSFColor(new byte[]{(byte)197,(byte)90,(byte)17}, null));
        CellStyle alertStyle = createColorStyle(workbook, "FFCC99", "974706");

        String[] headers = {"RESULTADO", "USUARIO / NOMBRE", "RUT", "ALERTA"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (UserRecord r : results) {
            if (r.getAlerta() == null || r.getAlerta().isBlank()) continue;
            Row row = sheet.createRow(rowNum++);
            String nombre = r.getUsuario() != null ? r.getUsuario() : r.getNombreCompletoCliente();
            String rut = r.getRut() != null ? r.getRut() : r.getRutCliente();
            String[] values = {
                    r.getResultado() != null ? r.getResultado().name() : "NUEVO",
                    nvl(nombre),
                    nvl(rut),
                    nvl(r.getAlerta())
            };
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values[i]);
                cell.setCellStyle(alertStyle);
            }
        }
        sheet.createFreezePane(0, 1);
    }

    // ─────────────────────────────────────────────
    //  ESTILOS
    // ─────────────────────────────────────────────

    private XSSFColor hexToXSSFColor(String hex) {
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    private CellStyle createColorStyle(XSSFWorkbook wb, String bgHex, String fgHex) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(hexToXSSFColor(bgHex));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        XSSFFont font = (XSSFFont) wb.createFont();
        font.setColor(hexToXSSFColor(fgHex));
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb, XSSFColor bgColor) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(bgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private String nvl(String val) {
        return val != null ? val : "";
    }

    // ─────────────────────────────────────────────
    //  PLANTILLA PARA ARCHIVO CLIENTE
    // ─────────────────────────────────────────────

    public byte[] generateTemplate() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ── Hoja 1: Plantilla de datos ──
            Sheet dataSheet = workbook.createSheet("LISTADO_CLIENTE");

            // Estilo encabezado
            CellStyle headerStyle = createHeaderStyle(workbook,
                    new XSSFColor(new byte[]{(byte)68,(byte)114,(byte)196}, null));

            // Estilo instrucción (celda amarilla)
            XSSFCellStyle instrStyle = workbook.createCellStyle();
            instrStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255,(byte)235,(byte)156}, null));
            instrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont instrFont = workbook.createFont();
            instrFont.setItalic(true);
            instrFont.setColor(new XSSFColor(new byte[]{(byte)125,(byte)100,(byte)0}, null));
            instrStyle.setFont(instrFont);
            instrStyle.setBorderBottom(BorderStyle.THIN);
            instrStyle.setBorderTop(BorderStyle.THIN);
            instrStyle.setBorderLeft(BorderStyle.THIN);
            instrStyle.setBorderRight(BorderStyle.THIN);

            // Estilo dato normal
            XSSFCellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Anchos de columna
            dataSheet.setColumnWidth(0, 4000);  // RUT
            dataSheet.setColumnWidth(1, 7000);  // NOMBRE
            dataSheet.setColumnWidth(2, 5000);  // APELLIDO_P
            dataSheet.setColumnWidth(3, 5000);  // APELLIDO_M
            dataSheet.setColumnWidth(4, 7000);  // AREA
            dataSheet.setColumnWidth(5, 9000);  // SEDE
            dataSheet.setColumnWidth(6, 6000);  // UBICACION

            // Fila de encabezados
            String[] headers = {"RUT", "NOMBRE", "APELLIDO_P", "APELLIDO_M", "AREA", "SEDE", "UBICACION"};
            Row headerRow = dataSheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fila de instrucciones
            String[] instrucciones = {
                "Ej: 12345678-9",
                "Nombre(s) o nombre completo si viene junto",
                "Apellido paterno (opcional si NOMBRE es completo)",
                "Apellido materno (opcional)",
                "Área exacta o aproximada",
                "Sede (opcional)",
                "PERSONAL, ABDOMINAL, ANILLO, PULSERA, CRISTALINO, TIROIDEO, CONTROL, AMBIENTAL, REFERENCIA"
            };
            Row instrRow = dataSheet.createRow(1);
            instrRow.setHeightInPoints(30);
            for (int i = 0; i < instrucciones.length; i++) {
                Cell cell = instrRow.createCell(i);
                cell.setCellValue(instrucciones[i]);
                cell.setCellStyle(instrStyle);
            }

            // 50 filas vacías para rellenar
            for (int r = 2; r < 52; r++) {
                Row row = dataSheet.createRow(r);
                row.setHeightInPoints(16);
                for (int c = 0; c < headers.length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Freeze encabezados
            dataSheet.createFreezePane(0, 2);

            // ── Hoja 2: Instrucciones ──
            Sheet instrSheet = workbook.createSheet("INSTRUCCIONES");
            instrSheet.setColumnWidth(0, 15000);

            CellStyle titleStyle = createTitleStyle(workbook);
            XSSFCellStyle bodyStyle = workbook.createCellStyle();
            XSSFFont bodyFont = workbook.createFont();
            bodyFont.setFontHeightInPoints((short)10);
            bodyStyle.setFont(bodyFont);
            bodyStyle.setWrapText(true);

            String[][] instrData = {
                {"INSTRUCCIONES DE USO - PLANTILLA COMPARADOR DE DOSIMETRÍA"},
                {""},
                {"1. CÓMO USAR ESTA PLANTILLA"},
                {"   Copia los datos del listado que te envió el cliente en las columnas correspondientes de la hoja LISTADO_CLIENTE."},
                {""},
                {"2. COLUMNA RUT"},
                {"   - Ingresa el RUT con guión y dígito verificador: 12345678-9"},
                {"   - Se aceptan RUTs con o sin puntos, el sistema los normaliza automáticamente."},
                {""},
                {"3. COLUMNA NOMBRE"},
                {"   - Si el cliente manda el nombre completo en una sola celda: ponlo todo aquí y deja APELLIDO_P y APELLIDO_M vacíos."},
                {"   - Ejemplo: 'JUAN PEREZ GOMEZ' en NOMBRE, vacío en APELLIDO_P y APELLIDO_M."},
                {""},
                {"4. COLUMNAS APELLIDO_P y APELLIDO_M"},
                {"   - Si el cliente manda el nombre separado: pon el nombre en NOMBRE, apellido paterno en APELLIDO_P y materno en APELLIDO_M."},
                {"   - APELLIDO_M es opcional, puedes dejarlo vacío si el cliente no lo manda."},
                {""},
                {"5. COLUMNA AREA"},
                {"   - Copia el área tal como la manda el cliente. El sistema acepta diferencias menores (tildes, mayúsculas, abreviaciones)."},
                {""},
                {"6. COLUMNA SEDE"},
                {"   - Si el cliente no manda sede, déjala vacía. El sistema comparará solo por área."},
                {""},
                {"7. PUEDES MEZCLAR FORMATOS"},
                {"   - Algunas filas pueden tener nombre completo en NOMBRE y otras con nombre separado en columnas."},
                {"   - El sistema detecta automáticamente qué formato usar en cada fila."},
            };

            for (int i = 0; i < instrData.length; i++) {
                Row row = instrSheet.createRow(i);
                row.setHeightInPoints(18);
                Cell cell = row.createCell(0);
                cell.setCellValue(instrData[i][0]);
                if (i == 0) cell.setCellStyle(titleStyle);
                else cell.setCellStyle(bodyStyle);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}