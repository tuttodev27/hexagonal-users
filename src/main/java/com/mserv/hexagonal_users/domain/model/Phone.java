package com.mserv.hexagonal_users.domain.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Phone {


    private String number;
    private String cityCode;
    private String countryCode;

    public Phone(String cityCode, String number, String countryCode) {

        this.cityCode = cityCode;
        this.number = number;
        this.countryCode = countryCode;
    }

    public Phone() {
    }


}
