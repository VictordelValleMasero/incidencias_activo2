package com.mercadona.activo2incidencias.dto.incidencia;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Datos del formulario publico "Comunicar incidencia", enviados como campos
 * de un multipart/form-data (edificioId, zonaId, departamentoId, descripcion),
 * junto con la parte "imagenes" (0-5 ficheros) que se recibe por separado.
 */
@Getter
@Setter
public class IncidenciaReportRequest {

    @NotNull(message = "El edificio es obligatorio")
    private Long edificioId;

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "El departamento es obligatorio")
    private Long departamentoId;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripcion debe tener entre 10 y 1000 caracteres")
    private String descripcion;
}
