package com.alexandria.api.repository;

import com.alexandria.api.domain.loan.Loan;
import com.alexandria.api.domain.loan.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    // Conta quantos empréstimos ativos o aluno possui (para a trava de no máximo 4 livros)
    long countByUserIdAndStatus(UUID userId, LoanStatus status);

    // Verifica se um livro possui empréstimos ativos (impede excluir um livro que está emprestado)
    boolean existsByBookIdAndStatus(UUID bookId, LoanStatus status);

    // impede excluir um usuário que ainda possui livros emprestados
    boolean existsByUserIdAndStatus(UUID userId, LoanStatus status);

    // Impede que o aluno pegue 2 exemplares do mesmo livro ao mesmo tempo
    boolean existsByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, LoanStatus status);

    // Lista todos os empréstimos de um usuário específico (para a tela "Meu Perfil")
    List<Loan> findByUserIdOrderByLoanDateDesc(UUID userId);
}
