package com.alexandria.api.dto.loan;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReturnLoanRequest(
    @NotNull(message = "O ID do usuário que está devolvendo é obrigatório.")
    UUID userId
) {}
