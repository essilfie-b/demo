package com.example.demo.repository;

import com.example.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationCode(String code);
    Optional<User> findByResetToken(String token);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void delete(User user);
    List<User> findAll();
}