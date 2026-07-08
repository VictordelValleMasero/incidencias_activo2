package com.mercadona.activo2incidencias.service;

import com.mercadona.activo2incidencias.common.exception.BusinessException;
import com.mercadona.activo2incidencias.config.IncidenciasProperties;
import com.mercadona.activo2incidencias.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Abstraccion sobre el almacenamiento de ficheros. Guarda en disco local bajo
 * STORAGE_ROOT por defecto; para mover a S3/R2/Azure Blob en el futuro basta
 * con sustituir esta implementacion sin tocar el resto del dominio.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final StorageProperties storageProperties;
    private final IncidenciasProperties incidenciasProperties;

    public String guardarImagenIncidencia(MultipartFile file, Long incidenciaId) {
        validarImagen(file);
        return guardar(file, "incidencias/" + incidenciaId);
    }

    public Resource cargar(String rutaRelativa) {
        try {
            Path path = raiz().resolve(rutaRelativa).normalize();
            if (!path.startsWith(raiz())) {
                throw new BusinessException("Ruta de archivo no valida");
            }
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException("El archivo no existe");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new BusinessException("Ruta de archivo no valida");
        }
    }

    public void validarImagen(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo esta vacio");
        }
        String tipo = file.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase())) {
            throw new BusinessException("Formato de imagen no permitido. Usa JPG, PNG o WEBP");
        }
        long maxBytes = (long) incidenciasProperties.getTamanoMaximoImagenMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("La imagen supera el tamano maximo permitido (" + incidenciasProperties.getTamanoMaximoImagenMb() + " MB)");
        }
        String nombreOriginal = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        if (nombreOriginal.contains("..")) {
            throw new BusinessException("Nombre de archivo no valido");
        }
    }

    private String guardar(MultipartFile file, String subcarpeta) {
        try {
            Path destino = raiz().resolve(subcarpeta).normalize();
            Files.createDirectories(destino);

            String extension = extensionDe(file.getOriginalFilename(), file.getContentType());
            String nombreAlmacenado = UUID.randomUUID() + extension;
            Path destinoArchivo = destino.resolve(nombreAlmacenado);

            Files.copy(file.getInputStream(), destinoArchivo);

            return subcarpeta + "/" + nombreAlmacenado;
        } catch (IOException e) {
            throw new BusinessException("No se ha podido guardar el archivo");
        }
    }

    private String extensionDe(String nombreOriginal, String contentType) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
        }
        if ("image/png".equalsIgnoreCase(contentType)) return ".png";
        if ("image/webp".equalsIgnoreCase(contentType)) return ".webp";
        return ".jpg";
    }

    private Path raiz() {
        return Paths.get(storageProperties.getRootPath()).toAbsolutePath().normalize();
    }
}
