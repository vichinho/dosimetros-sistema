package com.dosimetros.backend.service;

import com.dosimetros.backend.dto.auth.LoginRequest;
import com.dosimetros.backend.dto.auth.LoginResponse;
import com.dosimetros.backend.entity.Usuario;
import com.dosimetros.backend.repository.UsuarioRepository;
import com.dosimetros.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String username = principal.getUsername();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rol = usuario.getRol().getNombre();
        String token = jwtUtil.generateToken(username, rol);

        return new LoginResponse(token, username, rol);
    }
}