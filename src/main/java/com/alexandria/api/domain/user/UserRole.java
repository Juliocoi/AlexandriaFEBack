package com.alexandria.api.domain.user;

public enum UserRole {
    STUDENT("student"),
    STAFF("staff");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
