package com.mercadona.activo2incidencias.dto.asignacion;

import com.mercadona.activo2incidencias.domain.entity.ZonaDepartamentoResponsable;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AsignacionResponse {
    private Long id;
    private Long edificioId;
    private Long zonaId;
    private Long departamentoId;
    private Long responsableId;
    private EstadoActivo estado;
    private String observaciones;

    public static AsignacionResponse from(ZonaDepartamentoResponsable a) {
        return AsignacionResponse.builder()
                .id(a.getId())
                .edificioId(a.getEdificio().getId())
                .zonaId(a.getZona().getId())
                .departamentoId(a.getDepartamento().getId())
                .responsableId(a.getResponsable().getId())
                .estado(a.getEstado())
                .observaciones(a.getObservaciones())
                .build();
    }
}
