package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {

        if (jpaUserRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con este correo");
        }

        // Si el ID es nulo, generar un nuevo UUID
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }

        return jpaUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public User update(User user) {

        if (!jpaUserRepository.existsById(user.getId())) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + user.getId());
        }


        user.setModified(LocalDateTime.now());
        return jpaUserRepository.save(user);
    }

    @Override
    public long count() {
        return jpaUserRepository.count();
    }
}
