package com.alexandria.api.service;

import com.alexandria.api.domain.book.Book;
import com.alexandria.api.domain.loan.Loan;
import com.alexandria.api.domain.loan.LoanStatus;
import com.alexandria.api.domain.user.User;
import com.alexandria.api.dto.loan.CreateLoanRequest;
import com.alexandria.api.dto.loan.LoanResponse;
import com.alexandria.api.repository.BookRepository;
import com.alexandria.api.repository.LoanRepository;
import com.alexandria.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final int MAX_ACTIVE_LOANS = 4;
    private static final int DEFAULT_LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public LoanResponse borrowBook(CreateLoanRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Livro não encontrado."));

        // Verifica disponibilidade em estoque
        if (!book.isAvailable()) {
            throw new IllegalStateException("Este livro não possui exemplares disponíveis para empréstimo no momento.");
        }

        // Verifica limite máximo de 4 empréstimos ativos por aluno
        long activeLoansCount = loanRepository.countByUserIdAndStatus(user.getId(), LoanStatus.ACTIVE);
        if (activeLoansCount >= MAX_ACTIVE_LOANS) {
            throw new IllegalStateException(
                "Limite atingido: você já possui " + MAX_ACTIVE_LOANS + " empréstimos ativos. Devolva um livro antes de solicitar outro."
            );
        }

        // Impede pegar duas cópias do mesmo livro simultaneamente
        if (loanRepository.existsByUserIdAndBookIdAndStatus(user.getId(), book.getId(), LoanStatus.ACTIVE)) {
            throw new IllegalStateException("Você já possui um empréstimo ativo deste mesmo livro.");
        }

        // Atualiza o estoque disponível do livro
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Registra o empréstimo
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS));
        loan.setStatus(LoanStatus.ACTIVE);

        Loan savedLoan = loanRepository.save(loan);

        return new LoanResponse(savedLoan, "Empréstimo realizado com sucesso!");
    }

    @Transactional
    public LoanResponse returnBook(UUID loanId, UUID userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de empréstimo não encontrado."));

        // Apenas o aluno que realizou o empréstimo pode devolvê-lo
        if (!loan.getUser().getId().equals(userId)) {
            throw new SecurityException("Acesso negado: apenas o aluno que realizou este empréstimo pode efetuar a devolução.");
        }

        // Evita devolução duplicada
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Este livro já foi devolvido anteriormente.");
        }

        // Atualiza status e data de devolução
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        // Devolve o exemplar ao estoque disponível
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Loan updatedLoan = loanRepository.save(loan);

        return new LoanResponse(updatedLoan, "Livro devolvido com sucesso!");
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findUserLoans(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        return loanRepository.findByUserIdOrderByLoanDateDesc(userId).stream()
                .map(loan -> new LoanResponse(loan, null))
                .toList();
    }
}
