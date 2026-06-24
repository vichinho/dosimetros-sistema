package com.dosimetros.backend.repository;

import com.dosimetros.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Usado por Spring Security para autenticar
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
