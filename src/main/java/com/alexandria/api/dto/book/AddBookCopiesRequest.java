package com.alexandria.api.dto.book;

import jakarta.validation.constraints.NotNull;

public record AddBookCopiesRequest(
    @NotNull(message = "A quantidade de exemplares é obrigatória.")
    Integer quantity
) {}
