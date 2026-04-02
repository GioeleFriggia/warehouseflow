package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.AuthRequest;
import com.gioele.warehouseflow.dto.AuthResponse;
import com.gioele.warehouseflow.dto.UserResponse;
import com.gioele.warehouseflow.entity.AuditAction;
import com.gioele.warehouseflow.entity.User;
import com.gioele.warehouseflow.repository.UserRepository;
import com.gioele.warehouseflow.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       UserService userService,
                       AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        String token = jwtService.generateToken(user.getEmail());
        auditLogService.log(AuditAction.LOGIN_SUCCESS, "Auth", user.getEmail(), user,
                null, "token-created", "Login riuscito");

        UserResponse responseUser = userService.toResponse(user);
        return new AuthResponse(token, responseUser);
    }
}
