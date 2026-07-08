package com.mercadona.activo2incidencias.dto.responsable;

import com.mercadona.activo2incidencias.domain.entity.Responsable;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponsableResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String cargo;
    private Long departamentoId;
    private String telefonoWhatsapp;
    private String email;
    private String horario;
    private EstadoActivo estado;

    public static ResponsableResponse from(Responsable r) {
        return ResponsableResponse.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .apellidos(r.getApellidos())
                .cargo(r.getCargo())
                .departamentoId(r.getDepartamento().getId())
                .telefonoWhatsapp(r.getTelefonoWhatsapp())
                .email(r.getEmail())
                .horario(r.getHorario())
                .estado(r.getEstado())
                .build();
    }
}
