package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.BusinessException;
import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.config.IncidenciasProperties;
import com.mercadona.activo2incidencias.domain.entity.*;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp;
import com.mercadona.activo2incidencias.dto.departamento.DepartamentoPublicoResponse;
import com.mercadona.activo2incidencias.dto.edificio.EdificioPublicoResponse;
import com.mercadona.activo2incidencias.dto.incidencia.*;
import com.mercadona.activo2incidencias.dto.zona.ZonaPublicoResponse;
import com.mercadona.activo2incidencias.repository.*;
import com.mercadona.activo2incidencias.service.whatsapp.WhatsappNotificationService;
import com.mercadona.activo2incidencias.service.whatsapp.WhatsappSendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidenciaService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final EdificioRepository edificioRepository;
    private final ZonaRepository zonaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ZonaDepartamentoResponsableRepository asignacionRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final ImagenIncidenciaRepository imagenIncidenciaRepository;
    private final IncidenciaHistorialRepository incidenciaHistorialRepository;
    private final FileStorageService fileStorageService;
    private final WhatsappNotificationService whatsappNotificationService;
    private final AuditoriaService auditoriaService;
    private final IncidenciasProperties incidenciasProperties;

    // ---------------------------------------------------------------
    // Flujo publico: Incidencias -> Edificio -> Zona -> Departamento
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<EdificioPublicoResponse> listarEdificios() {
        return edificioRepository.findByEstadoOrderByOrdenAsc(EstadoActivo.ACTIVO).stream()
                .map(EdificioPublicoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ZonaPublicoResponse> listarZonas(Long edificioId) {
        if (!edificioRepository.existsById(edificioId)) {
            throw new ResourceNotFoundException("Edificio no encontrado");
        }
        return zonaRepository.findByEdificioIdAndEstadoOrderByOrdenAsc(edificioId, EstadoActivo.ACTIVO).stream()
                .map(ZonaPublicoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartamentoPublicoResponse> listarDepartamentos(Long zonaId) {
        if (!zonaRepository.existsById(zonaId)) {
            throw new ResourceNotFoundException("Zona no encontrada");
        }
        return asignacionRepository.findByZonaIdAndEstado(zonaId, EstadoActivo.ACTIVO).stream()
                .map(ZonaDepartamentoResponsable::getDepartamento)
                .filter(d -> d.getEstado() == EstadoActivo.ACTIVO)
                .collect(Collectors.toMap(Departamento::getId, d -> d, (a, b) -> a, LinkedHashMap::new))
                .values().stream()
                .sorted(Comparator.comparing(Departamento::getNombre))
                .map(DepartamentoPublicoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public IncidenciaReportResponse reportar(IncidenciaReportRequest request, List<MultipartFile> imagenes) {
        Edificio edificio = edificioRepository.findById(request.getEdificioId())
                .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
        if (edificio.getEstado() != EstadoActivo.ACTIVO) {
            throw new BusinessException("Este edificio no esta disponible actualmente");
        }

        Zona zona = zonaRepository.findById(request.getZonaId())
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));
        if (zona.getEstado() != EstadoActivo.ACTIVO) {
            throw new BusinessException("Esta zona no esta disponible actualmente");
        }
        if (!zona.getEdificio().getId().equals(edificio.getId())) {
            throw new BusinessException("La zona seleccionada no pertenece al edificio indicado");
        }

        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
        if (departamento.getEstado() != EstadoActivo.ACTIVO) {
            throw new BusinessException("Este departamento no esta disponible actualmente");
        }

        ZonaDepartamentoResponsable asignacion = asignacionRepository
                .findFirstByZonaIdAndDepartamentoIdAndEstado(zona.getId(), departamento.getId(), EstadoActivo.ACTIVO)
                .orElseThrow(() -> new BusinessException("El departamento seleccionado no esta disponible para esta zona"));

        if (imagenes != null && imagenes.size() > incidenciasProperties.getMaxImagenes()) {
            throw new BusinessException("Se permiten como maximo " + incidenciasProperties.getMaxImagenes() + " imagenes");
        }

        Responsable responsable = asignacion.getResponsable();

        Incidencia incidencia = Incidencia.builder()
                .codigo("TMP-" + System.nanoTime())
                .edificio(edificio)
                .zona(zona)
                .departamento(departamento)
                .responsable(responsable)
                .descripcion(request.getDescripcion())
                .estado(EstadoIncidencia.NUEVA)
                .whatsappEstado(EstadoNotificacionWhatsapp.PENDIENTE)
                .build();
        incidencia = incidenciaRepository.save(incidencia);
        incidencia.setCodigo(String.format("INC-%06d", incidencia.getId()));

        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file == null || file.isEmpty()) continue;
                String ruta = fileStorageService.guardarImagenIncidencia(file, incidencia.getId());
                ImagenIncidencia imagen = ImagenIncidencia.builder()
                        .incidencia(incidencia)
                        .nombreOriginal(file.getOriginalFilename())
                        .nombreArchivo(ruta)
                        .mimeType(file.getContentType())
                        .size(file.getSize())
                        .url("")
                        .build();
                incidencia.getImagenes().add(imagen);
            }
        }
        incidencia = incidenciaRepository.save(incidencia);
        for (ImagenIncidencia imagen : incidencia.getImagenes()) {
            imagen.setUrl("/api/admin/images/" + imagen.getId());
        }

        registrarHistorial(incidencia, null, EstadoIncidencia.NUEVA, "Incidencia creada por el usuario", "publico");

        String mensaje = whatsappNotificationService.construirMensaje(incidencia, responsable);
        incidencia.setMensajeWhatsapp(mensaje);
        WhatsappSendResult resultado = whatsappNotificationService.notificar(incidencia, responsable);

        incidencia.setWhatsappEstado(resultado.getEstado());
        boolean notificado = resultado.getEstado() == EstadoNotificacionWhatsapp.ENVIADO;
        if (notificado) {
            incidencia.setWhatsappError(null);
            incidencia.setEstado(EstadoIncidencia.NOTIFICADA);
        } else {
            incidencia.setWhatsappError(resultado.getError());
            incidencia.setEstado(EstadoIncidencia.ERROR_NOTIFICACION);
        }
        registrarHistorial(incidencia, EstadoIncidencia.NUEVA, incidencia.getEstado(),
                "Resultado del envio de WhatsApp: " + incidencia.getWhatsappEstado(), "publico");

        incidencia = incidenciaRepository.save(incidencia);
        auditoriaService.registrar("CREAR", "Incidencia", incidencia.getId(), incidencia.getCodigo());

        return IncidenciaReportResponse.builder()
                .codigo(incidencia.getCodigo())
                .mensaje("Incidencia enviada correctamente")
                .responsableNotificado(notificado)
                .build();
    }

    // ---------------------------------------------------------------
    // Administracion de incidencias
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<IncidenciaResumenResponse> listarAdmin(LocalDate fecha, Long edificioId, Long zonaId, Long departamentoId,
                                                         EstadoIncidencia estado, Boolean whatsappError, String texto,
                                                         Pageable pageable) {
        var spec = IncidenciaSpecifications.conFiltros(fecha, edificioId, zonaId, departamentoId, estado, whatsappError, texto);
        return incidenciaRepository.findAll(spec, pageable).map(this::toResumen);
    }

    @Transactional(readOnly = true)
    public IncidenciaDetalleResponse obtenerDetalle(Long id) {
        return toDetalle(obtener(id));
    }

    @Transactional
    public IncidenciaDetalleResponse cambiarEstado(Long id, EstadoIncidencia nuevoEstado, String observaciones) {
        Incidencia incidencia = obtener(id);
        EstadoIncidencia anterior = incidencia.getEstado();
        incidencia.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoIncidencia.CERRADA || nuevoEstado == EstadoIncidencia.RESUELTA
                || nuevoEstado == EstadoIncidencia.DESCARTADA) {
            incidencia.setClosedAt(LocalDateTime.now());
        }
        if (observaciones != null && !observaciones.isBlank()) {
            incidencia.setObservacionesInternas(observaciones);
        }
        registrarHistorial(incidencia, anterior, nuevoEstado, observaciones, actorActual());
        incidenciaRepository.save(incidencia);
        auditoriaService.registrar("CAMBIAR_ESTADO", "Incidencia", id, anterior + " -> " + nuevoEstado);
        return toDetalle(incidencia);
    }

    @Transactional
    public IncidenciaDetalleResponse reenviarWhatsapp(Long id) {
        Incidencia incidencia = obtener(id);
        if (incidencia.getResponsable() == null) {
            throw new BusinessException("Esta incidencia no tiene un responsable asignado");
        }

        String mensaje = whatsappNotificationService.construirMensaje(incidencia, incidencia.getResponsable());
        incidencia.setMensajeWhatsapp(mensaje);
        WhatsappSendResult resultado = whatsappNotificationService.notificar(incidencia, incidencia.getResponsable());

        incidencia.setWhatsappEstado(resultado.getEstado());
        if (resultado.getEstado() == EstadoNotificacionWhatsapp.ENVIADO) {
            incidencia.setWhatsappError(null);
            if (incidencia.getEstado() == EstadoIncidencia.ERROR_NOTIFICACION) {
                incidencia.setEstado(EstadoIncidencia.NOTIFICADA);
            }
        } else {
            incidencia.setWhatsappError(resultado.getError());
        }
        registrarHistorial(incidencia, incidencia.getEstado(), incidencia.getEstado(),
                "Reenvio manual de WhatsApp: " + resultado.getEstado(), actorActual());
        incidenciaRepository.save(incidencia);
        auditoriaService.registrar("REENVIAR_WHATSAPP", "Incidencia", id, resultado.getEstado().name());
        return toDetalle(incidencia);
    }

    private String actorActual() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "publico";
    }

    private void registrarHistorial(Incidencia incidencia, EstadoIncidencia anterior, EstadoIncidencia nuevo, String comentario, String actor) {
        IncidenciaHistorial historial = IncidenciaHistorial.builder()
                .incidencia(incidencia)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .comentario(comentario)
                .actor(actor)
                .build();
        incidenciaHistorialRepository.save(historial);
    }

    private Incidencia obtener(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada"));
    }

    private String nombreCompleto(Responsable responsable) {
        if (responsable == null) return null;
        String apellidos = responsable.getApellidos();
        return apellidos == null || apellidos.isBlank() ? responsable.getNombre() : responsable.getNombre() + " " + apellidos;
    }

    private IncidenciaResumenResponse toResumen(Incidencia i) {
        String resumen = i.getDescripcion().length() > 120 ? i.getDescripcion().substring(0, 120) + "..." : i.getDescripcion();
        return IncidenciaResumenResponse.builder()
                .id(i.getId())
                .codigo(i.getCodigo())
                .fecha(i.getCreatedAt().format(FECHA))
                .hora(i.getCreatedAt().format(HORA))
                .edificio(i.getEdificio().getNombre())
                .zona(i.getZona().getNombre())
                .departamento(i.getDepartamento().getNombre())
                .responsableNotificado(nombreCompleto(i.getResponsable()))
                .estado(i.getEstado().name())
                .descripcionResumida(resumen)
                .tieneImagenes(!i.getImagenes().isEmpty())
                .whatsappEnviado(i.getWhatsappEstado() == EstadoNotificacionWhatsapp.ENVIADO)
                .whatsappError(i.getWhatsappError() != null)
                .build();
    }

    private IncidenciaDetalleResponse toDetalle(Incidencia i) {
        List<ImagenDto> imagenes = i.getImagenes().stream()
                .map(img -> ImagenDto.builder()
                        .id(img.getId())
                        .url("/api/admin/images/" + img.getId())
                        .nombreOriginal(img.getNombreOriginal())
                        .build())
                .collect(Collectors.toList());

        List<IncidenciaHistorial> historial = incidenciaHistorialRepository.findByIncidenciaIdOrderByFechaDesc(i.getId());
        List<HistorialItemDto> historialItems = historial.stream()
                .map(h -> HistorialItemDto.builder()
                        .estadoAnterior(h.getEstadoAnterior() != null ? h.getEstadoAnterior().name() : null)
                        .estadoNuevo(h.getEstadoNuevo().name())
                        .comentario(h.getComentario())
                        .actor(h.getActor())
                        .fecha(h.getFecha())
                        .build())
                .collect(Collectors.toList());

        return IncidenciaDetalleResponse.builder()
                .id(i.getId())
                .codigo(i.getCodigo())
                .edificio(i.getEdificio().getNombre())
                .zona(i.getZona().getNombre())
                .departamento(i.getDepartamento().getNombre())
                .responsableNotificado(nombreCompleto(i.getResponsable()))
                .telefonoNotificado(i.getResponsable() != null ? i.getResponsable().getTelefonoWhatsapp() : null)
                .descripcion(i.getDescripcion())
                .imagenes(imagenes)
                .fecha(i.getCreatedAt().format(FECHA))
                .hora(i.getCreatedAt().format(HORA))
                .estado(i.getEstado().name())
                .historial(historialItems)
                .whatsappEstado(i.getWhatsappEstado().name())
                .mensajeWhatsapp(i.getMensajeWhatsapp())
                .whatsappError(i.getWhatsappError())
                .observacionesInternas(i.getObservacionesInternas())
                .build();
    }
}
