package com.mserv.hexagonal_users.application.usecase;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;

import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserUseCase {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserUseCase(UserRepository userRepository, AuthService authService, UserMapper userMapper, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public boolean isRegisterEmail(String email){
        return userRepository.existsByEmail(email);
    }


    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {

        // Validación: podrías mover esto a una clase validator en el futuro
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("El correo ya está registrado: " + userRequestDTO.getEmail());
        }

        // Mapeo de entrada
        UserEntity userEntity = userMapper.fromRequestDTO(userRequestDTO);

        userEntity.setCreated(LocalDateTime.now());
        userEntity.setModified(LocalDateTime.now());
        userEntity.setLastLogin(LocalDateTime.now());
        userEntity.setActive(true);
        userEntity.setToken(jwtUtil.generateToken(userEntity.getEmail()));

        // Mapeo a dominio y persistencia
        User user = userMapper.toDomain(userEntity);
        User savedUser = userRepository.save(user);

        // Mapeo de respuesta
        UserResponseDTO responseDTO = userMapper.toResponseDTO(savedUser);
        responseDTO.setToken(userEntity.getToken());

        return responseDTO;
    }


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
