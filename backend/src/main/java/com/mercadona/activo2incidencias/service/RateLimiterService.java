package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.RateLimitException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limitador de peticiones en memoria para proteger el endpoint publico de
 * creacion de incidencias frente a spam o bots.
 * Para un despliegue multi-instancia, sustituir por un backend compartido (Redis).
 */
@Service
public class RateLimiterService {

    private static final long VENTANA_MS = 60_000L;

    private final ConcurrentHashMap<String, Ventana> contadores = new ConcurrentHashMap<>();

    public void comprobarLimite(String clave, int maxPorMinuto) {
        Ventana ventana = contadores.computeIfAbsent(clave, k -> new Ventana());
        long ahora = Instant.now().toEpochMilli();

        synchronized (ventana) {
            if (ahora - ventana.inicio > VENTANA_MS) {
                ventana.inicio = ahora;
                ventana.contador.set(0);
            }
            if (ventana.contador.incrementAndGet() > maxPorMinuto) {
                throw new RateLimitException("Se han superado las solicitudes permitidas. Intentalo de nuevo en un minuto.");
            }
        }
    }

    private static class Ventana {
        volatile long inicio = Instant.now().toEpochMilli();
        AtomicInteger contador = new AtomicInteger(0);
    }
}
