package com.mserv.hexagonal_users.infrastructure.web;

import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        // Cambié fromDTO por fromRequestDTO
        User user = UserMapper.fromRequestDTO(userRequestDTO); // Llamado estático correctamente
        User saveUser = userUseCase.execute(user);
        UserResponseDTO responseDTO = UserMapper.toResponseDTO(saveUser); // Llamado estático correctamente
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        User user = userUseCase.getUserById(id);
        UserResponseDTO responseDTO = UserMapper.toResponseDTO(user); // Llamado estático correctamente
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email){
        User user = userUseCase.getUserByEmail(email);
        UserResponseDTO responseDTO = UserMapper.toResponseDTO(user); // Llamado estático correctamente
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/id")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequestDTO userRequestDTO){
        // Cambié fromDTO por fromRequestDTO
        User user = UserMapper.fromRequestDTO(userRequestDTO); // Llamado estático correctamente
        user.setId(id);
        User updateUser = userUseCase.updateUser(user);
        UserResponseDTO responseDTO = UserMapper.toResponseDTO(updateUser); // Llamado estático correctamente
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = (List<User>) userUseCase.getAllUsers();
        List<UserResponseDTO> response = users.stream()
                .map(UserMapper::toResponseDTO) // Llamado estático correctamente
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countUsers() {
        long count = userUseCase.countUsers();
        return ResponseEntity.ok(count);
    }
}
