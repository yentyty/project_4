package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.demo.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemTest {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setup() {
        // Khởi tạo các mock object trước mỗi phương thức kiểm tra
        itemRepository = mock(ItemRepository.class);
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems_ReturnsItems() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = BigDecimal.valueOf(10.99);
        item.setPrice(price);
        item.setDescription("Description 1");

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Item 2");
        BigDecimal price1 = BigDecimal.valueOf(15.99);
        item1.setPrice(price1);
        item.setDescription("Description 2");

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);

        when(itemRepository.findAll()).thenReturn(items);

        // Act
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertEquals("Item 1", responseItems.get(0).getName());
        assertEquals("Item 2", responseItems.get(1).getName());
        verify(itemRepository).findAll();
    }

    @Test
    public void testGetItemById_ExistingItem_ReturnsItem() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = BigDecimal.valueOf(10.99);
        item.setPrice(price);
        item.setDescription("Description 1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals("Item 1", responseItem.getName());
        verify(itemRepository).findById(1L);
    }

    @Test
    public void testGetItemById_NonExistingItem_ReturnsNotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemRepository).findById(1L);
    }

    @Test
    public void testGetItemsByName_ExistingItems_ReturnsItems() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = BigDecimal.valueOf(10.99);
        item.setPrice(price);
        item.setDescription("Description 1");

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Item 1");
        BigDecimal price1 = BigDecimal.valueOf(15.99);
        item1.setPrice(price1);
        item.setDescription("Description 2");

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item1);

        when(itemRepository.findByName("Item 1")).thenReturn(items);

        // Act
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertEquals("Item 1", responseItems.get(0).getName());
        assertEquals("Item 1", responseItems.get(1).getName());
        verify(itemRepository).findByName("Item 1");
    }

    @Test
    public void testGetItemsByName_NonExistingItems_ReturnsNotFound() {
        // Arrange
        when(itemRepository.findByName("Item 1")).thenReturn(null);

        // Act
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemRepository).findByName("Item 1");
    }
}
