package com.mserv.hexagonal_users.infrastructure.adapter.persistence.mapper;

import com.mserv.hexagonal_users.domain.model.Phone;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.PhoneEntity;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;

public class PhoneEntityMapper {

    public static Phone toDomain(PhoneEntity phoneEntity) {
        if (phoneEntity == null) {
            return null;
        }

        return new Phone(
                phoneEntity.getId(),
                phoneEntity.getNumber(),
                phoneEntity.getCityCode(),
                phoneEntity.getCountryCode()
        );
    }

    public static PhoneEntity toEntity(Phone phone, UserEntity userEntity) {
        if (phone == null) {
            return null;
        }
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setNumber(phone.getNumber());
        phoneEntity.setCityCode(phone.getCityCode());
        phoneEntity.setCountryCode(phone.getCountryCode());
        phoneEntity.setUser(userEntity);

        return phoneEntity;
    }
}
