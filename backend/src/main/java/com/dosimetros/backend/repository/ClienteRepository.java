package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByActivoTrue();

    // Búsqueda por nombre para el buscador del frontend
    List<Cliente> findByRazonSocialContainingIgnoreCaseAndActivoTrue(String razonSocial);

    // Clientes que tienen al menos una asignación con el ejecutivo dado
    @Query("""
        SELECT DISTINCT a.cliente FROM Asignacion a
        WHERE a.ejecutivo.id = :ejecutivoId
        ORDER BY a.cliente.razonSocial ASC
    """)
    List<Cliente> findDistinctClientesByEjecutivoId(@Param("ejecutivoId") Integer ejecutivoId);
}
