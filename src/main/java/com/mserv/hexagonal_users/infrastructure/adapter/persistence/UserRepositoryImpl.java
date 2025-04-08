package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    private UserEntity toEntity(User user) {
        UserEntity userEntity = UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .active(user.isActive())
                .build();

        // Verificar si la lista de teléfonos es null, y si es así, asignar una lista vacía
        if (user.getPhones() != null && !user.getPhones().isEmpty()) {
            List<PhoneEntity> phoneEntities = user.getPhones().stream()
                    .map(phone -> PhoneEntity.builder()
                            .number(phone.getNumber())
                            .cityCode(phone.getCityCode())
                            .countryCode(phone.getCountryCode())
                            .user(userEntity)
                            .build())
                    .collect(Collectors.toList());
            userEntity.setPhones(phoneEntities);
        } else {
            userEntity.setPhones(Collections.emptyList()); // Asignar lista vacía si es null
        }

        System.out.println("Teléfonos durante la conversión a entidad: " + userEntity.getPhones()); // Verificación

        return userEntity;
    }



    private User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),  // Long
                userEntity.getPhones() != null ? userEntity.getPhones().stream()
                        .map(PhoneEntityMapper::toDomain)
                        .collect(Collectors.toList()) : new ArrayList<>(), // List<Phone>
                userEntity.isActive(),  // boolean
                userEntity.getLastLogin(),  // LocalDateTime
                userEntity.getToken(),  // String
                userEntity.getModified(),  // LocalDateTime
                userEntity.getPassword(),  // String
                userEntity.getCreated(),  // LocalDateTime
                userEntity.getEmail(),  // String
                userEntity.getName()  // String
        );
    }
    @Transactional
    @Override
    public User save(User user) {

        if (jpaUserRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con este correo");
        }

        UserEntity userEntity = toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
        System.out.println("Teléfonos antes de guardar: " + userEntity.getPhones());
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
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
        UserEntity updatedEntity = jpaUserRepository.save(toEntity(user));
        return toDomain(updatedEntity);
    }

    @Override
    public long count() {
        return jpaUserRepository.count();
    }
}