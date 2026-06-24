package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    List<Empresa> findByActivaTrue();
}
