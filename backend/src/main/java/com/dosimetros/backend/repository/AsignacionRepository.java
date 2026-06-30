package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    List<Asignacion> findByDosimetroIdOrderByFechaAsignacionDesc(Integer dosimetroId);

    List<Asignacion> findByEjecutivoIdOrderByTrimestreDescFechaAsignacionDesc(Integer ejecutivoId);

    // HU #17: KPIs de asignaciones (todos opcionalmente filtrados por trimestre)
    @Query("SELECT COUNT(a) FROM Asignacion a WHERE (:trimestre IS NULL OR a.trimestre = :trimestre)")
    long contarAsignaciones(@Param("trimestre") String trimestre);

    @Query("""
        SELECT e.id, e.nombre, COUNT(a)
        FROM Asignacion a JOIN a.empresa e
        WHERE (:trimestre IS NULL OR a.trimestre = :trimestre)
        GROUP BY e.id, e.nombre
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorEmpresa(@Param("trimestre") String trimestre);

    @Query("""
        SELECT ej.id, ej.nombre, COUNT(a)
        FROM Asignacion a JOIN a.ejecutivo ej
        WHERE (:trimestre IS NULL OR a.trimestre = :trimestre)
        GROUP BY ej.id, ej.nombre
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorEjecutivo(@Param("trimestre") String trimestre);

    @Query("""
        SELECT tp.id, tp.nombre, COUNT(a)
        FROM Asignacion a JOIN a.tipoPorta tp
        WHERE (:trimestre IS NULL OR a.trimestre = :trimestre)
        GROUP BY tp.id, tp.nombre
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorTipoPorta(@Param("trimestre") String trimestre);

    @Query("""
        SELECT c.id, c.razonSocial, COUNT(a)
        FROM Asignacion a JOIN a.cliente c
        WHERE (:trimestre IS NULL OR a.trimestre = :trimestre)
        GROUP BY c.id, c.razonSocial
        ORDER BY COUNT(a) DESC
    """)
    List<Object[]> contarPorCliente(@Param("trimestre") String trimestre);

    @Query("SELECT a.trimestre, COUNT(a) FROM Asignacion a GROUP BY a.trimestre ORDER BY a.trimestre")
    List<Object[]> contarPorTrimestre();

    @Query("SELECT DISTINCT a.trimestre FROM Asignacion a ORDER BY a.trimestre")
    List<String> trimestresDistinct();
}