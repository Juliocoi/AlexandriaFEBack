package com.alexandria.api.controller;

import com.alexandria.api.dto.book.BookResponse;
import com.alexandria.api.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Lista os livros favoritos do usuário (para a sessão superior da Home no React)
    @GetMapping
    public ResponseEntity<List<BookResponse>> listFavorites(@PathVariable UUID userId) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }

    // Favorita um livro
    @PostMapping("/{bookId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable UUID userId,
            @PathVariable UUID bookId
    ) {
        favoriteService.addFavorite(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    // Remove um livro dos favoritos
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable UUID userId,
            @PathVariable UUID bookId
    ) {
        favoriteService.removeFavorite(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}
