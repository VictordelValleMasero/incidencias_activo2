package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IncidenciaReportResponse {
    private String codigo;
    private String mensaje;
    private boolean responsableNotificado;
}
