package com.mercadona.activo2incidencias.service.whatsapp;

import com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Proveedor simulado de WhatsApp para desarrollo y demo.
 * No envia mensajes reales: registra el intento en el log y devuelve un
 * resultado realista (incluyendo errores simulados para numeros con formato
 * invalido), de forma que todo el flujo de negocio (persistencia, reintentos,
 * panel de administracion) pueda probarse de extremo a extremo sin
 * credenciales de Meta / Twilio / etc.
 *
 * Para produccion: activar app.whatsapp.proveedor=meta-cloud-api (o el que
 * corresponda) e implementar el @Component correspondiente, por ejemplo
 * MetaCloudApiWhatsappProvider, con las credenciales reales de WhatsApp
 * Business API / Meta Cloud API (o Twilio, 360dialog, MessageBird, Vonage).
 */
@Component
@ConditionalOnProperty(prefix = "app.whatsapp", name = "proveedor", havingValue = "mock", matchIfMissing = true)
public class MockWhatsappProvider implements WhatsappProvider {

    private static final Logger log = LoggerFactory.getLogger(MockWhatsappProvider.class);
    private static final Pattern E164 = Pattern.compile("^\\+[1-9][0-9]{7,14}$");

    @Value("${app.whatsapp.numero-emisor:+34600000000}")
    private String numeroEmisor;

    @Override
    public WhatsappSendResult enviar(String telefonoE164, String mensaje) {
        if (telefonoE164 == null || !E164.matcher(telefonoE164).matches()) {
            log.warn("[WhatsApp MOCK] Numero invalido: {}", telefonoE164);
            return WhatsappSendResult.error(EstadoNotificacionWhatsapp.NUMERO_INVALIDO,
                    "El numero de telefono no tiene un formato valido (E.164)");
        }

        log.info("[WhatsApp MOCK] Emisor {} -> Destinatario {}\n{}", numeroEmisor, telefonoE164, mensaje);

        String respuestaSimulada = "{\"messaging_product\":\"whatsapp\",\"contacts\":[{\"wa_id\":\""
                + telefonoE164.replace("+", "") + "\"}],\"messages\":[{\"id\":\"mock-" + System.currentTimeMillis() + "\"}]}";
        return WhatsappSendResult.ok(respuestaSimulada);
    }

    @Override
    public String nombre() {
        return "mock";
    }
}
