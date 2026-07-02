package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    List<Empresa> findByActivaTrue();
}