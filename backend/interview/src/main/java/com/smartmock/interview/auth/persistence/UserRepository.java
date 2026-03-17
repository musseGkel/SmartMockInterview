package com.smartmock.interview.auth.persistence;

import com.smartmock.interview.auth.domain.UserAccount;

import java.util.Optional;

public interface UserRepository {
    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(String id);

    UserAccount save(UserAccount user);
}