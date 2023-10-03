package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.model.persistence.User;
import java.util.ArrayList;
import java.util.List;

public class OrderTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetOrdersForUser_ExistingUser_ReturnsOrders() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        UserOrder order1 = new UserOrder();
        UserOrder order2 = new UserOrder();
        List<UserOrder> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
        verify(userRepository).findByUsername(username);
        verify(orderRepository).findByUser(user);
    }

    @Test
    public void testSubmit_NonExistingUser_ReturnsNotFound() {
        // Arrange
        String username = "nonexistinguser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername(username);
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_NonExistingUser_ReturnsNotFound() {
        // Arrange
        String username = "nonexistinguser";
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername(username);
        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
