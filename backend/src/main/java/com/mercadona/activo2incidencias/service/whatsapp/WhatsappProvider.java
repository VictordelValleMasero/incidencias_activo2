package com.mercadona.activo2incidencias.service.whatsapp;

/**
 * Abstraccion sobre el proveedor de envio de WhatsApp.
 * Implementaciones futuras (Meta Cloud API, Twilio, 360dialog, MessageBird,
 * Vonage) deben implementar esta interfaz sin tocar el resto del dominio:
 * basta con anadir un @Component activado por app.whatsapp.proveedor.
 */
public interface WhatsappProvider {

    WhatsappSendResult enviar(String telefonoE164, String mensaje);

    String nombre();
}
