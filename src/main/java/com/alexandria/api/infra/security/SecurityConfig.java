package com.alexandria.api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // <-- Ativa o bean CorsConfigurationSource
                .csrf(AbstractHttpConfigurer::disable) // Desabilita proteção CSRF (não usada em APIs REST stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll() // <-- Evita falso 403 em falhas internas
                        // 1. Rotas Públicas (Login e Auto-cadastro de Aluno)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                        // 2. Rotas Exclusivas para Funcionários (STAFF)
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("STAFF")

                        // 3. Qualquer outra rota exige que o usuário esteja autenticado (Aluno ou Funcionário)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Bean gerenciado pelo Spring para criptografar e validar senhas usando o algoritmo BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
