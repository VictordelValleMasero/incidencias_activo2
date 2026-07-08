package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.Departamento;
import com.mercadona.activo2incidencias.domain.entity.Responsable;
import com.mercadona.activo2incidencias.dto.departamento.DepartamentoRequest;
import com.mercadona.activo2incidencias.repository.DepartamentoRepository;
import com.mercadona.activo2incidencias.repository.ResponsableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final ResponsableRepository responsableRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Departamento crear(DepartamentoRequest request) {
        Departamento departamento = Departamento.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .estado(request.getEstado())
                .responsablePrincipal(resolverResponsable(request.getResponsablePrincipalId()))
                .plantillaWhatsapp(request.getPlantillaWhatsapp())
                .build();
        departamento = departamentoRepository.save(departamento);
        auditoriaService.registrar("CREAR", "Departamento", departamento.getId(), departamento.getNombre());
        return departamento;
    }

    @Transactional
    public Departamento actualizar(Long id, DepartamentoRequest request) {
        Departamento departamento = obtener(id);
        departamento.setNombre(request.getNombre());
        departamento.setDescripcion(request.getDescripcion());
        departamento.setEstado(request.getEstado());
        departamento.setResponsablePrincipal(resolverResponsable(request.getResponsablePrincipalId()));
        departamento.setPlantillaWhatsapp(request.getPlantillaWhatsapp());
        departamento = departamentoRepository.save(departamento);
        auditoriaService.registrar("EDITAR", "Departamento", departamento.getId(), departamento.getNombre());
        return departamento;
    }

    @Transactional
    public void eliminar(Long id) {
        Departamento departamento = obtener(id);
        departamentoRepository.delete(departamento);
        auditoriaService.registrar("ELIMINAR", "Departamento", id, departamento.getNombre());
    }

    @Transactional(readOnly = true)
    public Departamento obtener(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Departamento> listar() {
        return departamentoRepository.findAllByOrderByNombreAsc();
    }

    private Responsable resolverResponsable(Long responsableId) {
        if (responsableId == null) {
            return null;
        }
        return responsableRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));
    }
}
