package com.dosimetros.backend.service;

import com.dosimetros.backend.entity.Empresa;
import com.dosimetros.backend.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public List<Empresa> listarActivas() {
        return repository.findByActivaTrue();
    }
}