package com.marcus.grocerylist.service;

import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.repository.GroceryListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroceryListServiceTest {

    @Mock
    private GroceryListRepository groceryListRepository;

    @InjectMocks
    private GroceryListService groceryListService;

    private User testUser;
    private GroceryList list1;
    private GroceryList list2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User("testUser", "password", "test@example.com");
        testUser.setId(1L);

        list1 = new GroceryList("My First List", testUser);
        list1.setId(10L);

        list2 = new GroceryList("Shopping Essentials", testUser);
        list2.setId(20L);
    }

    @Test
    void testFindListsByUser() {
        when(groceryListRepository.findByUser(testUser)).thenReturn(Arrays.asList(list1, list2));

        List<GroceryList> result = groceryListService.findListsByUser(testUser);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(list1));
        assertTrue(result.contains(list2));
        verify(groceryListRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testFindListsByUserNoListsFound() {
        when(groceryListRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        List<GroceryList> result = groceryListService.findListsByUser(testUser);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(groceryListRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testGetListByIdFound() {
        when(groceryListRepository.findById(list1.getId())).thenReturn(Optional.of(list1));

        Optional<GroceryList> result = groceryListService.getListById(list1.getId());

        assertTrue(result.isPresent());
        assertEquals(list1.getId(), result.get().getId());
        assertEquals(list1.getName(), result.get().getName());
        verify(groceryListRepository, times(1)).findById(list1.getId());
    }

    @Test
    void testGetListByIdNotFound() {
        Long nonExistentId = 99L;
        when(groceryListRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<GroceryList> result = groceryListService.getListById(nonExistentId);

        assertFalse(result.isPresent());
        verify(groceryListRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testSaveList() {
        GroceryList newList = new GroceryList("New List", testUser);
        GroceryList savedList = new GroceryList("New List", testUser);
        savedList.setId(30L);

        when(groceryListRepository.save(any(GroceryList.class))).thenReturn(savedList);

        GroceryList result = groceryListService.saveList(newList);

        assertNotNull(result);
        assertEquals(30L, result.getId());
        assertEquals("New List", result.getName());
        verify(groceryListRepository, times(1)).save(newList);
    }

    @Test
    void testDeleteList() {
        groceryListService.deleteList(list1.getId());

        verify(groceryListRepository, times(1)).deleteById(list1.getId());
    }

    @Test
    void testGetListById_ByUser() {
        when(groceryListRepository.findByUser(testUser)).thenReturn(Arrays.asList(list1, list2));

        List<GroceryList> result = groceryListService.getListById(testUser);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(list1));
        assertTrue(result.contains(list2));
        verify(groceryListRepository, times(1)).findByUser(testUser);
    }
}