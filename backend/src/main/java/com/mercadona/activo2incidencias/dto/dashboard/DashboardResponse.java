package com.mercadona.activo2incidencias.dto.dashboard;

import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaResumenResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DashboardResponse {
    private long incidenciasHoy;
    private long pendientes;
    private long notificadas;
    private long errorWhatsapp;
    private long totalEdificios;
    private long totalZonas;
    private long totalDepartamentos;
    private List<IncidenciaResumenResponse> ultimasIncidencias;
    private List<ZonaConteoDto> zonasConMasIncidencias;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ZonaConteoDto {
        private String zona;
        private long total;
    }
}
