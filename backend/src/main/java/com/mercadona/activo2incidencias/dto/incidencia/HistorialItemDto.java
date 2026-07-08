package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class HistorialItemDto {
    private String estadoAnterior;
    private String estadoNuevo;
    private String comentario;
    private String actor;
    private LocalDateTime fecha;
}
