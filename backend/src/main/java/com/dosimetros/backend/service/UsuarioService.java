package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.usuario.UsuarioRequest;
import com.dosimetros.backend.dto.usuario.UsuarioResponse;
import com.dosimetros.backend.entity.Ejecutivo;
import com.dosimetros.backend.entity.Rol;
import com.dosimetros.backend.entity.Usuario;
import com.dosimetros.backend.exception.ResourceNotFoundException;
import com.dosimetros.backend.repository.EjecutivoRepository;
import com.dosimetros.backend.repository.RolRepository;
import com.dosimetros.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EjecutivoRepository ejecutivoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          EjecutivoRepository ejecutivoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.ejecutivoRepository = ejecutivoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listarActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return toResponse(usuario);
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username '" + request.getUsername() + "' ya está en uso");
        }

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + request.getRolId()));

        Ejecutivo ejecutivo = null;
        if ("EJECUTIVO".equals(rol.getNombre())) {
            if (request.getEjecutivoId() == null) {
                throw new IllegalArgumentException("El ejecutivoId es obligatorio para usuarios con rol EJECUTIVO");
            }
            ejecutivo = ejecutivoRepository.findById(request.getEjecutivoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ejecutivo no encontrado con id: " + request.getEjecutivoId()));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setEjecutivo(ejecutivo);
        usuario.setActivo(true);

        return toResponse(usuarioRepository.save(usuario));
    }

    public void desactivar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getRol().getNombre(),
                u.getEjecutivo() != null ? u.getEjecutivo().getId() : null,
                u.getEjecutivo() != null ? u.getEjecutivo().getNombre() : null,
                u.getActivo()
        );
    }
}
