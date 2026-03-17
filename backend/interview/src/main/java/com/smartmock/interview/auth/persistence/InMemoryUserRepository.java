package com.smartmock.interview.auth.persistence;

import com.smartmock.interview.auth.domain.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserAccount> byId = new ConcurrentHashMap<>();
    private final Map<String, UserAccount> byEmail = new ConcurrentHashMap<>();

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(byEmail.get(email.toLowerCase()));
    }

    @Override
    public Optional<UserAccount> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public UserAccount save(UserAccount user) {
        byId.put(user.getId(), user);
        byEmail.put(user.getEmail().toLowerCase(), user);
        return user;
    }
}