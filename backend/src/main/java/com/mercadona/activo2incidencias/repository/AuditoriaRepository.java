package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}
