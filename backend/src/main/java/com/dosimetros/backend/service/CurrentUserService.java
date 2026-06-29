package com.dosimetros.backend.service;

import com.dosimetros.backend.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Resuelve datos del usuario autenticado a partir del SecurityContext.
 * El principal solo guarda username y rol, por lo que el ejecutivo asociado
 * se consulta a la base de datos cuando hace falta acotar resultados.
 */
@Service
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    public CurrentUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("No hay un usuario autenticado");
        }
        return auth.getName();
    }

    /**
     * Id del ejecutivo asociado al usuario autenticado. Falla si el usuario
     * (típicamente con rol EJECUTIVO) no tiene un ejecutivo vinculado.
     */
    public Integer requireEjecutivoId() {
        String username = getCurrentUsername();
        return usuarioRepository.findEjecutivoIdByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "Tu usuario no tiene un ejecutivo asociado; contacta a un administrador"));
    }
}
