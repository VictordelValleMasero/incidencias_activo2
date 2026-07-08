package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Zona;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Long> {

    List<Zona> findByEdificioIdAndEstadoOrderByOrdenAsc(Long edificioId, EstadoActivo estado);

    List<Zona> findAllByOrderByOrdenAsc();

    long countByEstado(EstadoActivo estado);
}
