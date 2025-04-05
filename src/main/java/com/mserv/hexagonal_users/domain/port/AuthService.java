package com.mserv.hexagonal_users.domain.port;

import java.util.Optional;

public interface AuthService {
    String generateToken(String email);
    boolean validateToken(String token);
    Optional<String> getUserFromToken(String token);
    void invalidateToken(String token);
}
