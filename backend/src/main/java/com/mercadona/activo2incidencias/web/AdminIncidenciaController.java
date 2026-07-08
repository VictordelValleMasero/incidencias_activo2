package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.domain.enums.EstadoIncidencia;
import com.mercadona.activo2incidencias.dto.incidencia.CambiarEstadoRequest;
import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaDetalleResponse;
import com.mercadona.activo2incidencias.dto.incidencia.IncidenciaResumenResponse;
import com.mercadona.activo2incidencias.service.IncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/incidents")
@RequiredArgsConstructor
public class AdminIncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<Page<IncidenciaResumenResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long edificioId,
            @RequestParam(required = false) Long zonaId,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) EstadoIncidencia estado,
            @RequestParam(required = false) Boolean whatsappError,
            @RequestParam(required = false) String texto,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(incidenciaService.listarAdmin(
                fecha, edificioId, zonaId, departamentoId, estado, whatsappError, texto, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDetalleResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.obtenerDetalle(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidenciaDetalleResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(incidenciaService.cambiarEstado(id, request.getEstado(), request.getObservaciones()));
    }

    @PostMapping("/{id}/resend-whatsapp")
    public ResponseEntity<IncidenciaDetalleResponse> reenviarWhatsapp(@PathVariable Long id) {
        return ResponseEntity.ok(incidenciaService.reenviarWhatsapp(id));
    }
}
