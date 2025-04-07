package com.mserv.hexagonal_users.infrastructure.web;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.LoginRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.LoginResponseDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.exception.ErrorResponse;
import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserUseCase userUseCase;

    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;

    public UserController(UserUseCase userUseCase, JWTUtil jwtUtil, UserMapper userMapper) {
        this.userUseCase = userUseCase;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {

            try {

                UserEntity userEntity = userMapper.fromRequestDTO(userRequestDTO);
                userEntity.setCreated(LocalDateTime.now());
                userEntity.setModified(LocalDateTime.now());
                userEntity.setLastLogin(LocalDateTime.now());
                String token = jwtUtil.generateToken(userEntity.getEmail());
                userEntity.setToken(token);
                userEntity.setActive(true);

                User user = userMapper.toDomain(userEntity);
                User savedUser = userUseCase.createdUser(user);

                UserResponseDTO responseDTO = userMapper.toResponseDTO(savedUser);
                responseDTO.setToken(token);

                return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            } catch (UserAlreadyExistsException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("400", e.getMessage(), "Correo duplicado", "ERR_DUPLICATE_EMAIL"));
            } catch (Exception e) {
                log.error("Error al registrar usuario: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("500", "Ocurrió un error al registrar el usuario. Intente nuevamente más tarde.", e.getMessage(), "ERR_INTERNAL_SERVER"));
            }
        }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {

            User user = userUseCase.getUserByEmail(loginRequest.getEmail());
            if (!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("401", "Credenciales inválidas", "El correo o la contraseña no son correctos", "ERR_INVALID_CREDENTIALS"));
            }
            String token = jwtUtil.generateToken(user.getEmail());
            UserResponseDTO userDTO = userMapper.toResponseDTO(user);
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setUser(userDTO);

            return ResponseEntity.ok(response);

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("401", e.getMessage(), "Usuario no registrado", "ERR_USER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error durante login: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("500", "Ocurrió un error al intentar iniciar sesión.", e.getMessage(), "ERR_INTERNAL_LOGIN"));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userUseCase.getUserById(id);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(user);
            return ResponseEntity.ok(responseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {

            UserEntity userEntity = userMapper.fromRequestDTO(userRequestDTO);
            userEntity.setId(id);

            User user = userMapper.toDomain(userEntity);
            User updatedUser = userUseCase.updateUser(user);

            UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
            return ResponseEntity.ok(responseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userUseCase.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = (List<User>) userUseCase.getAllUsers();
        List<UserResponseDTO> response = users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        long count = userUseCase.countUsers();
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserPartial(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User existingUser = userUseCase.getUserById(id);
            if (userRequestDTO.getEmail() != null) {
                existingUser.setEmail(userRequestDTO.getEmail());
            }
            if (userRequestDTO.getName() != null) {
                existingUser.setName(userRequestDTO.getName());
            }
            if (userRequestDTO.getPassword() != null) {
                existingUser.setPassword(userRequestDTO.getPassword());
            }

            User updatedUser = userUseCase.updateUser(existingUser);
            UserResponseDTO responseDTO = userMapper.toResponseDTO(updatedUser);
            return ResponseEntity.ok(responseDTO);

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error al actualizar parcialmente el usuario: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("500", "Ocurrió un error al actualizar el usuario. Intente nuevamente más tarde.", e.getMessage(), "ERR_INTERNAL_SERVER"));
        }
    }
}
