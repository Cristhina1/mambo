package com.sistemaFacturacion.Mambo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaFacturacion.Mambo.auth.dto.LoginRequest;
import com.sistemaFacturacion.Mambo.auth.dto.RegisterRequest;
import com.sistemaFacturacion.Mambo.auth.response.AuthResponse;
import com.sistemaFacturacion.Mambo.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin("http://localhost:4200/")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService; // Aquí se maneja la lógica de login y registro

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
