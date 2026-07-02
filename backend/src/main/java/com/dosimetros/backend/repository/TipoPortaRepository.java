package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.TipoPorta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoPortaRepository extends JpaRepository<TipoPorta, Integer> {

    List<TipoPorta> findAllByOrderByNombreAsc();

    List<TipoPorta> findByTipoDosimetroIdOrderByNombreAsc(Integer tipoDosimetroId);

    Optional<TipoPorta> findByNombreAndTipoDosimetroId(String nombre, Integer tipoDosimetroId);
}