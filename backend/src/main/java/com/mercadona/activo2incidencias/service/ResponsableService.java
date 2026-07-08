package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.Departamento;
import com.mercadona.activo2incidencias.domain.entity.Responsable;
import com.mercadona.activo2incidencias.dto.responsable.ResponsableRequest;
import com.mercadona.activo2incidencias.repository.DepartamentoRepository;
import com.mercadona.activo2incidencias.repository.ResponsableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsableService {

    private final ResponsableRepository responsableRepository;
    private final DepartamentoRepository departamentoRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Responsable crear(ResponsableRequest request) {
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));

        Responsable responsable = Responsable.builder()
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .cargo(request.getCargo())
                .departamento(departamento)
                .telefonoWhatsapp(request.getTelefonoWhatsapp())
                .email(request.getEmail())
                .horario(request.getHorario())
                .estado(request.getEstado())
                .build();

        responsable = responsableRepository.save(responsable);
        auditoriaService.registrar("CREAR", "Responsable", responsable.getId(), responsable.getNombre());
        return responsable;
    }

    @Transactional
    public Responsable actualizar(Long id, ResponsableRequest request) {
        Responsable responsable = obtener(id);
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));

        responsable.setNombre(request.getNombre());
        responsable.setApellidos(request.getApellidos());
        responsable.setCargo(request.getCargo());
        responsable.setDepartamento(departamento);
        responsable.setTelefonoWhatsapp(request.getTelefonoWhatsapp());
        responsable.setEmail(request.getEmail());
        responsable.setHorario(request.getHorario());
        responsable.setEstado(request.getEstado());

        responsable = responsableRepository.save(responsable);
        auditoriaService.registrar("EDITAR", "Responsable", responsable.getId(), responsable.getNombre());
        return responsable;
    }

    @Transactional
    public void eliminar(Long id) {
        Responsable responsable = obtener(id);
        responsableRepository.delete(responsable);
        auditoriaService.registrar("ELIMINAR", "Responsable", id, responsable.getNombre());
    }

    @Transactional(readOnly = true)
    public Responsable obtener(Long id) {
        return responsableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Responsable> listar() {
        return responsableRepository.findAllByOrderByNombreAsc();
    }
}
