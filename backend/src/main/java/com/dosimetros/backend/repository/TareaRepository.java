package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    List<Tarea> findAllByOrderByFechaCreacionDesc();

    Optional<Tarea> findByNumeroTarea(String numeroTarea);
}