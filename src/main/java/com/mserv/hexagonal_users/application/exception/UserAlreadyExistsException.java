package com.mserv.hexagonal_users.application.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {
    private String errorCode;

    public UserAlreadyExistsException (String message){
        super(message);
    }


}
