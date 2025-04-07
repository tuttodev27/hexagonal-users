package com.mserv.hexagonal_users.infrastructure.adapter.persistence;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        // Creamos y devolvemos la entidad UserEntity
        UserEntity userEntity= UserEntity.builder()
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

        List<PhoneEntity> phoneEntities = user.getPhones() != null
                ? user.getPhones().stream()
                .map(phone -> PhoneEntityMapper.toEntity(phone, userEntity))
                .collect(Collectors.toList())
                : new ArrayList<>();

        userEntity.setPhones(phoneEntities);

        return userEntity;

    }




    private User toDomain(UserEntity userEntity) {
        return new User(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.getCreated(),
            userEntity.getLastLogin(),
            userEntity.getModified(),
            userEntity.getToken(),
            userEntity.isActive(),
            userEntity.getPhones() != null ? userEntity.getPhones().stream()
                    .map(PhoneEntityMapper::toDomain)
                    .collect(Collectors.toList()) : new ArrayList<>()
    );
}



    @Override
    public User save(User user) {

        if (jpaUserRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Ya existe un usuario con este correo");
        }

        if (user.getId() == null) {
            user.setId(System.currentTimeMillis());
        }
        UserEntity userEntity = toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(userEntity);
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
