package com.mercadona.activo2incidencias.security;

import com.mercadona.activo2incidencias.domain.entity.Administrador;
import com.mercadona.activo2incidencias.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrador admin = administradorRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + email));

        return User.builder()
                .username(admin.getEmail())
                .password(admin.getPasswordHash())
                .disabled(!admin.isActivo())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRol().name())))
                .build();
    }
}
