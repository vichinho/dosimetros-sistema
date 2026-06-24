package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Ejecutivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjecutivoRepository extends JpaRepository<Ejecutivo, Integer> {

    List<Ejecutivo> findByActivoTrue();
    List<Ejecutivo> findByEmpresaIdAndActivoTrue(Integer empresaId);
}
