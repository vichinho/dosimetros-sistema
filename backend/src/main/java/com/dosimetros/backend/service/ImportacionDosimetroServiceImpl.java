package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImportacionDosimetroServiceImpl implements ImportacionDosimetroService {

    @Override
    public ImportacionDosimetrosResponse importarExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar un archivo Excel");
        }

        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Solo se permiten archivos .xlsx");
        }

        ImportacionDosimetrosResponse response = new ImportacionDosimetrosResponse();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            int totalFilas = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                totalFilas++;
            }

            response.setTotalFilas(totalFilas);
            response.setExitosas(0);
            response.setFallidas(0);

            return response;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }
    }
}