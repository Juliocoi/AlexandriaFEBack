package com.alexandria.api.controller;

import com.alexandria.api.dto.user.CreateUserRequest;
import com.alexandria.api.dto.user.RegisterStudentRequest;
import com.alexandria.api.dto.user.UpdateUserRequest;
import com.alexandria.api.dto.user.UserResponse;
import com.alexandria.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.alexandria.api.domain.user.User;
import com.alexandria.api.domain.user.UserRole;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // Auto-cadastro público para Alunos
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerStudent(@RequestBody @Valid RegisterStudentRequest request) {
        UserResponse created = userService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Cadastro administrativo (Funcionário pode cadastrar Aluno ou outro Funcionário)
    @PostMapping
    public ResponseEntity<UserResponse> createByStaff(@RequestBody @Valid CreateUserRequest request) {
        UserResponse created = userService.createByStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
        @PathVariable UUID id,
        @AuthenticationPrincipal User loggedUser
    ) {
        validateOwnership(loggedUser, id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request,
            @AuthenticationPrincipal User loggedUser
    ) {
        validateOwnership(loggedUser, id);
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void validateOwnership(User loggedUser, UUID targetUserId) {
        if (loggedUser.getRole() == UserRole.STUDENT && !loggedUser.getId().equals(targetUserId)) {
            throw new SecurityException("Acesso negado: você só tem permissão para visualizar ou editar o seu próprio perfil.");
        }
    }
}
