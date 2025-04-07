package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PhoneMapper {

    public static PhoneRequestDTO toRequestDTO(Phone phone) {
        if (phone == null) {
            return null;
        }

        return new PhoneRequestDTO(
                phone.getCityCode(),
                phone.getNumber(),
                phone.getCountryCode()
        );
    }
    public static PhoneResponseDTO toResponseDTO(Phone phone) {
        if (phone == null) {
            return null;
        }

        return PhoneResponseDTO.builder()
                .number(phone.getNumber())
                .cityCode(phone.getCityCode())
                .countryCode(phone.getCountryCode())
                .build();
    }
    public static Phone toDomain(PhoneRequestDTO phoneRequestDTO) {
        if (phoneRequestDTO == null) {
            return null;
        }
        Phone phone= new Phone();
        phone.setNumber(phoneRequestDTO.getNumber());
        phone.setCityCode(phoneRequestDTO.getCityCode());
        phone.setCountryCode(phoneRequestDTO.getCountryCode());

        return phone;
    }
    public static PhoneEntity toEntity(Phone phone, UserEntity userEntity) {
        if (phone == null) {
            return null;
        }

        PhoneEntity phoneEntity = new PhoneEntity();

        phoneEntity.setNumber(phone.getNumber());
        phoneEntity.setCityCode(phone.getCityCode());

        // Verificar si countryCode es null y asignar valor predeterminado si es necesario
        phoneEntity.setCountryCode(phone.getCountryCode() != null ? phone.getCountryCode() : "defaultCountryCode");  // Cambia "defaultCountryCode" por el valor que necesites

        phoneEntity.setUser(userEntity);  // Asociar el usuario

        return phoneEntity;
    }


}