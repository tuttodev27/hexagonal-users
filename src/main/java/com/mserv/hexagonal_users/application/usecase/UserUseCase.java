package com.mserv.hexagonal_users.application.usecase;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserUseCase {
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public UserUseCase(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public boolean isRegisterEmail(String email){
        return userRepository.existsByEmail(email);
    }


    public User createdUser(User user) {

        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistsException("El correo ya está registrado");
        });


        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        String token = authService.generateToken(user.getEmail());
        user.setToken(token);
        user.setActive(true);
        user.setId(null);

        // Guarda el usuario
        return userRepository.save(user);
    }

    // Método para obtener un usuario por su ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Método para obtener un usuario por su correo electrónico
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    // Método para actualizar un usuario
    public User updateUser(User user) {
        // Verifica si el usuario existe en la base de datos
        User existingUser = getUserById(user.getId());
        user.setModified(LocalDateTime.now());

        // Asegúrate de que el campo version esté manejado por Hibernate
        // Si no se ha modificado, Hibernate gestionará la versión de manera automática

        return userRepository.save(user);
    }

    // Método para eliminar un usuario
    public void deleteUser(Long id) {
        // Verifica que el usuario exista antes de eliminarlo
        getUserById(id);
        userRepository.deleteById(id);
    }

    // Método para obtener todos los usuarios
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Método para contar todos los usuarios
    public long countUsers() {
        return userRepository.count();
    }
}
