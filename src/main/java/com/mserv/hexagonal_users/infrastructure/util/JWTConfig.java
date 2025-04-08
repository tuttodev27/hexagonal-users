package com.mserv.hexagonal_users.infrastructure.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JWTConfig {

    private String secret;
    private long expiration;
    @PostConstruct
    public void validate() {
        System.out.println("Clave secreta cargada: " + secret);

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("El secreto JWT no puede estar vacío.");
        }
        if (expiration <= 0) {
            throw new IllegalArgumentException("El tiempo de expiración JWT debe ser mayor a 0.");
        }
    }

}


