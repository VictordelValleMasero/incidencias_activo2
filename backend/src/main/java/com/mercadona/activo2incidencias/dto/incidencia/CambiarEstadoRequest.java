package com.mercadona.activo2incidencias.dto.incidencia;

import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoIncidencia estado;

    private String observaciones;
}
