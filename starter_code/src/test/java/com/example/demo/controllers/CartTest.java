package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import com.example.demo.TestUtils;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class CartTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        cartRepository = mock(CartRepository.class);
        itemRepository = mock(ItemRepository.class);
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart_ValidUserAndItem_ReturnsCart() {
        // Arrange
        User user = new User();
        Cart cart = new Cart();
        user.setUsername("testUser");
        user.setPassword("Password");
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(10.99));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart carts = response.getBody();
        assertNotNull(carts);
        assertEquals(2, carts.getItems().size());
        verify(userRepository).findByUsername("testUser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(cart);
    }

    @Test
    public void testAddToCart_InvalidUser_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistingUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername("nonExistingUser");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_InvalidItem_ReturnsNotFound() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername("testUser");
        verify(itemRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_ValidUserAndItem_ReturnsCart() {
        // Arrange
        User user = new User();
        Cart cart = new Cart();
        user.setUsername("testUser");
        user.setPassword("Password");
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(10.99));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        cart.setUser(user);
        cart.addItem(item);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart updatedCart = response.getBody();
        assertNotNull(updatedCart);
        assertEquals(0, updatedCart.getItems().size());
        verify(userRepository).findByUsername("testUser");
        verify(itemRepository).findById(1L);
        verify(cartRepository).save(updatedCart);
    }

    @Test
    public void testRemoveFromCart_InvalidUser_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistingUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername("nonExistingUser");
        verify(itemRepository, never()).findById(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_InvalidItem_ReturnsNotFound() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository).findByUsername("testUser");
        verify(itemRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
