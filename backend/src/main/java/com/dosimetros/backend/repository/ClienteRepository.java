package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByActivoTrue();

    // Búsqueda por nombre para el buscador del frontend
    List<Cliente> findByRazonSocialContainingIgnoreCaseAndActivoTrue(String razonSocial);
}
