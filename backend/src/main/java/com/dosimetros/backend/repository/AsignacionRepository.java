package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    List<Asignacion> findByDosimetroIdOrderByFechaAsignacionDesc(Integer dosimetroId);

    List<Asignacion> findByEjecutivoIdOrderByTrimestreDescFechaAsignacionDesc(Integer ejecutivoId);

    // HU #17: KPIs de asignaciones
    @Query("""
        SELECT e.id, e.nombre, COUNT(a)
        FROM Asignacion a JOIN a.empresa e
        GROUP BY e.id, e.nombre
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorEmpresa();

    @Query("""
        SELECT ej.id, ej.nombre, COUNT(a)
        FROM Asignacion a JOIN a.ejecutivo ej
        GROUP BY ej.id, ej.nombre
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorEjecutivo();

    @Query("SELECT a.trimestre, COUNT(a) FROM Asignacion a GROUP BY a.trimestre ORDER BY a.trimestre")
    List<Object[]> contarPorTrimestre();
}