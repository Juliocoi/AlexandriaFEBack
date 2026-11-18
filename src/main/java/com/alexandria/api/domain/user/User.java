package com.alexandria.api.domain.user;

import com.alexandria.api.domain.book.Book;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "mother_name", nullable = false)
    private String motherName;

    @Column(name = "father_name")
    private String fatherName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    
    private Set<Book> favoriteBooks = new HashSet<>();

    // --- Métodos obrigatórios da interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por convenção do Spring Security, perfis (roles) usam o prefixo "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email; // Nosso identificador de login é o e-mail
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
