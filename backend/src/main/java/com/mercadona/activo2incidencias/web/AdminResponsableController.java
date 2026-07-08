package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.responsable.ResponsableRequest;
import com.mercadona.activo2incidencias.dto.responsable.ResponsableResponse;
import com.mercadona.activo2incidencias.service.ResponsableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/responsibles")
@RequiredArgsConstructor
public class AdminResponsableController {

    private final ResponsableService responsableService;

    @GetMapping
    public ResponseEntity<List<ResponsableResponse>> listar() {
        return ResponseEntity.ok(responsableService.listar().stream().map(ResponsableResponse::from).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<ResponsableResponse> crear(@Valid @RequestBody ResponsableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponsableResponse.from(responsableService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsableResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ResponsableRequest request) {
        return ResponseEntity.ok(ResponsableResponse.from(responsableService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        responsableService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
