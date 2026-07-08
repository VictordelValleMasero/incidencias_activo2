package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImagenDto {
    private Long id;
    private String url;
    private String nombreOriginal;
}
