package com.mercadona.activo2incidencias.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {
    private String rootPath = "./uploads";
}
