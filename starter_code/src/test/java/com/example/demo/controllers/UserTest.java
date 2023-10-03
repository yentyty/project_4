package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.demo.model.persistence.User;
import java.util.Optional;

public class UserTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById_ValidId_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userController.findById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository).findById(userId);
    }

    @Test
    public void testFindById_InvalidId_ReturnsNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.findById(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findById(userId);
    }

    @Test
    public void testFindByUserName_ExistingUser_ReturnsUser() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.findByUserName(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testFindByUserName_NonExistingUser_ReturnsNotFound() {
        // Arrange
        String username = "nonexistinguser";

        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act
        ResponseEntity<User> response = userController.findByUserName(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void testCreateUser_ValidRequest_ReturnsOk() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setConfirmPassword("password");

        User user = new User();
        user.setUsername(request.getUsername());

        Cart cart = new Cart();
        user.setCart(cart);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByUsername("testuser1")).thenReturn(null);
    }

    @Test
    public void testCreateUser_InvalidRequest_ReturnsBadRequest() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("short");
        request.setConfirmPassword("different");

        // Act
        ResponseEntity<User> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}