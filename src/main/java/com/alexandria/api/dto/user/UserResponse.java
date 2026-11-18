package com.alexandria.api.dto.user;

import com.alexandria.api.domain.user.User;
import com.alexandria.api.domain.user.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String name,
    String email,
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,
    String motherName,
    String fatherName,
    UserRole role
) {
    public UserResponse(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getBirthDate(),
            user.getMotherName(),
            user.getFatherName(),
            user.getRole()
        );
    }
}
