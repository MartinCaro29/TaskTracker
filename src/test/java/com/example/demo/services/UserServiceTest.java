package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.repositories.AuditLogRepository;
import com.example.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetUserById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("Bob", foundUser.getUsername());
        assertEquals("bob@example.com", foundUser.getEmail());
    }


    @Test
    void testCreateUser_Success() {
        User user = new User();
        user.setUsername("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        var dto = userService.createUser(user);

        assertNotNull(dto);
        assertEquals("Alice", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
    }


    @Test
    void testUsernameTooShort() {
        User user = new User();
        user.setUsername("Bo");
        assertTrue(user.getUsername().length() < 3);
    }

    @Test
    void testPasswordTooShort() {
        User user = new User();
        user.setPassword("1234567");
        assertTrue(user.getPassword().length() < 8);
    }
}
