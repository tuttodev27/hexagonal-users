package com.mserv.hexagonal_users.infrastructure.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class JWTUtil {

    private static final Logger LOGGER = Logger.getLogger(JWTUtil.class.getName());
    private final JWTConfig jwtConfig;
    private final SecretKey signingKey;

    public JWTUtil(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.warning("Token expirado");
        } catch (MalformedJwtException e) {
            LOGGER.warning("Token mal formado");
        } catch (UnsupportedJwtException e) {
            LOGGER.warning("Token no soportado");
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Token vacío o nulo");
        }
        return false;
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
