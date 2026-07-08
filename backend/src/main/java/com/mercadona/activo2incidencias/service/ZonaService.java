package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.Edificio;
import com.mercadona.activo2incidencias.domain.entity.Zona;
import com.mercadona.activo2incidencias.dto.zona.ZonaRequest;
import com.mercadona.activo2incidencias.repository.EdificioRepository;
import com.mercadona.activo2incidencias.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaService {

    private final ZonaRepository zonaRepository;
    private final EdificioRepository edificioRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Zona crear(ZonaRequest request) {
        Edificio edificio = edificioRepository.findById(request.getEdificioId())
                .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));

        Zona zona = Zona.builder()
                .edificio(edificio)
                .nombre(request.getNombre())
                .codigo(request.getCodigo())
                .descripcion(request.getDescripcion())
                .imagen(request.getImagen())
                .estado(request.getEstado())
                .orden(request.getOrden())
                .build();
        zona = zonaRepository.save(zona);
        auditoriaService.registrar("CREAR", "Zona", zona.getId(), zona.getNombre());
        return zona;
    }

    @Transactional
    public Zona actualizar(Long id, ZonaRequest request) {
        Zona zona = obtener(id);
        Edificio edificio = edificioRepository.findById(request.getEdificioId())
                .orElseThrow(() -> new ResourceNotFoundException("Edificio no encontrado"));

        zona.setEdificio(edificio);
        zona.setNombre(request.getNombre());
        zona.setCodigo(request.getCodigo());
        zona.setDescripcion(request.getDescripcion());
        zona.setImagen(request.getImagen());
        zona.setEstado(request.getEstado());
        zona.setOrden(request.getOrden());
        zona = zonaRepository.save(zona);
        auditoriaService.registrar("EDITAR", "Zona", zona.getId(), zona.getNombre());
        return zona;
    }

    @Transactional
    public void eliminar(Long id) {
        Zona zona = obtener(id);
        zonaRepository.delete(zona);
        auditoriaService.registrar("ELIMINAR", "Zona", id, zona.getNombre());
    }

    @Transactional(readOnly = true)
    public Zona obtener(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Zona> listar() {
        return zonaRepository.findAllByOrderByOrdenAsc();
    }
}
