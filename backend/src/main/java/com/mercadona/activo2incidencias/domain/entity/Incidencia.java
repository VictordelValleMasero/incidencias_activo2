package com.mercadona.activo2incidencias.domain.entity;

import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoNotificacionWhatsapp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edificio_id", nullable = false)
    private Edificio edificio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Responsable responsable;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoIncidencia estado = EstadoIncidencia.NUEVA;

    @Enumerated(EnumType.STRING)
    @Column(name = "whatsapp_estado", nullable = false, length = 30)
    @Builder.Default
    private EstadoNotificacionWhatsapp whatsappEstado = EstadoNotificacionWhatsapp.PENDIENTE;

    @Column(name = "whatsapp_error", columnDefinition = "text")
    private String whatsappError;

    @Column(name = "mensaje_whatsapp", columnDefinition = "text")
    private String mensajeWhatsapp;

    @Column(name = "observaciones_internas", columnDefinition = "text")
    private String observacionesInternas;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "incidencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImagenIncidencia> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "incidencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<IncidenciaHistorial> historial = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
