package com.mserv.hexagonal_users.application;

import com.mserv.hexagonal_users.application.exception.UserAlreadyExistsException;
import com.mserv.hexagonal_users.application.exception.UserNotFoundException;
import com.mserv.hexagonal_users.application.mappers.UserMapper;
import com.mserv.hexagonal_users.application.usecase.UserUseCase;
import com.mserv.hexagonal_users.domain.model.User;
import com.mserv.hexagonal_users.domain.port.AuthService;
import com.mserv.hexagonal_users.domain.port.UserRepository;
import com.mserv.hexagonal_users.infrastructure.adapter.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;  // Mock del repositorio


    @Mock
    private AuthService authService;

    @InjectMocks
    private UserUseCase userUseCase;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test

    public void shouldRegisterUserSuccessfully() {
        // Arrange
        User user = new User();
        user.setName("Juan");
        user.setEmail("juan@rodriguez.org");
        user.setPassword("password");
        user.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        user.setLastLogin(now);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());  // Simula que no existe el usuario
        when(authService.generateToken(user.getEmail())).thenReturn("dummy-jwt-token");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userUseCase.createdUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals("dummy-jwt-token", result.getToken());
        assertTrue(result.isActive());

        // Verifica que se haya guardado el usuario
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenUserExists() {
        User user = new User();
        user.setName("Juan");
        user.setEmail("juan@rodriguez.org");
        user.setPassword("password");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));
        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createdUser(user));
        verify(userRepository, times(0)).save(user);
    }
    @Test
    public void shouldGetUserByIdSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Juan");
        user.setEmail("juan@rodriguez.org");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userUseCase.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("juan@rodriguez.org", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserNotFoundById() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void shouldGetUserByEmailSuccessfully() {
        String email = "juan@rodriguez.org";
        User user = new User();
        user.setEmail(email);
        user.setName("Juan");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userUseCase.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("Juan", result.getName());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUserNotFoundByEmail() {
        String email = "juan@rodriguez.org";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserByEmail(email));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Juan");
        existingUser.setEmail("juan@rodriguez.org");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Juan Updated");
        updatedUser.setEmail("juan.updated@rodriguez.org");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userUseCase.updateUser(updatedUser);

        assertNotNull(result);
        assertEquals("Juan Updated", result.getName());
        assertEquals("juan.updated@rodriguez.org", result.getEmail());

        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUpdatingUserNotFound() {
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Juan Updated");
        updatedUser.setEmail("juan.updated@rodriguez.org");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.updateUser(updatedUser));

        verify(userRepository, times(0)).save(updatedUser);
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Juan");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        userUseCase.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenDeletingUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.deleteUser(userId));

        verify(userRepository, times(0)).deleteById(userId);
    }

    @Test
    public void shouldGetAllUsersSuccessfully() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Juan");
        user1.setEmail("juan@rodriguez.org");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Pedro");
        user2.setEmail("pedro@rodriguez.org");
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);
        Iterable<User> result = userUseCase.getAllUsers();
        assertNotNull(result);
        assertTrue(((List<User>) result).size() > 0);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void shouldCountUsersSuccessfully() {
        long userCount = 5;
        when(userRepository.count()).thenReturn(userCount);
        long result = userUseCase.countUsers();
        assertEquals(userCount, result);
        verify(userRepository, times(1)).count();
    }
}


