package com.mserv.hexagonal_users.infrastructure.config;


import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.getAllClaimsFromToken(token);
                    String username = claims.getSubject();

                    ArrayList<String> roles = claims.get("roles", ArrayList.class);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    respondWithUnauthorized(response, "Token inválido o expirado.");
                    return;
                }
            } catch (ExpiredJwtException e) {
                respondWithUnauthorized(response, "Token expirado.");
                return;
            } catch (UnsupportedJwtException e) {
                respondWithUnauthorized(response, "Token no soportado.");
                return;
            } catch (MalformedJwtException e) {
                respondWithUnauthorized(response, "Token mal formado.");
                return;
            } catch (Exception e) {
                respondWithUnauthorized(response, "Error al validar el token.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"mensaje\": \"%s\"}", message));
    }

}

