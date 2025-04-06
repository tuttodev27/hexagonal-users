package com.mserv.hexagonal_users.infrastructure.web;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.exception.ErrorResponse;
import com.mserv.hexagonal_users.infrastructure.util.JWTConfig;
import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserUseCase userUseCase;
    private final JWTUtil jwtUtil;

    public UserController(UserUseCase userUseCase, JWTUtil jwtUtil) {
        this.userUseCase = userUseCase;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User user = UserMapper.fromRequestDTO(userRequestDTO);
            User saveUser = userUseCase.execute(user);

            String token = jwtUtil.generateToken(saveUser.getEmail());
            UserResponseDTO responseDTO = UserMapper.toResponseDTO(saveUser);
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            User user = userUseCase.getUserById(id);
            UserResponseDTO responseDTO = UserMapper.toResponseDTO(user);
            return ResponseEntity.ok(responseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User user = UserMapper.fromRequestDTO(userRequestDTO);
            user.setId(id);
            User updatedUser = userUseCase.updateUser(user);
            UserResponseDTO responseDTO = UserMapper.toResponseDTO(updatedUser);
            return ResponseEntity.ok(responseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("404", e.getMessage(), "Usuario no encontrado", "ERR_USER_NOT_FOUND"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
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
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        long count = userUseCase.countUsers();
        return ResponseEntity.ok(count);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserPartial(@PathVariable UUID id, @RequestBody UserRequestDTO userRequestDTO) {
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
            UserResponseDTO responseDTO = UserMapper.toResponseDTO(updatedUser);
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