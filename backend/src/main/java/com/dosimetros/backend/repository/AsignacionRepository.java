package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    List<Asignacion> findByDosimetroIdOrderByFechaAsignacionDesc(Integer dosimetroId);

    List<Asignacion> findByEjecutivoIdOrderByTrimestreDescFechaAsignacionDesc(Integer ejecutivoId);

    List<Asignacion> findByClienteIdOrderByFechaAsignacionDesc(Integer clienteId);

    // Vista ejecutivo con multifiltros (todos opcionales).
    @Query("""
        SELECT a FROM Asignacion a
        WHERE a.ejecutivo.id = :ejecutivoId
          AND (:clienteId IS NULL OR a.cliente.id = :clienteId)
          AND (:trimestre IS NULL OR a.trimestre = :trimestre)
          AND (:fecha IS NULL OR a.fechaAsignacion = :fecha)
          AND (:tipoPortaId IS NULL OR a.tipoPorta.id = :tipoPortaId)
          AND (:numeroBandeja IS NULL OR a.numeroBandeja = :numeroBandeja)
          AND (:slotBandeja IS NULL OR a.slotBandeja = :slotBandeja)
          AND (:link IS NULL OR LOWER(a.linkTrello) LIKE LOWER(CONCAT('%', :link, '%')))
        ORDER BY a.trimestre DESC, a.fechaAsignacion DESC
    """)
    List<Asignacion> filtrarPorEjecutivo(
            @Param("ejecutivoId") Integer ejecutivoId,
            @Param("clienteId") Integer clienteId,
            @Param("trimestre") String trimestre,
            @Param("fecha") LocalDate fecha,
            @Param("tipoPortaId") Integer tipoPortaId,
            @Param("numeroBandeja") Integer numeroBandeja,
            @Param("slotBandeja") Integer slotBandeja,
            @Param("link") String link
    );

    // Opciones de filtro acotadas a las asignaciones del ejecutivo
    @Query("""
        SELECT DISTINCT a.trimestre FROM Asignacion a WHERE a.ejecutivo.id = :ejecutivoId
        ORDER BY SUBSTRING(a.trimestre, 3, 4) DESC, SUBSTRING(a.trimestre, 1, 1) DESC
    """)
    List<String> trimestresDeEjecutivo(@Param("ejecutivoId") Integer ejecutivoId);

    @Query("""
        SELECT DISTINCT c.id, c.razonSocial FROM Asignacion a JOIN a.cliente c
        WHERE a.ejecutivo.id = :ejecutivoId ORDER BY c.razonSocial
    """)
    List<Object[]> clientesDeEjecutivo(@Param("ejecutivoId") Integer ejecutivoId);

    @Query("""
        SELECT DISTINCT tp.id, tp.nombre FROM Asignacion a JOIN a.tipoPorta tp
        WHERE a.ejecutivo.id = :ejecutivoId ORDER BY tp.nombre
    """)
    List<Object[]> portasDeEjecutivo(@Param("ejecutivoId") Integer ejecutivoId);

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

    // Formato del trimestre: 'QTYYYY' (ej. 2T2025). Se ordena por año y luego trimestre.
    @Query("""
        SELECT a.trimestre, COUNT(a) FROM Asignacion a
        GROUP BY a.trimestre
        ORDER BY SUBSTRING(a.trimestre, 3, 4) ASC, SUBSTRING(a.trimestre, 1, 1) ASC
    """)
    List<Object[]> contarPorTrimestre();

    @Query("""
        SELECT DISTINCT a.trimestre FROM Asignacion a
        ORDER BY SUBSTRING(a.trimestre, 3, 4) ASC, SUBSTRING(a.trimestre, 1, 1) ASC
    """)
    List<String> trimestresDistinct();

    // HU dashboard #2/#3: al filtrar por un trimestre, desglose de sus
    // asignaciones por MES (según la fecha de asignación), para que el gráfico
    // temporal muestre los meses de ese trimestre y no todos los trimestres.
    @Query("""
        SELECT MONTH(a.fechaAsignacion), COUNT(a) FROM Asignacion a
        WHERE a.trimestre = :trimestre
        GROUP BY MONTH(a.fechaAsignacion)
        ORDER BY MONTH(a.fechaAsignacion) ASC
    """)
    List<Object[]> contarPorMesEnTrimestre(@Param("trimestre") String trimestre);
}