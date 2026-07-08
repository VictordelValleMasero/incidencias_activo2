package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.IncidenciaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenciaHistorialRepository extends JpaRepository<IncidenciaHistorial, Long> {

    List<IncidenciaHistorial> findByIncidenciaIdOrderByFechaDesc(Long incidenciaId);
}
