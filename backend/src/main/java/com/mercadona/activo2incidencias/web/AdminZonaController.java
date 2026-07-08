package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.zona.ZonaRequest;
import com.mercadona.activo2incidencias.dto.zona.ZonaResponse;
import com.mercadona.activo2incidencias.service.ZonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/zones")
@RequiredArgsConstructor
public class AdminZonaController {

    private final ZonaService zonaService;

    @GetMapping
    public ResponseEntity<List<ZonaResponse>> listar() {
        return ResponseEntity.ok(zonaService.listar().stream().map(ZonaResponse::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<ZonaResponse> crear(@Valid @RequestBody ZonaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ZonaResponse.from(zonaService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZonaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ZonaRequest request) {
        return ResponseEntity.ok(ZonaResponse.from(zonaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
