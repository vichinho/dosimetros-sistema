package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Ejecutivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjecutivoRepository extends JpaRepository<Ejecutivo, Integer> {

    List<Ejecutivo> findByActivoTrue();

    // Coincidencia exacta por nombre (carga de asignaciones por archivo, #12)
    List<Ejecutivo> findByNombreIgnoreCaseAndActivoTrue(String nombre);
}