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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String USER_SESSION_KEY = "authenticatedUser";

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return RegisterResponse.success(savedUser.getId());
    }

    @Transactional(readOnly = true)
    public MessageResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        session.setAttribute(USER_SESSION_KEY, user.getId());
        log.info("User logged in successfully: {}", user.getUsername());

        return MessageResponse.of("Login successful");
    }

    public MessageResponse logout(HttpSession session) {
        session.invalidate();
        return MessageResponse.of("Logout successful");
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser(HttpSession session) {
        Long userId = (Long) session.getAttribute(USER_SESSION_KEY);
        if (userId == null) {
            throw new UnauthorizedException("Not authenticated");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(USER_SESSION_KEY) != null;
    }
}

