package com.mserv.hexagonal_users.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
    private String details;
    private String errorCode;
}
