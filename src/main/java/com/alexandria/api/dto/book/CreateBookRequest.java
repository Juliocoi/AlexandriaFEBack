package com.alexandria.api.dto.book;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
    String coverUrl,

    @NotBlank(message = "O ISBN é obrigatório.")
    String isbn,

    @NotBlank(message = "O título do livro é obrigatório.")
    String title,

    @NotBlank(message = "O autor principal é obrigatório.")
    String author,

    String coAuthor,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate releaseDate,

    Integer edition,

    String publisher,

    @NotBlank(message = "A localização na estante é obrigatória.")
    String bookshelf,

    @NotNull(message = "A quantidade total de exemplares é obrigatória.")
    @Min(value = 1, message = "O livro deve ter pelo menos 1 exemplar.")
    Integer totalCopies,

    String category,

    String synopsis,

    Integer pages,

    String language
) {}
