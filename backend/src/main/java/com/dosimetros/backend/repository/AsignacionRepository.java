package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {

    // HU-14: historial completo de un dosímetro
    List<Asignacion> findByDosimetroIdOrderByFechaAsignacionDesc(Integer dosimetroId);

    // HU-3: asignaciones de los clientes de un ejecutivo
    List<Asignacion> findByEjecutivoId(Integer ejecutivoId);

    // HU-15: dosímetros de un cliente agrupados por trimestre
    List<Asignacion> findByClienteIdOrderByTrimestre(Integer clienteId);

    // HU-16: clientes pendientes de envío (sin link de Trello)
    @Query("SELECT a FROM Asignacion a WHERE a.ejecutivo.id = :ejecutivoId AND a.linkTrello IS NULL")
    List<Asignacion> findPendientesEnvioByEjecutivo(@Param("ejecutivoId") Integer ejecutivoId);

    // HU-17: KPI total asignados por empresa
    @Query("SELECT a.empresa.nombre, COUNT(a) FROM Asignacion a GROUP BY a.empresa.nombre")
    List<Object[]> countAsignadosByEmpresa();
}
