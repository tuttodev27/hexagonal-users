package com.mserv.hexagonal_users.infrastructure.web;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserUseCase userUseCase;

    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_Success() throws UserAlreadyExistsException {
        // Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setName("Test User");

        when(userUseCase.registerUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        ResponseEntity<?> response = userController.registerUser(userRequestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userResponseDTO, response.getBody());
    }
}