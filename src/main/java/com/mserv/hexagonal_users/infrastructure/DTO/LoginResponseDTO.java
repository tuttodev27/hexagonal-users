
package com.mserv.hexagonal_users.infrastructure.DTO;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private UserResponseDTO user;
}