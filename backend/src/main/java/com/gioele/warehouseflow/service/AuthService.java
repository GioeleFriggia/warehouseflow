package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.AuthRequest;
import com.gioele.warehouseflow.dto.AuthResponse;
import com.gioele.warehouseflow.dto.UserResponse;
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

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        String token = jwtService.generateToken(user.getEmail());
        UserResponse responseUser = userService.toResponse(user);
        return new AuthResponse(token, responseUser);
    }
}
