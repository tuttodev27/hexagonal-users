package com.mserv.hexagonal_users.domain.port;

public interface AuthService {
    String generateToken(String email);
    boolean validateToken(String token);
}
