package com.mserv.hexagonal_users.infrastructure.adapter.persistence;


import com.mserv.hexagonal_users.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
