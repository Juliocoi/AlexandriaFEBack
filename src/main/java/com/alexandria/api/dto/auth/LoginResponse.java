package com.alexandria.api.dto.auth;

import com.alexandria.api.dto.user.UserResponse;

public record LoginResponse(
    String token,
    UserResponse user
) {}
