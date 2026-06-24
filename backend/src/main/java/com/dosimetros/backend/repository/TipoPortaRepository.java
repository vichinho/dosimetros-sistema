package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.TipoPorta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPortaRepository extends JpaRepository<TipoPorta, Integer> {

    // Compatibilidad: portas disponibles para un tipo de dosímetro
    List<TipoPorta> findByTipoDosimetroId(Integer tipoDosimetroId);
}
