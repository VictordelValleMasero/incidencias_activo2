package com.mercadona.activo2incidencias.dto.edificio;

import com.mercadona.activo2incidencias.domain.entity.Edificio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EdificioPublicoResponse {
    private Long id;
    private String nombre;
    private String codigo;

    public static EdificioPublicoResponse from(Edificio e) {
        return EdificioPublicoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .codigo(e.getCodigo())
                .build();
    }
}
