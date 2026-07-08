package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.departamento.DepartamentoRequest;
import com.mercadona.activo2incidencias.dto.departamento.DepartamentoResponse;
import com.mercadona.activo2incidencias.service.DepartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
public class AdminDepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar() {
        return ResponseEntity.ok(departamentoService.listar().stream().map(DepartamentoResponse::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> crear(@Valid @RequestBody DepartamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartamentoResponse.from(departamentoService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody DepartamentoRequest request) {
        return ResponseEntity.ok(DepartamentoResponse.from(departamentoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
