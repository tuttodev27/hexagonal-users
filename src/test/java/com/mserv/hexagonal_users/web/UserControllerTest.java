package com.mserv.hexagonal_users.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.infrastructure.DTO.LoginRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.PhoneRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserRequestDTO;
import com.mserv.hexagonal_users.infrastructure.DTO.UserResponseDTO;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import com.mserv.hexagonal_users.infrastructure.util.JWTUtil;
import com.mserv.hexagonal_users.infrastructure.web.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;



public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan");
        userRequestDTO.setEmail("juan@rodriguez.org");
        userRequestDTO.setPassword("password");

        UserEntity userEntity = new UserEntity();
        userEntity.setName("Juan");
        userEntity.setEmail("juan@rodriguez.org");

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setName("Juan");
        responseDTO.setEmail("juan@rodriguez.org");

        when(userMapper.fromRequestDTO(any(UserRequestDTO.class))).thenReturn(userEntity);
        when(userUseCase.createdUser(any(User.class))).thenReturn(new User());
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);
        when(jwtUtil.generateToken(any(String.class))).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@rodriguez.org"))
                .andExpect(jsonPath("$.name").value("Juan"));

        verify(userUseCase, times(1)).createdUser(any(User.class));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Juan");
        userRequestDTO.setEmail("juan@rodriguez.org");
        userRequestDTO.setPassword("password");

        when(userUseCase.createdUser(any(User.class))).thenThrow(new UserAlreadyExistsException("El correo ya está registrado"));

        mockMvc.perform(post("/api/users/register")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El correo ya está registrado"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        String email = "juan@rodriguez.org";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(email);
        userResponseDTO.setName("Juan");

        when(userUseCase.getUserByEmail(anyString())).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"))
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        String email = "juan@rodriguez.org";
        String password = "wrongpassword";

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);

        when(userUseCase.getUserByEmail(anyString())).thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(post("/api/users/login")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"));
    }
    @Test
    public void shouldGetUserById() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Juan");
        user.setEmail("juan@email.com");

        when(userUseCase.getUserById(userId)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenCallRealMethod(); // si tienes implementado realMethod

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        Long userId = 1L;
        List<PhoneRequestDTO> phones = List.of(new PhoneRequestDTO("123456789", "1", "57"));
        UserRequestDTO userRequestDTO = new UserRequestDTO("Updated", "updated@email.com", "newpass123", phones);

        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setName(userRequestDTO.getName());
        entity.setEmail(userRequestDTO.getEmail());
        entity.setPassword(userRequestDTO.getPassword());

        User domainUser = new User();
        domainUser.setId(userId);
        domainUser.setName(entity.getName());
        domainUser.setEmail(entity.getEmail());
        domainUser.setPassword(entity.getPassword());

        when(userMapper.fromRequestDTO(any())).thenReturn(entity);
        when(userMapper.toDomain(any())).thenReturn(domainUser);
        when(userUseCase.updateUser(any())).thenReturn(domainUser);
        when(userMapper.toResponseDTO(any())).thenCallRealMethod();

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        Long userId = 1L;

        doNothing().when(userUseCase).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test

    public void shouldLoginSuccessfully() throws Exception {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("test@email.com", "1234");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        when(userUseCase.getUserByEmail(request.getEmail())).thenReturn(user);
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("validToken");
        when(userMapper.toResponseDTO(any(User.class))).thenCallRealMethod();

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Verifica que el estado de la respuesta sea 200 OK
                .andExpect(jsonPath("$.token").value("validToken")) // Verifica que el token esté presente en la respuesta
                .andExpect(jsonPath("$.user.email").value("test@email.com")); // Verifica que el email del usuario sea el correcto
    }


}
