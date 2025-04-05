package com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;

public class PhoneEntityMapper {


    public static PhoneEntity toEntity(Phone phone, UserEntity userEntity) {
        if (phone == null) {
            return null;
        }

        PhoneEntity phoneEntity = new PhoneEntity();

        phoneEntity.setNumber(phone.getNumber());
        phoneEntity.setCityCode(phone.getCityCode());
        phoneEntity.setCountryCode(phone.getCountryCode());
        phoneEntity.setUser(userEntity); // Relacionamos con el UserEntity

        return phoneEntity;
    }

    // Método para convertir de PhoneEntity a Phone
    public static Phone toDomain(PhoneEntity phoneEntity) {
        if (phoneEntity == null) {
            return null;
        }

        return new Phone(

                phoneEntity.getCityCode(),
                phoneEntity.getNumber(),
                phoneEntity.getCountryCode()
        );
    }
}
