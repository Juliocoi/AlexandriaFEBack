package com.alexandria.api.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
    int status,
    String error,
    String message,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime timestamp,
    Map<String, String> fieldErrors // Usado quando houver erro de validação (@Valid) nos campos
) {
    public ApiErrorResponse(int status, String error, String message) {
        this(status, error, message, LocalDateTime.now(), null);
    }
}
