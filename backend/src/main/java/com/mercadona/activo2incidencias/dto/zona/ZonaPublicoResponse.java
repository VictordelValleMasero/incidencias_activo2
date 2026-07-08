package com.mercadona.activo2incidencias.dto.zona;

import com.mercadona.activo2incidencias.domain.entity.Zona;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ZonaPublicoResponse {
    private Long id;
    private String nombre;
    private String codigo;

    public static ZonaPublicoResponse from(Zona z) {
        return ZonaPublicoResponse.builder()
                .id(z.getId())
                .nombre(z.getNombre())
                .codigo(z.getCodigo())
                .build();
    }
}
