package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Dosimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DosimetroRepository extends JpaRepository<Dosimetro, Integer> {

    Optional<Dosimetro> findByNumeroAndEstado(Integer numero, String estado);

    List<Dosimetro> findByEstadoOrderByNumeroAsc(String estado);

    List<Dosimetro> findByNumeroOrderByIdAsc(Integer numero);

    boolean existsByNumeroAndTipoDosimetroIdAndTipoPortaIdAndEstado(
            Integer numero,
            Integer tipoDosimetroId,
            Integer tipoPortaId,
            String estado
    );

    @Query("""
        SELECT d FROM Dosimetro d
        WHERE (:tipoDosimetroId IS NULL OR d.tipoDosimetro.id = :tipoDosimetroId)
          AND (:tipoPortaId IS NULL OR d.tipoPorta.id = :tipoPortaId)
          AND (:estado IS NULL OR d.estado = :estado)
        ORDER BY d.numero ASC
    """)
    List<Dosimetro> filtrar(
            @Param("tipoDosimetroId") Integer tipoDosimetroId,
            @Param("tipoPortaId") Integer tipoPortaId,
            @Param("estado") String estado
    );

    @Query("""
        SELECT d FROM Dosimetro d
        WHERE d.tarea.id = :tareaId
          AND d.numeroBandeja >= :bandejaDesde
          AND d.numeroBandeja <= :bandejaHasta
          AND (:slotDesde IS NULL OR d.slotBandeja >= :slotDesde)
          AND (:slotHasta IS NULL OR d.slotBandeja <= :slotHasta)
    """)
    List<Dosimetro> findByTareaYRangoBandejaSlot(
            @Param("tareaId") Integer tareaId,
            @Param("bandejaDesde") Integer bandejaDesde,
            @Param("bandejaHasta") Integer bandejaHasta,
            @Param("slotDesde") Integer slotDesde,
            @Param("slotHasta") Integer slotHasta
    );

    @Query("""
        SELECT d FROM Dosimetro d
        WHERE d.estado = 'disponible'
          AND d.tarea.id IN :tareaIds
          AND d.tipoDosimetro.id = :tipoDosimetroId
        ORDER BY d.tarea.id ASC, d.numeroBandeja ASC, d.slotBandeja ASC
    """)
    List<Dosimetro> findDisponiblesCompatiblesEnTareas(
            @Param("tareaIds") List<Integer> tareaIds,
            @Param("tipoDosimetroId") Integer tipoDosimetroId
    );

    // HU #9: dosímetros cuyo número físico se repite (más de un id con el mismo numero)
    @Query("""
        SELECT d FROM Dosimetro d
        WHERE d.numero IN (
            SELECT d2.numero FROM Dosimetro d2 GROUP BY d2.numero HAVING COUNT(d2) > 1
        )
        ORDER BY d.numero ASC, d.id ASC
    """)
    List<Dosimetro> findDuplicados();

    // HU #17: KPIs de stock
    @Query("SELECT d.estado, COUNT(d) FROM Dosimetro d GROUP BY d.estado ORDER BY d.estado")
    List<Object[]> contarPorEstado();

    @Query("""
        SELECT t.id, t.nombre, COUNT(d)
        FROM Dosimetro d JOIN d.tipoDosimetro t
        GROUP BY t.id, t.nombre
        ORDER BY t.nombre
    """)
    List<Object[]> contarPorTipoDosimetro();
}
