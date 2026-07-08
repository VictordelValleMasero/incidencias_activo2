package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Departamento;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    List<Departamento> findAllByOrderByNombreAsc();

    long countByEstado(EstadoActivo estado);
}
