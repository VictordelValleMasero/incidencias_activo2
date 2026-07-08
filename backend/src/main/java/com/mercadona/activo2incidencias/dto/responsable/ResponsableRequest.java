package com.mercadona.activo2incidencias.dto.responsable;

import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ResponsableRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellidos;

    private String cargo;

    @NotNull(message = "El departamento es obligatorio")
    private Long departamentoId;

    @NotBlank(message = "El telefono de WhatsApp es obligatorio")
    @Pattern(regexp = "^\\+[1-9][0-9]{7,14}$", message = "El telefono debe incluir el prefijo internacional, formato E.164 (ej. +34600000000)")
    private String telefonoWhatsapp;

    private String email;

    private String horario;

    private EstadoActivo estado = EstadoActivo.ACTIVO;
}
