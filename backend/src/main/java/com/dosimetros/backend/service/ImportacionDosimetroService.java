package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.dosimetro.ImportacionDosimetrosResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImportacionDosimetroService {
    ImportacionDosimetrosResponse importarExcel(MultipartFile file);
}