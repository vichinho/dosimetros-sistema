package com.dosimetros.backend;

import com.dosimetros.backend.repository.DosimetroRepository;
import com.dosimetros.backend.repository.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private DosimetroRepository dosimetroRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    void contextLoads() { }

    @Test
    void repositoriesConectan() {
        long totalDosimetros = dosimetroRepository.count();
        long totalEmpresas = empresaRepository.count();

        System.out.println("Dosímetros en BD: " + totalDosimetros);
        System.out.println("Empresas en BD: " + totalEmpresas);

        assertEquals(2, totalEmpresas);
    }
}