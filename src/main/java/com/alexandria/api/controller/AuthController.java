package com.alexandria.api.controller;

import com.alexandria.api.domain.user.User;
import com.alexandria.api.dto.auth.LoginRequest;
import com.alexandria.api.dto.auth.LoginResponse;
import com.alexandria.api.dto.user.UserResponse;
import com.alexandria.api.infra.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var credentials = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(credentials);

        User authenticatedUser = (User) auth.getPrincipal();
        String token = tokenService.generateToken(authenticatedUser);

        return ResponseEntity.ok(new LoginResponse(token, new UserResponse(authenticatedUser)));
    }
}
