package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
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

        // Asignar los teléfonos correctamente
        if (user.getPhones() != null) {
            List<PhoneEntity> phoneEntities = user.getPhones().stream()
                    .map(phone -> PhoneEntityMapper.toEntity(phone, userEntity))  // Aseguramos que el teléfono tenga su usuario asociado
                    .collect(Collectors.toList());
            userEntity.setPhones(phoneEntities);
        }

        return userEntity;
    }


    // En UserMapper.toDomain
    public static UserEntity fromRequestDTO(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        // Mapear las propiedades de UserRequestDTO a UserEntity
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequestDTO.getName());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(userRequestDTO.getPassword());

        List<Phone> phoneList = userRequestDTO.getPhones().stream()
                .map(phoneRequestDTO -> PhoneMapper.toDomain(phoneRequestDTO))  // Usando lambda en lugar de method reference
                .collect(Collectors.toList());


        // Inicializa las fechas antes de persistir
        userEntity.prePersist();

        return userEntity;
    }
    // Conversion from User to UserResponseDTO
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

            // Map phones from UserEntity to User
            if (userEntity.getPhones() != null) {
                List<Phone> phones = userEntity.getPhones().stream()
                        .map(PhoneEntityMapper::toDomain)  // Assuming you have a method to convert PhoneEntity to Phone
                        .collect(Collectors.toList());
                user.setPhones(phones);
            }

            return user;
        }

    }
