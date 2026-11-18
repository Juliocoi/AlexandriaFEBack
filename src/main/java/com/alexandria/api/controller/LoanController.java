package com.alexandria.api.controller;

import com.alexandria.api.domain.user.User;
import com.alexandria.api.domain.user.UserRole;
import com.alexandria.api.dto.loan.CreateLoanRequest;
import com.alexandria.api.dto.loan.LoanResponse;
import com.alexandria.api.dto.loan.ReturnLoanRequest;
import com.alexandria.api.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // Realizar empréstimo
    @PostMapping
    public ResponseEntity<LoanResponse> borrowBook(
        @RequestBody @Valid CreateLoanRequest request,
        @AuthenticationPrincipal User loggedUser
    ) {
        validateOwnership(loggedUser, request.userId());
        LoanResponse response = loanService.borrowBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Devolver livro
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<LoanResponse> returnBook(
            @PathVariable UUID loanId,
            @RequestBody @Valid ReturnLoanRequest request,
            @AuthenticationPrincipal User loggedUser
    ) {
        validateOwnership(loggedUser, request.userId());
        LoanResponse response = loanService.returnBook(loanId, request.userId());
        return ResponseEntity.ok(response);
    }

    // Listar histórico e empréstimos ativos de um usuário (Tela "Meu Perfil" / "Meus Livros")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getUserLoans(
        @PathVariable UUID userId,
        @AuthenticationPrincipal User loggedUser    
    ) {
        validateOwnership(loggedUser, userId);
        return ResponseEntity.ok(loanService.findUserLoans(userId));
    }

    // Regra de proteção: Funcionário (STAFF) pode acessar qualquer conta; Aluno (STUDENT) só a própria
    private void validateOwnership(User loggedUser, UUID targetUserId) {
        if (loggedUser.getRole() == UserRole.STUDENT && !loggedUser.getId().equals(targetUserId)) {
            throw new SecurityException("Acesso negado: você só tem permissão para realizar operações na sua própria conta.");
        }
    }
}
