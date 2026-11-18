package com.alexandria.api.service;

import com.alexandria.api.domain.book.Book;
import com.alexandria.api.domain.loan.LoanStatus;
import com.alexandria.api.dto.book.BookResponse;
import com.alexandria.api.dto.book.CreateBookRequest;
import com.alexandria.api.dto.book.UpdateBookRequest;
import com.alexandria.api.repository.BookRepository;
import com.alexandria.api.repository.LoanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String author, String category, String query) {
        List<Book> books;

        if (query != null && !query.isBlank()) {
            books = bookRepository.searchByGeneralTerm(query.trim());
        } else if (title != null && !title.isBlank()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title.trim());
        } else if (author != null && !author.isBlank()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author.trim());
        } else if (category != null && !category.isBlank()) {
            books = bookRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            books = bookRepository.findAll();
        }

        // Converte cada Book da lista em um BookResponse usando Java Stream API
        return books.stream().map(BookResponse::new).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado com o ID informado."));
        return new BookResponse(book);
    }

    @Transactional
    public BookResponse create(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException(
                "Já existe um livro cadastrado com este ISBN. Utilize a opção de adicionar exemplares."
            );
        }

        Book book = new Book();
        book.setCoverUrl(request.coverUrl());
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setCoAuthor(request.coAuthor());
        book.setReleaseDate(request.releaseDate());
        book.setEdition(request.edition());
        book.setPublisher(request.publisher());
        book.setBookshelf(request.bookshelf());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.totalCopies()); // No cadastro inicial, todos estão disponíveis
        book.setCategory(request.category());
        book.setSynopsis(request.synopsis());
        book.setPages(request.pages());
        book.setLanguage(request.language());

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook);
    }

    @Transactional
    public BookResponse update(UUID id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));

        // Se alterou o ISBN, verifica se o novo ISBN já pertence a outro livro
        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException("O novo ISBN informado já está em uso por outro livro.");
        }

        // Calcula quantos exemplares estão emprestados neste momento
        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (request.totalCopies() < borrowedCopies) {
            throw new IllegalArgumentException(
                "Não é possível reduzir o total para " + request.totalCopies() +
                " pois existem " + borrowedCopies + " exemplares emprestados no momento."
            );
        }

        int copiesDifference = request.totalCopies() - book.getTotalCopies();

        book.setCoverUrl(request.coverUrl());
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setCoAuthor(request.coAuthor());
        book.setReleaseDate(request.releaseDate());
        book.setEdition(request.edition());
        book.setPublisher(request.publisher());
        book.setBookshelf(request.bookshelf());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + copiesDifference);
        book.setCategory(request.category());
        book.setSynopsis(request.synopsis());
        book.setPages(request.pages());
        book.setLanguage(request.language());

        return new BookResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponse updateStock(UUID id, Integer quantity) {
        if (quantity == null || quantity == 0) {
            throw new IllegalArgumentException("A quantidade informada deve ser diferente de zero.");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));

        int newTotalCopies = book.getTotalCopies() + quantity;
        int newAvailableCopies = book.getAvailableCopies() + quantity;

        if (newTotalCopies < 0 || newAvailableCopies < 0) {
            throw new IllegalArgumentException(
                "Operação inválida: não há exemplares disponíveis suficientes para remover esta quantidade."
            );
        }

        book.setTotalCopies(newTotalCopies);
        book.setAvailableCopies(newAvailableCopies);

        return new BookResponse(bookRepository.save(book));
    }

    @Transactional
    public void delete(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Livro não encontrado.");
        }

        if (loanRepository.existsByBookIdAndStatus(id, LoanStatus.ACTIVE)) {
            throw new IllegalStateException(
                "Não é possível excluir este livro pois existem exemplares emprestados no momento."
            );
        }

        bookRepository.deleteById(id);
    }
}
