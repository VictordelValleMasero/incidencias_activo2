package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.*;
import com.mercadona.activo2incidencias.dto.asignacion.AsignacionRequest;
import com.mercadona.activo2incidencias.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final ZonaDepartamentoResponsableRepository asignacionRepository;
    private final EdificioRepository edificioRepository;
    private final ZonaRepository zonaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ResponsableRepository responsableRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public ZonaDepartamentoResponsable crear(AsignacionRequest request) {
        ZonaDepartamentoResponsable asignacion = ZonaDepartamentoResponsable.builder()
                .edificio(resolverEdificio(request.getEdificioId()))
                .zona(resolverZona(request.getZonaId()))
                .departamento(resolverDepartamento(request.getDepartamentoId()))
                .responsable(resolverResponsable(request.getResponsableId()))
                .estado(request.getEstado())
                .observaciones(request.getObservaciones())
                .build();
        asignacion = asignacionRepository.save(asignacion);
        auditoriaService.registrar("CREAR", "ZonaDepartamentoResponsable", asignacion.getId(),
                "Zona " + asignacion.getZona().getNombre() + " / Departamento " + asignacion.getDepartamento().getNombre());
        return asignacion;
    }

    @Transactional
    public ZonaDepartamentoResponsable actualizar(Long id, AsignacionRequest request) {
        ZonaDepartamentoResponsable asignacion = obtener(id);
        asignacion.setEdificio(resolverEdificio(request.getEdificioId()));
        asignacion.setZona(resolverZona(request.getZonaId()));
        asignacion.setDepartamento(resolverDepartamento(request.getDepartamentoId()));
        asignacion.setResponsable(resolverResponsable(request.getResponsableId()));
        asignacion.setEstado(request.getEstado());
        asignacion.setObservaciones(request.getObservaciones());
        asignacion = asignacionRepository.save(asignacion);
        auditoriaService.registrar("EDITAR", "ZonaDepartamentoResponsable", asignacion.getId(), "Actualizada");
        return asignacion;
    }

    @Transactional
    public void eliminar(Long id) {
        ZonaDepartamentoResponsable asignacion = obtener(id);
        asignacionRepository.delete(asignacion);
        auditoriaService.registrar("ELIMINAR", "ZonaDepartamentoResponsable", id, "Eliminada");
    }

    @Transactional(readOnly = true)
    public ZonaDepartamentoResponsable obtener(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignacion no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<ZonaDepartamentoResponsable> listar() {
        return asignacionRepository.findAllByOrderByIdDesc();
    }

    private Edificio resolverEdificio(Long id) {
        return edificioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
    }

    private Zona resolverZona(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));
    }

    private Departamento resolverDepartamento(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    }

    private Responsable resolverResponsable(Long id) {
        return responsableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));
    }
}
