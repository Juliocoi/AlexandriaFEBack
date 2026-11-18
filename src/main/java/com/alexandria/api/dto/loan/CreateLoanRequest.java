package com.alexandria.api.dto.loan;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateLoanRequest(
    @NotNull(message = "O ID do usuário é obrigatório.")
    UUID userId,

    @NotNull(message = "O ID do livro é obrigatório.")
    UUID bookId
) {}
