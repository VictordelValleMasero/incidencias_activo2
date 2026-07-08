package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.edificio.EdificioRequest;
import com.mercadona.activo2incidencias.dto.edificio.EdificioResponse;
import com.mercadona.activo2incidencias.service.EdificioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/buildings")
@RequiredArgsConstructor
public class AdminEdificioController {

    private final EdificioService edificioService;

    @GetMapping
    public ResponseEntity<List<EdificioResponse>> listar() {
        return ResponseEntity.ok(edificioService.listar().stream().map(EdificioResponse::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<EdificioResponse> crear(@Valid @RequestBody EdificioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(EdificioResponse.from(edificioService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EdificioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody EdificioRequest request) {
        return ResponseEntity.ok(EdificioResponse.from(edificioService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        edificioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
