package com.mserv.hexagonal_users.application.mappers;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;

public class PhoneMapper {
    public static PhoneEntity toEntity(Phone phone, UserEntity userEntity) {
        if (phone == null) {
            return null;
        }

        PhoneEntity phoneEntity = PhoneEntity.builder()
                .number(phone.getNumber())
                .cityCode(phone.getCityCode())
                .countryCode(phone.getCountryCode())
                .user(userEntity)
                .build();

        return phoneEntity;
    }


    // Convertir de Phone a PhoneRequestDTO
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

    // Convertir de Phone a PhoneResponseDTO
    public static PhoneResponseDTO toResponseDTO(Phone phone) {
        if (phone == null) {
            return null;
        }

        return new PhoneResponseDTO(
                phone.getNumber(),
                phone.getCityCode(),
                phone.getCountryCode()
        );
    }

    // Convertir de PhoneRequestDTO a Phone
    public static Phone toDomain(PhoneRequestDTO phoneRequestDTO) {
        if (phoneRequestDTO == null) {
            return null;
        }

        return new Phone(
                phoneRequestDTO.getCityCode(),
                phoneRequestDTO.getNumber(),
                phoneRequestDTO.getCountryCode()
        );
    }
}
