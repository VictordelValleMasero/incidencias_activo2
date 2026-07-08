package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IncidenciaResumenResponse {
    private Long id;
    private String codigo;
    private String fecha;
    private String hora;
    private String edificio;
    private String zona;
    private String departamento;
    private String responsableNotificado;
    private String estado;
    private String descripcionResumida;
    private boolean tieneImagenes;
    private boolean whatsappEnviado;
    private boolean whatsappError;
}
