package com.mercadona.activo2incidencias.dto.asignacion;

import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AsignacionRequest {

    @NotNull(message = "El edificio es obligatorio")
    private Long edificioId;

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "El departamento es obligatorio")
    private Long departamentoId;

    @NotNull(message = "El responsable es obligatorio")
    private Long responsableId;

    private EstadoActivo estado = EstadoActivo.ACTIVO;

    private String observaciones;
}
