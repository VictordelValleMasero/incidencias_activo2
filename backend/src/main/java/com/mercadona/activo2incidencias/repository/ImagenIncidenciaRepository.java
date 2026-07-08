package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.ImagenIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenIncidenciaRepository extends JpaRepository<ImagenIncidencia, Long> {

    List<ImagenIncidencia> findByIncidenciaId(Long incidenciaId);
}
