package com.mercadona.activo2incidencias.dto.departamento;

import com.mercadona.activo2incidencias.domain.entity.Departamento;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DepartamentoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private EstadoActivo estado;
    private Long responsablePrincipalId;
    private String plantillaWhatsapp;

    public static DepartamentoResponse from(Departamento d) {
        return DepartamentoResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .estado(d.getEstado())
                .responsablePrincipalId(d.getResponsablePrincipal() != null ? d.getResponsablePrincipal().getId() : null)
                .plantillaWhatsapp(d.getPlantillaWhatsapp())
                .build();
    }
}
