package com.dosimetros.backend.security;

import com.dosimetros.backend.entity.Usuario;
import com.dosimetros.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()))
        );
    }
}
