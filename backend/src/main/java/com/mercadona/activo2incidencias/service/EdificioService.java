package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.BusinessException;
import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.Edificio;
import com.mercadona.activo2incidencias.dto.edificio.EdificioRequest;
import com.mercadona.activo2incidencias.repository.EdificioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EdificioService {

    private final EdificioRepository edificioRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Edificio crear(EdificioRequest request) {
        if (edificioRepository.existsByCodigoIgnoreCase(request.getCodigo())) {
            throw new BusinessException("Ya existe un edificio con ese codigo");
        }
        Edificio edificio = Edificio.builder()
                .nombre(request.getNombre())
                .codigo(request.getCodigo())
                .descripcion(request.getDescripcion())
                .estado(request.getEstado())
                .orden(request.getOrden())
                .build();
        edificio = edificioRepository.save(edificio);
        auditoriaService.registrar("CREAR", "Edificio", edificio.getId(), edificio.getNombre());
        return edificio;
    }

    @Transactional
    public Edificio actualizar(Long id, EdificioRequest request) {
        Edificio edificio = obtener(id);
        edificio.setNombre(request.getNombre());
        edificio.setCodigo(request.getCodigo());
        edificio.setDescripcion(request.getDescripcion());
        edificio.setEstado(request.getEstado());
        edificio.setOrden(request.getOrden());
        edificio = edificioRepository.save(edificio);
        auditoriaService.registrar("EDITAR", "Edificio", edificio.getId(), edificio.getNombre());
        return edificio;
    }

    @Transactional
    public void eliminar(Long id) {
        Edificio edificio = obtener(id);
        edificioRepository.delete(edificio);
        auditoriaService.registrar("ELIMINAR", "Edificio", id, edificio.getNombre());
    }

    @Transactional(readOnly = true)
    public Edificio obtener(Long id) {
        return edificioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Edificio> listar() {
        return edificioRepository.findAllByOrderByOrdenAsc();
    }
}
