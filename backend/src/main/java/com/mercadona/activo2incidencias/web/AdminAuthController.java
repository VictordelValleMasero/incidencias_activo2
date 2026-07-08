package com.mercadona.activo2incidencias.web;

import com.mercadona.activo2incidencias.dto.auth.LoginRequest;
import com.mercadona.activo2incidencias.dto.auth.LoginResponse;
import com.mercadona.activo2incidencias.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
