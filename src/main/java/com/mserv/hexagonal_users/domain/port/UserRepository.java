package com.mserv.hexagonal_users.domain.port;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;


import java.util.List;
import java.util.Optional;

public interface UserRepository{


    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(Long id);
    List<User> findAll();
    User update(User user);
    long count();
}
