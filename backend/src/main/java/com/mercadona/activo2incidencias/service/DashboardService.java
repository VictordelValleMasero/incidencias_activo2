package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.domain.entity.Incidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp;
import com.mercadona.activo2incidencias.dto.dashboard.DashboardResponse;
import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaResumenResponse;
import com.mercadona.activo2incidencias.repository.DepartamentoRepository;
import com.mercadona.activo2incidencias.repository.EdificioRepository;
import com.mercadona.activo2incidencias.repository.IncidenciaRepository;
import com.mercadona.activo2incidencias.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final EdificioRepository edificioRepository;
    private final ZonaRepository zonaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final IncidenciaRepository incidenciaRepository;

    @Transactional(readOnly = true)
    public DashboardResponse obtenerResumen() {
        LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
        LocalDateTime hoyFin = hoyInicio.plusDays(1);

        List<Incidencia> ultimos30Dias = incidenciaRepository.findAll().stream()
                .filter(i -> i.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.toList());

        Map<String, Long> porZona = ultimos30Dias.stream()
                .collect(Collectors.groupingBy(i -> i.getZona().getNombre(), Collectors.counting()));

        List<IncidenciaResumenResponse> ultimasIncidencias = incidenciaRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::toResumen)
                .collect(Collectors.toList());

        List<DashboardResponse.ZonaConteoDto> zonasConMasIncidencias = porZona.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> DashboardResponse.ZonaConteoDto.builder().zona(e.getKey()).total(e.getValue()).build())
                .collect(Collectors.toList());

        long pendientes = incidenciaRepository.countByEstadoIn(
                List.of(EstadoIncidencia.NUEVA, EstadoIncidencia.EN_REVISION, EstadoIncidencia.EN_CURSO));

        return DashboardResponse.builder()
                .incidenciasHoy(incidenciaRepository.countByCreatedAtBetween(hoyInicio, hoyFin))
                .pendientes(pendientes)
                .notificadas(incidenciaRepository.countByEstado(EstadoIncidencia.NOTIFICADA))
                .errorWhatsapp(incidenciaRepository.countByEstado(EstadoIncidencia.ERROR_NOTIFICACION))
                .totalEdificios(edificioRepository.countByEstado(EstadoActivo.ACTIVO))
                .totalZonas(zonaRepository.countByEstado(EstadoActivo.ACTIVO))
                .totalDepartamentos(departamentoRepository.countByEstado(EstadoActivo.ACTIVO))
                .ultimasIncidencias(ultimasIncidencias)
                .zonasConMasIncidencias(zonasConMasIncidencias)
                .build();
    }

    private IncidenciaResumenResponse toResumen(Incidencia i) {
        String resumen = i.getDescripcion().length() > 80 ? i.getDescripcion().substring(0, 80) + "..." : i.getDescripcion();
        String responsable = null;
        if (i.getResponsable() != null) {
            String apellidos = i.getResponsable().getApellidos();
            responsable = apellidos == null || apellidos.isBlank()
                    ? i.getResponsable().getNombre()
                    : i.getResponsable().getNombre() + " " + apellidos;
        }
        return IncidenciaResumenResponse.builder()
                .id(i.getId())
                .codigo(i.getCodigo())
                .fecha(i.getCreatedAt().format(FECHA))
                .hora(i.getCreatedAt().format(HORA))
                .edificio(i.getEdificio().getNombre())
                .zona(i.getZona().getNombre())
                .departamento(i.getDepartamento().getNombre())
                .responsableNotificado(responsable)
                .estado(i.getEstado().name())
                .descripcionResumida(resumen)
                .tieneImagenes(!i.getImagenes().isEmpty())
                .whatsappEnviado(i.getWhatsappEstado() == EstadoNotificacionWhatsapp.ENVIADO)
                .whatsappError(i.getWhatsappError() != null)
                .build();
    }
}
