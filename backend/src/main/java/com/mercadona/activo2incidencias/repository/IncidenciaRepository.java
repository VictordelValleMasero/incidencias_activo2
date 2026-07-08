package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Incidencia;
import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long>, JpaSpecificationExecutor<Incidencia> {

    Optional<Incidencia> findByCodigo(String codigo);

    long countByCreatedAtBetween(LocalDateTime desde, LocalDateTime hasta);

    long countByEstado(EstadoIncidencia estado);

    long countByEstadoIn(List<EstadoIncidencia> estados);

    long countByWhatsappError(String whatsappError);

    List<Incidencia> findTop10ByOrderByCreatedAtDesc();
}
