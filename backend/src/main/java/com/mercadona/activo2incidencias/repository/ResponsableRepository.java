package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Responsable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsableRepository extends JpaRepository<Responsable, Long> {

    List<Responsable> findByDepartamentoId(Long departamentoId);

    List<Responsable> findAllByOrderByNombreAsc();
}
