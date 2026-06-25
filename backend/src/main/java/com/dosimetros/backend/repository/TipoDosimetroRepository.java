package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.TipoDosimetro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoDosimetroRepository extends JpaRepository<TipoDosimetro, Integer> {

    List<TipoDosimetro> findAllByOrderByNombreAsc();

    Optional<TipoDosimetro> findByNombre(String nombre);
}