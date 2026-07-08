package com.mercadona.activo2incidencias.dto.zona;

import com.mercadona.activo2incidencias.domain.entity.Zona;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ZonaResponse {
    private Long id;
    private Long edificioId;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String imagen;
    private EstadoActivo estado;
    private int orden;

    public static ZonaResponse from(Zona z) {
        return ZonaResponse.builder()
                .id(z.getId())
                .edificioId(z.getEdificio().getId())
                .nombre(z.getNombre())
                .codigo(z.getCodigo())
                .descripcion(z.getDescripcion())
                .imagen(z.getImagen())
                .estado(z.getEstado())
                .orden(z.getOrden())
                .build();
    }
}
