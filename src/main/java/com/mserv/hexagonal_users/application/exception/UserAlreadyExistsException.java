package com.mserv.hexagonal_users.application.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException (String message){
        super(message);
    }
}
