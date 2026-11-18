package com.alexandria.api.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

//auto-cadastro do aluno via sing up page(sem necessidade de aprovação do administrador)
public record RegisterStudentRequest(
    @NotBlank(message = "O nome completo é obrigatório.")
    String name,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    String password,

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve estar no passado.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @NotBlank(message = "O nome da mãe é obrigatório.")
    String motherName,

    String fatherName // Opcional (pode ser nulo)
) {}
