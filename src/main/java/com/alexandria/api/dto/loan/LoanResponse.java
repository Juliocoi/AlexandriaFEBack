package com.alexandria.api.dto.loan;

import com.alexandria.api.domain.loan.Loan;
import com.alexandria.api.domain.loan.LoanStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record LoanResponse(
    UUID id,
    UUID userId,
    String userName,
    UUID bookId,
    String bookTitle,
    String bookAuthor,
    String bookCoverUrl,
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate loanDate,
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dueDate,
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate returnDate,
    LoanStatus status,
    String message
) {
    public LoanResponse(Loan loan, String message) {
        this(
            loan.getId(),
            loan.getUser().getId(),
            loan.getUser().getName(),
            loan.getBook().getId(),
            loan.getBook().getTitle(),
            loan.getBook().getAuthor(),
            loan.getBook().getCoverUrl(),
            loan.getLoanDate(),
            loan.getDueDate(),
            loan.getReturnDate(),
            loan.getStatus(),
            message
        );
    }
}
