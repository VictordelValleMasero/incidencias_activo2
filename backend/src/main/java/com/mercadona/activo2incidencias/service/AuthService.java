package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.InvalidCredentialsException;
import com.mercadona.activo2incidencias.domain.entity.Administrador;
import com.mercadona.activo2incidencias.dto.auth.AdminResponse;
import com.mercadona.activo2incidencias.dto.auth.LoginRequest;
import com.mercadona.activo2incidencias.dto.auth.LoginResponse;
import com.mercadona.activo2incidencias.repository.AdministradorRepository;
import com.mercadona.activo2incidencias.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditoriaService auditoriaService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Administrador admin = administradorRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email o contrasena incorrectos"));

        if (!admin.isActivo() || !passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new InvalidCredentialsException("Email o contrasena incorrectos");
        }

        String token = jwtService.generarToken(admin.getEmail(), admin.getId(), admin.getRol().name());
        auditoriaService.registrar("LOGIN", "Administrador", admin.getId(), "Inicio de sesion correcto");

        return LoginResponse.builder()
                .token(token)
                .admin(AdminResponse.from(admin))
                .build();
    }
}
