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

import java.util.UUID;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    public UserController(UserUseCase userUseCase, UserMapper userMapper) {
        this.userUseCase = userUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        User user= userMapper.userToDomain(userRequestDTO);
        User saveUser= userUseCase.execute(user);
        UserResponseDTO responseDTO= userMapper.domainToUserResponse(saveUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        User user = userUseCase.getUserById(id);
        UserResponseDTO responseDTO = userMapper.domainToUserResponse(user);
        return ResponseEntity.ok(responseDTO);
    }
}
