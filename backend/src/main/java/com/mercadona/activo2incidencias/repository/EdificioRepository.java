package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Edificio;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdificioRepository extends JpaRepository<Edificio, Long> {

    List<Edificio> findByEstadoOrderByOrdenAsc(EstadoActivo estado);

    List<Edificio> findAllByOrderByOrdenAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    long countByEstado(EstadoActivo estado);
}
