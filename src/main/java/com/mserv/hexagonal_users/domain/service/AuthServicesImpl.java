package com.mserv.hexagonal_users.domain.service;

import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import com.mserv.hexagonal_users.infrastructure.util.JWTConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class AuthServicesImpl implements AuthService {
    @Value("${jwt.secret}")
    private String secretKey;

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
        // Verificar que la clave secreta no sea nula ni vacía
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("La clave secreta no puede ser nula o vacía");
        }

        // Crear la clave de firma con la clave secreta
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // Generar el token JWT con el correo electrónico como subject
        return Jwts.builder()
                .setSubject(email) // Usamos el correo electrónico como el subject
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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

