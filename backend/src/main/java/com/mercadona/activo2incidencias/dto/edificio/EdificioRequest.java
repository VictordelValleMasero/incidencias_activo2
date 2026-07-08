package com.mercadona.activo2incidencias.dto.edificio;

import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EdificioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    private String descripcion;

    private EstadoActivo estado = EstadoActivo.ACTIVO;

    private int orden = 0;
}
