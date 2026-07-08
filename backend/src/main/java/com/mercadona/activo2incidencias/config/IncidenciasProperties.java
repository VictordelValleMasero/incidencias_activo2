package com.mercadona.activo2incidencias.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.incidencias")
@Getter
@Setter
public class IncidenciasProperties {
    private int maxImagenes = 5;
    private int tamanoMaximoImagenMb = 5;
    private int descripcionMinLongitud = 10;
    private int descripcionMaxLongitud = 1000;
}
