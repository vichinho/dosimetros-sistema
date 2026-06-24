package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {

    Optional<Tarea> findByNumeroTarea(String numeroTarea);
}
