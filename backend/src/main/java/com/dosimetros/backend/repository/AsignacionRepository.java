package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    List<Asignacion> findByDosimetroIdOrderByFechaAsignacionDesc(Integer dosimetroId);

    List<Asignacion> findByEjecutivoIdOrderByTrimestreDescFechaAsignacionDesc(Integer ejecutivoId);
}