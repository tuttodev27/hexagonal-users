package com.mserv.hexagonal_users.application.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@Getter
@Setter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    private String errorCode;
    public UserNotFoundException(String message){
        super(message);
    }
}
