package com.alexandria.api.service;

import com.alexandria.api.domain.loan.LoanStatus;
import com.alexandria.api.domain.user.User;
import com.alexandria.api.domain.user.UserRole;
import com.alexandria.api.dto.user.CreateUserRequest;
import com.alexandria.api.dto.user.RegisterStudentRequest;
import com.alexandria.api.dto.user.UpdateUserRequest;
import com.alexandria.api.dto.user.UserResponse;
import com.alexandria.api.repository.LoanRepository;
import com.alexandria.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse registerStudent(RegisterStudentRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // Criptografa a senha antes de salvar
        user.setBirthDate(request.birthDate());
        user.setMotherName(request.motherName());
        user.setFatherName(request.fatherName());
        user.setRole(UserRole.STUDENT); // Garante que auto-cadastro seja sempre Aluno

        return new UserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse createByStaff(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setBirthDate(request.birthDate());
        user.setMotherName(request.motherName());
        user.setFatherName(request.fatherName());
        user.setRole(request.role());

        return new UserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("O novo e-mail informado já está em uso por outro usuário.");
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setBirthDate(request.birthDate());
        user.setMotherName(request.motherName());
        user.setFatherName(request.fatherName());

        // Só atualiza a senha se uma nova foi enviada na requisição
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return new UserResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        if (loanRepository.existsByUserIdAndStatus(id, LoanStatus.ACTIVE)) {
            throw new IllegalStateException(
                "Não é possível excluir este usuário pois ele possui empréstimos ativos pendentes de devolução."
            );
        }

        userRepository.deleteById(id);
    }

}
