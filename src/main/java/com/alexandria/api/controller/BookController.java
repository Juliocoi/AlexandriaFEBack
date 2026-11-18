package com.alexandria.api.controller;

import com.alexandria.api.dto.book.AddBookCopiesRequest;
import com.alexandria.api.dto.book.BookResponse;
import com.alexandria.api.dto.book.CreateBookRequest;
import com.alexandria.api.dto.book.UpdateBookRequest;
import com.alexandria.api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET /api/books (lista todos ou filtra via ?title=..., ?author=..., ?category=..., ?query=...)
    @GetMapping
    public ResponseEntity<List<BookResponse>> listOrSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query
    ) {
        List<BookResponse> books = bookService.searchBooks(title, author, category, query);
        return ResponseEntity.ok(books);
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody @Valid CreateBookRequest request) {
        BookResponse created = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBookRequest request
    ) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    // PATCH /api/books/{id}/copies
    @PatchMapping("/{id}/copies")
    public ResponseEntity<BookResponse> updateStock(
            @PathVariable UUID id,
            @RequestBody @Valid AddBookCopiesRequest request
    ) {
        return ResponseEntity.ok(bookService.updateStock(id, request.quantity()));
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content
    }

}
