package com.mercadona.activo2incidencias.service.whatsapp;

import com.mercadona.activo2incidencias.config.AppUrlProperties;
import com.mercadona.activo2incidencias.domain.entity.Incidencia;
import com.mercadona.activo2incidencias.domain.entity.Responsable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WhatsappNotificationService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final String PLANTILLA_POR_DEFECTO =
            "Nueva incidencia registrada\n\n" +
            "Edificio: {{edificio}}\n" +
            "Zona: {{zona}}\n" +
            "Departamento: {{departamento}}\n" +
            "Fecha: {{fecha}}\n" +
            "Hora: {{hora}}\n\n" +
            "Descripcion:\n{{descripcion}}\n\n" +
            "Adjuntos:\n{{numero_imagenes}} imagen/es\n\n" +
            "ID incidencia: {{id_incidencia}}\n" +
            "Consultar incidencia:\n{{url_incidencia}}";

    private final WhatsappProvider whatsappProvider;
    private final AppUrlProperties appUrlProperties;

    /**
     * Construye el mensaje final (aplicando la plantilla del departamento si
     * existe, o la plantilla por defecto) y lo envia al responsable. No
     * persiste nada: el llamador decide como guardar el resultado.
     */
    public WhatsappSendResult notificar(Incidencia incidencia, Responsable responsable) {
        String mensaje = construirMensaje(incidencia, responsable);
        if (responsable == null) {
            return WhatsappSendResult.error(
                    com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp.ERROR,
                    "La zona no tiene un responsable asignado para este departamento");
        }
        return whatsappProvider.enviar(responsable.getTelefonoWhatsapp(), mensaje);
    }

    public String construirMensaje(Incidencia incidencia, Responsable responsable) {
        String plantilla = incidencia.getDepartamento().getPlantillaWhatsapp();
        if (plantilla == null || plantilla.isBlank()) {
            plantilla = PLANTILLA_POR_DEFECTO;
        }

        String urlIncidencia = appUrlProperties.getPublicUrl() + "/admin/incidencias/" + incidencia.getId();

        return plantilla
                .replace("{{edificio}}", incidencia.getEdificio().getNombre())
                .replace("{{zona}}", incidencia.getZona().getNombre())
                .replace("{{departamento}}", incidencia.getDepartamento().getNombre())
                .replace("{{responsable}}", responsable != null ? responsable.getNombre() : "")
                .replace("{{fecha}}", incidencia.getCreatedAt().format(FECHA))
                .replace("{{hora}}", incidencia.getCreatedAt().format(HORA))
                .replace("{{descripcion}}", incidencia.getDescripcion())
                .replace("{{numero_imagenes}}", String.valueOf(incidencia.getImagenes().size()))
                .replace("{{id_incidencia}}", incidencia.getCodigo())
                .replace("{{url_incidencia}}", urlIncidencia);
    }
}
