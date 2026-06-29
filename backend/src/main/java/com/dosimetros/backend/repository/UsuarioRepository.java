package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByActivoTrue();

    boolean existsByUsername(String username);

    @Query("SELECT u.ejecutivo.id FROM Usuario u WHERE u.username = :username")
    Optional<Integer> findEjecutivoIdByUsername(@Param("username") String username);
}