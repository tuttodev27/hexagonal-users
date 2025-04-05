package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import jakarta.validation.Valid;

import java.util.stream.Collectors;

public class UserMapper {

    // Conversion from User to UserEntity
    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setCreated(user.getCreated());
        userEntity.setModified(user.getModified());
        userEntity.setLastLogin(user.getLastLogin());
        userEntity.setToken(user.getToken());
        userEntity.setActive(user.isActive());

        userEntity.setPhones(user.getPhones().stream()
                .map(phone -> PhoneEntityMapper.toEntity(phone, userEntity))
                .collect(Collectors.toList()));

        return userEntity;
    }

    // Conversion from UserEntity to User
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getPhones().stream()
                        .map(phoneEntity -> PhoneEntityMapper.toDomain(phoneEntity))
                        .collect(Collectors.toList()),
                userEntity.getCreated(),
                userEntity.getModified(),
                userEntity.getLastLogin(),
                userEntity.getToken(),
                userEntity.isActive()
        );
    }

    // Conversion from UserRequestDTO to User
    public static User fromRequestDTO(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        return new User(
                null,  // ID will be set by the database or service layer
                userRequestDTO.getName(),
                userRequestDTO.getEmail(),
                userRequestDTO.getPassword(),
                userRequestDTO.getPhones().stream()
                        .map(PhoneMapper::toDomain)  // Convert phones
                        .collect(Collectors.toList()),
                null,  // Created date can be managed elsewhere
                null,  // Modified date can be managed elsewhere
                null,  // Last login can be handled by the service layer
                null,  // Token can be set elsewhere
                true   // Assume active by default, but this can be adjusted as needed
        );
    }

    // Conversion from User to UserRequestDTO
    public static UserRequestDTO toRequestDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserRequestDTO(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhones().stream()
                        .map(PhoneMapper::toRequestDTO)  // Convert phones to request DTO
                        .collect(Collectors.toList())
        );
    }

    // Conversion from User to UserResponseDTO
    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhones().stream()
                        .map(PhoneMapper::toResponseDTO)  // Convert phones to response DTO
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }


}
