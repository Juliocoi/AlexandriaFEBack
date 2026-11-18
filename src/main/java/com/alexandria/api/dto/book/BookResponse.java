package com.alexandria.api.dto.book;

import com.alexandria.api.domain.book.Book;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record BookResponse(
    UUID id,
    String coverUrl,
    String isbn,
    String title,
    String author,
    String coAuthor,
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate releaseDate,
    Integer edition,
    String publisher,
    String bookshelf,
    Integer totalCopies,
    Integer availableCopies,
    boolean isAvailable,
    String category,
    String synopsis,
    Integer pages,
    String language
) {
    public BookResponse(Book book) {
        this(
            book.getId(),
            book.getCoverUrl(),
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getCoAuthor(),
            book.getReleaseDate(),
            book.getEdition(),
            book.getPublisher(),
            book.getBookshelf(),
            book.getTotalCopies(),
            book.getAvailableCopies(),
            book.isAvailable(),
            book.getCategory(),
            book.getSynopsis(),
            book.getPages(),
            book.getLanguage()
        );
    }
}
