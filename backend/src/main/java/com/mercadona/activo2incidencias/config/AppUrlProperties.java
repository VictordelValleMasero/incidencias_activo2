package com.mercadona.activo2incidencias.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppUrlProperties {
    private String publicUrl = "http://localhost:4200";
}
