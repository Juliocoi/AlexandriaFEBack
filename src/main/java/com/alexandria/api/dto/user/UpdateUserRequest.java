package com.alexandria.api.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserRequest(
    @NotBlank(message = "O nome completo é obrigatório.")
    String name,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
    String password, // Opcional na edição: só altera se o usuário enviar uma nova senha

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve estar no passado.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @NotBlank(message = "O nome da mãe é obrigatório.")
    String motherName,

    String fatherName
) {}
