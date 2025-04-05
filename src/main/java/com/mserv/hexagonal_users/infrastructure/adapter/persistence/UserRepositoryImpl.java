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
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.isActive())
                .build();
    }

    private User toDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .created(userEntity.getCreated())
                .modified(userEntity.getModified())
                .lastLogin(userEntity.getLastLogin())
                .token(userEntity.getToken())
                .isActive(userEntity.isActive())
                .build();
    }

    @Override
    public User save(User user) {
        if (jpaUserRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con este correo");
        }

        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        UserEntity savedEntity = jpaUserRepository.save(toEntity(user));
        return toDomain(savedEntity);
    }
    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
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
        List<UserEntity> userEntities = jpaUserRepository.findAll();
        return userEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User update(User user) {
        if (!jpaUserRepository.existsById(user.getId())) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + user.getId());
        }

        user.setModified(LocalDateTime.now());
        UserEntity updatedEntity = jpaUserRepository.save(toEntity(user)); // Guardar UserEntity
        return toDomain(updatedEntity);
    }

    @Override
    public long count() {
        return jpaUserRepository.count();
    }
}
