package com.mserv.hexagonal_users.domain.port;

import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.JpaUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository{
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(UUID id);
    List<User> findAll();
    User update(User user);
    long count();
}
