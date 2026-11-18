package com.alexandria.api.domain.book;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@EqualsAndHashCode(of = "id") 
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "co_author")
    private String coAuthor;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Integer edition;

    private String publisher;

    @Column(nullable = false)
    private String bookshelf;

    private String category;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies = 1;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies = 1;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private Integer pages;

    private String language;

    // Método auxiliar (não cria coluna no banco) para definir disponibilidade
    public boolean isAvailable() {
        return this.availableCopies != null && this.availableCopies > 0;
    }

}
