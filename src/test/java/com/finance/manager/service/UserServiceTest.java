package com.finance.manager.service;

import com.finance.manager.dto.request.LoginRequest;
import com.finance.manager.dto.request.RegisterRequest;
import com.finance.manager.dto.response.MessageResponse;
import com.finance.manager.dto.response.RegisterResponse;
import com.finance.manager.entity.User;
import com.finance.manager.exception.DuplicateResourceException;
import com.finance.manager.exception.UnauthorizedException;
import com.finance.manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();

        loginRequest = LoginRequest.builder()
                .username("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("encodedPassword")
                .fullName("Test User")
                .phoneNumber("+1234567890")
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("User registered successfully", response.getMessage());
        assertEquals(1L, response.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        MessageResponse response = userService.login(loginRequest, session);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        verify(session).setAttribute(eq("authenticatedUser"), eq(1L));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest, session));
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest, session));
    }

    @Test
    void logout_Success() {
        MessageResponse response = userService.logout(session);

        assertNotNull(response);
        assertEquals("Logout successful", response.getMessage());
        verify(session).invalidate();
    }

    @Test
    void getAuthenticatedUser_Success() {
        when(session.getAttribute("authenticatedUser")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getAuthenticatedUser(session);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAuthenticatedUser_NotAuthenticated_ThrowsException() {
        when(session.getAttribute("authenticatedUser")).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> userService.getAuthenticatedUser(session));
    }

    @Test
    void getAuthenticatedUser_UserNotFound_ThrowsException() {
        when(session.getAttribute("authenticatedUser")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.getAuthenticatedUser(session));
    }

    @Test
    void isAuthenticated_ReturnsTrue() {
        when(session.getAttribute("authenticatedUser")).thenReturn(1L);

        assertTrue(userService.isAuthenticated(session));
    }

    @Test
    void isAuthenticated_ReturnsFalse() {
        when(session.getAttribute("authenticatedUser")).thenReturn(null);

        assertFalse(userService.isAuthenticated(session));
    }
}

