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
    private UserController userController; // Controlador que vamos a probar

    @Mock
    private UserUseCase userUseCase; // Simulación de UserUseCase

    @Mock
    private JWTUtil jwtUtil; // Simulación de JWTUtil

    @Mock
    private UserMapper userMapper; // Simulación de UserMapper

    @Mock
    private UserResponseDTO userResponseDTO; // DTO de respuesta

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    public void registerUser_Success() throws UserAlreadyExistsException {
        // Arrange
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setName("Test User");

        when(userUseCase.registerUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO); // Simulamos que el método registerUser devuelve un DTO

        // Act
        ResponseEntity<?> response = userController.registerUser(userRequestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Verificamos que el estado sea 201 (CREATED)
        assertEquals(userResponseDTO, response.getBody()); // Verificamos que el cuerpo de la respuesta sea el DTO esperado
    }
}