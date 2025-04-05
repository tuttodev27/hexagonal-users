package com.mserv.hexagonal_users.domain.service;

import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import com.mserv.hexagonal_users.infrastructure.util.JWTConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class AuthServicesImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JWTConfig jwtConfig;

    public AuthServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public String generateToken(String email) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(expirationDate).
                signWith(SignatureAlgorithm.HS256, String.valueOf(SECRET_KEY)).compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtConfig.getSecret());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<String> getUserFromToken(String token) {
        try {
            String username = Jwts.parser().setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token).getBody().getSubject();

            return Optional.of(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}

