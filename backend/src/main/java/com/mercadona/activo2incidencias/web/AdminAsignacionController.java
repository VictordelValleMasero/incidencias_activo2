package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.asignacion.AsignacionRequest;
import com.mercadona.activo2incidencias.dto.asignacion.AsignacionResponse;
import com.mercadona.activo2incidencias.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/assignments")
@RequiredArgsConstructor
public class AdminAsignacionController {

    private final AsignacionService asignacionService;

    @GetMapping
    public ResponseEntity<List<AsignacionResponse>> listar() {
        return ResponseEntity.ok(asignacionService.listar().stream().map(AsignacionResponse::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AsignacionResponse> crear(@Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(AsignacionResponse.from(asignacionService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionResponse> actualizar(@PathVariable Long id, @Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.ok(AsignacionResponse.from(asignacionService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
