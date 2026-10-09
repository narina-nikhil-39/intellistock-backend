package com.nikhil.IntelliStock.controller;

import com.nikhil.IntelliStock.dto.AuthRequestDTO;
import com.nikhil.IntelliStock.dto.AuthResponseDTO;
import com.nikhil.IntelliStock.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Only clients authenticated as ROLE_ADMIN can create additional admins
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponseDTO> createAdmin(@Valid @RequestBody AuthRequestDTO request) {
        return new ResponseEntity<>(authService.createAdminUser(request), HttpStatus.CREATED);
    }
}