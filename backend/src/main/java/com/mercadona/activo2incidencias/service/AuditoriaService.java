package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.domain.entity.Auditoria;
import com.mercadona.activo2incidencias.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrar(String accion, String entidad, Object entidadId, String detalles) {
        registrar(accion, entidad, entidadId, detalles, null);
    }

    public void registrar(String accion, String entidad, Object entidadId, String detalles, String ip) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "publico";

        Auditoria auditoria = Auditoria.builder()
                .usuarioId(usuarioId)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId != null ? entidadId.toString() : null)
                .detalles(detalles)
                .ip(ip)
                .build();

        auditoriaRepository.save(auditoria);
    }
}
