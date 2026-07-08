package com.mercadona.activo2incidencias.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", length = 180)
    private String usuarioId;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(nullable = false, length = 100)
    private String entidad;

    @Column(name = "entidad_id", length = 50)
    private String entidadId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 64)
    private String ip;

    @Column(columnDefinition = "text")
    private String detalles;

    @PrePersist
    void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
