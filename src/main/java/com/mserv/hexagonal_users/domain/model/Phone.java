package com.mserv.hexagonal_users.domain.model;

import java.util.UUID;

public class Phone {
    private UUID id;
    private String number;
    private String cityCode;
    private String cityCountry;

    public Phone(UUID id, String cityCode, String number, String cityCountry) {
        this.id = id;
        this.cityCode = cityCode;
        this.number = number;
        this.cityCountry = cityCountry;
    }

    public Phone() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCountry() {
        return cityCountry;
    }

    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }
}




