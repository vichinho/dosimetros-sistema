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
          AND (:estado IS NULL OR d.estado = :estado)
        ORDER BY d.numero ASC
    """)
    List<Dosimetro> filtrar(
            @Param("tipoDosimetroId") Integer tipoDosimetroId,
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
}
