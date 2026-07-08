package com.mercadona.activo2incidencias.repository;

import com.mercadona.activo2incidencias.domain.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByEmailIgnoreCase(String email);
}
