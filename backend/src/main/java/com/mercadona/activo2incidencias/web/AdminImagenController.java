package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.common.exception.ResourceNotFoundException;
import com.mercadona.activo2incidencias.domain.entity.ImagenIncidencia;
import com.mercadona.activo2incidencias.repository.ImagenIncidenciaRepository;
import com.mercadona.activo2incidencias.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sirve las imagenes adjuntas a una incidencia. Requiere JWT (todo lo que
 * cuelga de /api/admin/** exige autenticacion salvo /api/admin/auth/**,
 * ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class AdminImagenController {

    private final ImagenIncidenciaRepository imagenIncidenciaRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/{imagenId}")
    public ResponseEntity<Resource> archivo(@PathVariable Long imagenId) {
        ImagenIncidencia imagen = imagenIncidenciaRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada"));
        Resource resource = fileStorageService.cargar(imagen.getNombreArchivo());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getMimeType()))
                .body(resource);
    }
}
