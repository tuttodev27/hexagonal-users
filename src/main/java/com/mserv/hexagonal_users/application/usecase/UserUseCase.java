package com.mserv.hexagonal_users.application.usecase;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final AuthService authService;


    public UserUseCase(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }
    public User execute(User user){
        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser ->{
            throw new UserAlreadyExistsException("El correo ya existe");
        });
        user.setId(UUID.randomUUID());
        LocalDateTime now= LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        String token= authService.generateToken(user.getEmail());
        user.setToken(token);
        user.setActive(true);

        return userRepository.save(user);
    }
    public User getUserById (UUID id){
        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("Usuario no encontrado con ID " + id));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("Usuario no encontrado con el email: " + email));
    }

    public User updateUser(User user){
        if(!userRepository.existsByEmail(user.getEmail())){
            throw new UserNotFoundException("Usuario no encontrado con el email: " + user.getEmail());
        }
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {

        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

   public long countUsers() {
        return userRepository.count();
    }

}
