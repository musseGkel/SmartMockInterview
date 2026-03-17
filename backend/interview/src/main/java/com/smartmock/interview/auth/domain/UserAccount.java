package com.smartmock.interview.auth.domain;

import java.util.UUID;

public class UserAccount {
    private final String id;
    private final String email;
    private final String passwordHash;
    private final String role;

    public UserAccount(String email, String passwordHash, String role) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}