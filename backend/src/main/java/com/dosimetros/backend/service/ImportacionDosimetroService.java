package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dosimetro.ActualizacionStockResponse;
import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportacionDosimetroService {
    ImportacionDosimetrosResponse importarExcel(MultipartFile file);

    // #8: actualización de stock por archivo con upsert (clave = número).
    ActualizacionStockResponse actualizarStockExcel(MultipartFile file);
}
