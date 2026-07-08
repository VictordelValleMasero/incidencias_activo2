package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class IncidenciaDetalleResponse {
    private Long id;
    private String codigo;
    private String edificio;
    private String zona;
    private String departamento;
    private String responsableNotificado;
    private String telefonoNotificado;
    private String descripcion;
    private List<ImagenDto> imagenes;
    private String fecha;
    private String hora;
    private String estado;
    private List<HistorialItemDto> historial;
    private String whatsappEstado;
    private String mensajeWhatsapp;
    private String whatsappError;
    private String observacionesInternas;
}
