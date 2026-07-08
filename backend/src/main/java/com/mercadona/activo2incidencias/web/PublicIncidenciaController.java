package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.departamento.DepartamentoPublicoResponse;
import com.mercadona.activo2incidencias.dto.edificio.EdificioPublicoResponse;
import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaReportRequest;
import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaReportResponse;
import com.mercadona.activo2incidencias.dto.zona.ZonaPublicoResponse;
import com.mercadona.activo2incidencias.service.IncidenciaService;
import com.mercadona.activo2incidencias.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.MessageDigest;
import java.util.List;

/**
 * Flujo publico "Comunicar incidencia" (sin login), consumido por el modulo
 * movil que se abre desde el tile Incidencias de la app Activo 2.
 * Ver SecurityConfig: /api/incidencias/** esta permitido sin autenticacion.
 */
@RestController
@RequestMapping("/api/incidencias")
@RequiredArgsConstructor
@Validated
public class PublicIncidenciaController {

    private final IncidenciaService incidenciaService;
    private final RateLimiterService rateLimiterService;

    @Value("${app.rate-limit.public-incidencias-por-minuto:6}")
    private int limitePorMinuto;

    @GetMapping("/buildings")
    public ResponseEntity<List<EdificioPublicoResponse>> buildings() {
        return ResponseEntity.ok(incidenciaService.listarEdificios());
    }

    @GetMapping("/buildings/{buildingId}/zones")
    public ResponseEntity<List<ZonaPublicoResponse>> zones(@PathVariable Long buildingId) {
        return ResponseEntity.ok(incidenciaService.listarZonas(buildingId));
    }

    @GetMapping("/zones/{zoneId}/departments")
    public ResponseEntity<List<DepartamentoPublicoResponse>> departments(@PathVariable Long zoneId) {
        return ResponseEntity.ok(incidenciaService.listarDepartamentos(zoneId));
    }

    @PostMapping(value = "/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IncidenciaReportResponse> report(
            @Valid @ModelAttribute IncidenciaReportRequest request,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
            HttpServletRequest httpRequest) {

        String ipHash = anonimizarIp(obtenerIp(httpRequest));
        rateLimiterService.comprobarLimite(ipHash, limitePorMinuto);

        IncidenciaReportResponse respuesta = incidenciaService.reportar(request, imagenes);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String anonimizarIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "anon";
        }
    }
}
