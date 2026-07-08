package com.mercadona.activo2incidencias.dto.zona;

import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ZonaRequest {

    @NotNull(message = "El edificio es obligatorio")
    private Long edificioId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    private String descripcion;

    private String imagen;

    private EstadoActivo estado = EstadoActivo.ACTIVO;

    private int orden = 0;
}
