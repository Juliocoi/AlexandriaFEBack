package com.alexandria.api.service;

import com.alexandria.api.domain.book.Book;
import com.alexandria.api.domain.user.User;
import com.alexandria.api.dto.book.BookResponse;
import com.alexandria.api.repository.BookRepository;
import com.alexandria.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getUserFavorites(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        return user.getFavoriteBooks().stream()
                .map(BookResponse::new)
                .toList();
    }

    @Transactional
    public void addFavorite(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));

        // Como favoriteBooks é um Set<Book>, se já existir ele não duplica
        user.getFavoriteBooks().add(book);
        userRepository.save(user);
    }

    @Transactional
    public void removeFavorite(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));

        user.getFavoriteBooks().remove(book);
        userRepository.save(user);
    }
}
