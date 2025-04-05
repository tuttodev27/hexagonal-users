package com.mserv.hexagonal_users.application.usecase;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserUseCase(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public User execute(User user) {
        // Verificar si el correo ya está registrado
        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistsException("El correo ya está registrado");
        });

        // Asignar un nuevo ID y las fechas de creación y modificación
        user.setId(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        // Generar y asignar el token
        String token = authService.generateToken(user.getEmail());
        user.setToken(token);
        user.setActive(true);

        // Persistir el usuario
        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    public User updateUser(User user) {
        getUserById(user.getId());  // Primero buscamos el usuario por ID
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }
   public void deleteUser(UUID id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
    public long countUsers() {
        return userRepository.count();
    }
}
