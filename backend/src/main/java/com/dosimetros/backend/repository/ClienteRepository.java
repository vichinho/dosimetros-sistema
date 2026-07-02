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

    // Clientes cuyo ejecutivo responsable es el indicado (HU #3 / #16)
    List<Cliente> findByEjecutivoIdAndActivoTrueOrderByRazonSocialAsc(Integer ejecutivoId);

    // Ids de clientes que actualmente son el último destino de algún dosímetro
    // en estado 'asignado' (es decir, tienen dosímetros vigentes).
    @Query("""
        SELECT DISTINCT a.cliente.id FROM Asignacion a
        WHERE a.dosimetro.estado = 'asignado'
          AND a.id = (SELECT MAX(a2.id) FROM Asignacion a2 WHERE a2.dosimetro.id = a.dosimetro.id)
    """)
    List<Integer> findIdsClientesConDosimetroVigente();
}
