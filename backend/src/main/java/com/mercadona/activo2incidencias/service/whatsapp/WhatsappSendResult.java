package com.mercadona.activo2incidencias.service.whatsapp;

import com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WhatsappSendResult {
    private final EstadoNotificacionWhatsapp estado;
    private final String respuestaApi;
    private final String error;

    public static WhatsappSendResult ok(String respuestaApi) {
        return new WhatsappSendResult(EstadoNotificacionWhatsapp.ENVIADO, respuestaApi, null);
    }

    public static WhatsappSendResult error(EstadoNotificacionWhatsapp estado, String error) {
        return new WhatsappSendResult(estado, null, error);
    }
}
