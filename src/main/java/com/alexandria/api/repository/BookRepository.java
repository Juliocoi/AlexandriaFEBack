package com.alexandria.api.repository;

import com.alexandria.api.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    boolean existsByIsbn(String isbn);

    // Buscas específicas (ignorando maiúsculas/minúsculas e buscando trechos com LIKE %...%)
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByCategoryIgnoreCase(String category);

    // Busca unificada (para à barra de pesquisa única)
    @Query("""
        SELECT b FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :term, '%'))
           OR LOWER(b.author) LIKE LOWER(CONCAT('%', :term, '%'))
           OR LOWER(b.category) LIKE LOWER(CONCAT('%', :term, '%'))
    """)
    List<Book> searchByGeneralTerm(@Param("term") String term);
}
