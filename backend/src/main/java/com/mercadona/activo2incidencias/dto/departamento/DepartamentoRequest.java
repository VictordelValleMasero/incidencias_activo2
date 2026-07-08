package com.mercadona.activo2incidencias.dto.departamento;

import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DepartamentoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private EstadoActivo estado = EstadoActivo.ACTIVO;

    private Long responsablePrincipalId;

    private String plantillaWhatsapp;
}
