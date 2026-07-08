package com.mercadona.activo2incidencias.dto.auth;

import com.mercadona.activo2incidencias.domain.entity.Administrador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminResponse {
    private Long id;
    private String nombre;
    private String email;

    public static AdminResponse from(Administrador admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .nombre(admin.getNombre())
                .email(admin.getEmail())
                .build();
    }
}
