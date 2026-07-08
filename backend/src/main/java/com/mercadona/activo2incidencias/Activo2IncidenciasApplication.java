package com.mercadona.activo2incidencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Activo2IncidenciasApplication {

    public static void main(String[] args) {
        SpringApplication.run(Activo2IncidenciasApplication.class, args);
    }
}
