package com.mserv.hexagonal_users.domain.port;

import com.mserv.hexagonal_users.domain.model.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> authenticate(String username, String password);

    String generateToken(String email);
    boolean validateToken(String token);
    Optional<String> getUserFromToken(String token);

}
