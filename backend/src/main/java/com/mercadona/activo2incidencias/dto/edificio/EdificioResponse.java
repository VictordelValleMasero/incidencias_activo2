package com.mercadona.activo2incidencias.dto.edificio;

import com.mercadona.activo2incidencias.domain.entity.Edificio;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EdificioResponse {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private EstadoActivo estado;
    private int orden;

    public static EdificioResponse from(Edificio e) {
        return EdificioResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .codigo(e.getCodigo())
                .descripcion(e.getDescripcion())
                .estado(e.getEstado())
                .orden(e.getOrden())
                .build();
    }
}
