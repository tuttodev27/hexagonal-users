package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
public class UserMapper {


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

        if (user.getPhones() != null) {
            List<PhoneEntity> phoneEntities = user.getPhones().stream()
                    .map(phone -> PhoneEntityMapper.toEntity(phone, userEntity))
                    .collect(Collectors.toList());
            userEntity.setPhones(phoneEntities);
        }

        return userEntity;
    }

   public static UserEntity fromRequestDTO(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequestDTO.getName());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(userRequestDTO.getPassword());

        List<Phone> phoneList = userRequestDTO.getPhones().stream()
                .map(phoneRequestDTO -> PhoneMapper.toDomain(phoneRequestDTO))
                .collect(Collectors.toList());

        userEntity.prePersist();

        return userEntity;
    }
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhones().stream()
                        .map(PhoneMapper::toResponseDTO)  // Convertir teléfonos a response DTO
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }
        public static User toDomain(UserEntity userEntity) {
            if (userEntity == null) {
                return null;
            }

            User user = new User();
            user.setId(userEntity.getId());
            user.setName(userEntity.getName());
            user.setEmail(userEntity.getEmail());
            user.setPassword(userEntity.getPassword());
            user.setCreated(userEntity.getCreated());
            user.setModified(userEntity.getModified());
            user.setLastLogin(userEntity.getLastLogin());
            user.setToken(userEntity.getToken());
            user.setActive(userEntity.isActive());

            if (userEntity.getPhones() != null) {
                List<Phone> phones = userEntity.getPhones().stream()
                        .map(PhoneEntityMapper::toDomain)
                        .collect(Collectors.toList());
                user.setPhones(phones);
            }

            return user;
        }

    }
