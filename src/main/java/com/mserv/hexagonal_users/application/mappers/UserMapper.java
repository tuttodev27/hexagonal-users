package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper.PhoneEntityMapper;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;

import java.util.ArrayList;
import java.util.List;
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
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getPhones() != null ?  // Asegúrate de que la lista no sea null
                        userEntity.getPhones().stream()
                                .map(PhoneEntityMapper::toDomain)
                                .collect(Collectors.toList()) :
                        new ArrayList<>(),  // Si no hay teléfonos, asigna una lista vacía
                userEntity.getCreated(),
                userEntity.getModified(),
                userEntity.getLastLogin(),
                userEntity.getToken(),
                userEntity.isActive()
        );
    }


    public static User fromRequestDTO(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            return null;
        }

        // Aseguramos que la lista de teléfonos no sea null ni vacía
        List<Phone> phoneList = userRequestDTO.getPhones() != null && !userRequestDTO.getPhones().isEmpty() ?
                userRequestDTO.getPhones().stream()
                        .map(PhoneMapper::toDomain)  // Convertir teléfonos
                        .collect(Collectors.toList()) :
                List.of();  // Si no hay teléfonos, asigna una lista vacía

        // Verificamos que los teléfonos sean correctamente mapeados
        if (phoneList.isEmpty()) {
            System.out.println("Advertencia: No se proporcionaron teléfonos, se asignará una lista vacía.");
        }

        return new User(
                null,  // ID lo establecerá la base de datos
                userRequestDTO.getName(),
                userRequestDTO.getEmail(),
                userRequestDTO.getPassword(),
                phoneList,  // Lista de teléfonos nunca será null ni vacía
                null,  // La fecha de creación puede gestionarse por separado
                null,  // La fecha de modificación también
                null,  // Último login se manejará en la capa de servicio
                null,  // El token se asignará en otro momento
                true   // Asumimos que el usuario está activo por defecto
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
                        .map(PhoneMapper::toResponseDTO)  // Convertir teléfonos a response DTO
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }
}
