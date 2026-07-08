package com.mercadona.activo2incidencias.dto.departamento;

import com.mercadona.activo2incidencias.domain.entity.Departamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DepartamentoPublicoResponse {
    private Long id;
    private String nombre;

    public static DepartamentoPublicoResponse from(Departamento d) {
        return DepartamentoPublicoResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .build();
    }
}
