package com.alexandria.api.dto.user;

import com.alexandria.api.domain.user.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
//Cadastro administrativo de usuário feito por um funcionário) 
public record CreateUserRequest(
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

    String fatherName,

    @NotNull(message = "O perfil de acesso (role) é obrigatório.")
    UserRole role
) {}
