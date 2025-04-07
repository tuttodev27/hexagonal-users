package com.mserv.hexagonal_users.application.usecase;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
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
    public User execute(User user) {

        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistsException("El correo ya está registrado");
        });

        user.setId(System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        String token = authService.generateToken(user.getEmail());
        user.setToken(token);
        user.setActive(true);


        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("Usuario no encontrado con el correo: " + email));
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }
   public void deleteUser(Long id) {
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
