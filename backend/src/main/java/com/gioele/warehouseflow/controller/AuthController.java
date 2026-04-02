package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.AuthRequest;
import com.gioele.warehouseflow.dto.AuthResponse;
import com.gioele.warehouseflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
