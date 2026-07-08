package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.ZonaDepartamentoResponsable;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZonaDepartamentoResponsableRepository extends JpaRepository<ZonaDepartamentoResponsable, Long> {

    List<ZonaDepartamentoResponsable> findByZonaIdAndEstado(Long zonaId, EstadoActivo estado);

    Optional<ZonaDepartamentoResponsable> findFirstByZonaIdAndDepartamentoIdAndEstado(Long zonaId, Long departamentoId, EstadoActivo estado);

    List<ZonaDepartamentoResponsable> findAllByOrderByIdDesc();
}
