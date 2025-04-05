package com.mserv.hexagonal_users.domain.model;

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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}




