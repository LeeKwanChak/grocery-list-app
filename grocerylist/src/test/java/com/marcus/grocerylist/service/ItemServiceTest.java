package com.marcus.grocerylist.service;

import com.marcus.grocerylist.exception.ResourceNotFoundException;
import com.marcus.grocerylist.exception.UnauthorizedAccessException;
import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.model.Item;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.repository.GroceryListRepository;
import com.marcus.grocerylist.repository.ItemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private GroceryListRepository groceryListRepository;

    @InjectMocks
    private ItemService itemService;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);

        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    void testGetItemsByListId() {
        Long listId = 1L;
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(listId);

        List<Item> expectedItems = Arrays.asList(
                new Item("Milk", mockGroceryList, false, 1),
                new Item("Bread", mockGroceryList, false, 1)
        );
        expectedItems.get(0).setId(1L);
        expectedItems.get(1).setId(2L);

        when(itemRepository.findByGroceryListId(listId)).thenReturn(expectedItems);

        List<Item> actualItems = itemService.getItemsByListId(listId);

        assertNotNull(actualItems);
        assertEquals(2, actualItems.size());
        assertEquals("Milk", actualItems.get(0).getName());
        assertEquals(1L, actualItems.get(0).getId());

        verify(itemRepository, times(1)).findByGroceryListId(listId);
    }

    @Test
    void testGetItemsByListIdWhenListIsEmpty() {
        Long listId = 2L;
        when(itemRepository.findByGroceryListId(listId)).thenReturn(new ArrayList<>());

        List<Item> actualItems = itemService.getItemsByListId(listId);

        assertNotNull(actualItems);
        assertTrue(actualItems.isEmpty());
        verify(itemRepository, times(1)).findByGroceryListId(listId);
    }

    @Test
    void testGetItemsByListIdForNonExistentList() {
        Long nonExistentListId = 99L;
        when(itemRepository.findByGroceryListId(nonExistentListId)).thenReturn(new ArrayList<>());

        List<Item> actualItems = itemService.getItemsByListId(nonExistentListId);

        assertNotNull(actualItems);
        assertTrue(actualItems.isEmpty());
        verify(itemRepository, times(1)).findByGroceryListId(nonExistentListId);
    }

    @Test
    void testSaveItem() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);
        Item newItem = new Item("New Item", mockGroceryList);
        newItem.setCompleted(false);
        newItem.setQuantity(1);

        Item savedItem = new Item("New Item", mockGroceryList);
        savedItem.setId(1L);
        savedItem.setCompleted(false);
        savedItem.setQuantity(1);

        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.saveItem(newItem);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Item", result.getName());
        assertFalse(result.isCompleted());
        assertEquals(1, result.getQuantity());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSaveItemWithNullName() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);
        Item newItem = new Item(null, mockGroceryList);
        newItem.setCompleted(false);
        newItem.setQuantity(1);

        when(itemRepository.save(any(Item.class))).thenThrow(new IllegalArgumentException("Item name cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> itemService.saveItem(newItem));

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSaveItemWithEmptyName() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);
        Item newItem = new Item("", mockGroceryList);
        newItem.setCompleted(false);
        newItem.setQuantity(1);

        when(itemRepository.save(any(Item.class))).thenThrow(new IllegalArgumentException("Item name cannot be empty"));

        assertThrows(IllegalArgumentException.class, () -> itemService.saveItem(newItem));

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSaveItemWithZeroQuantity() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);
        Item newItem = new Item("Test Item", mockGroceryList);
        newItem.setCompleted(false);
        newItem.setQuantity(0);

        when(itemRepository.save(any(Item.class))).thenThrow(new IllegalArgumentException("Quantity must be positive"));

        assertThrows(IllegalArgumentException.class, () -> itemService.saveItem(newItem));

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testSaveItemWithNegativeQuantity() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);
        Item newItem = new Item("Test Item", mockGroceryList);
        newItem.setCompleted(false);
        newItem.setQuantity(-5);

        when(itemRepository.save(any(Item.class))).thenThrow(new IllegalArgumentException("Quantity cannot be negative"));

        assertThrows(IllegalArgumentException.class, () -> itemService.saveItem(newItem));

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testDeleteItem() {
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteById(itemId);

        itemService.deleteItem(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void testDeleteItemWithNonExistentId() {
        Long nonExistentItemId = 99L;

        doNothing().when(itemRepository).deleteById(nonExistentItemId);

        assertDoesNotThrow(() -> itemService.deleteItem(nonExistentItemId));

        verify(itemRepository, times(1)).deleteById(nonExistentItemId);
    }

    @Test
    void testGetItemsByGroceryList() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);

        List<Item> expectedItems = Arrays.asList(
                new Item("Item A", mockGroceryList, false, 1),
                new Item("Item B", mockGroceryList, true, 2)
        );
        expectedItems.get(0).setId(1L);
        expectedItems.get(1).setId(2L);

        when(itemRepository.findByGroceryList(mockGroceryList)).thenReturn(expectedItems);

        List<Item> actualItems = itemService.getItemsByGroceryList(mockGroceryList);

        assertNotNull(actualItems);
        assertEquals(2, actualItems.size());
        assertEquals("Item A", actualItems.get(0).getName());
        assertEquals(1L, actualItems.get(0).getId());

        verify(itemRepository, times(1)).findByGroceryList(mockGroceryList);
    }

    @Test
    void testFindByIdFound() {
        Long itemId = 1L;
        GroceryList dummyGroceryList = new GroceryList();
        dummyGroceryList.setId(99L);
        Item expectedItem = new Item("Found Item", dummyGroceryList, false, 1);
        expectedItem.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        Optional<Item> result = itemService.findById(itemId);

        assertTrue(result.isPresent());
        assertEquals(expectedItem, result.get());

        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testFindByIdNotFound() {
        Long itemId = 99L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        Optional<Item> result = itemService.findById(itemId);

        assertFalse(result.isPresent());

        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void testDeleteItemsInBatchSuccess() {
        String currentUser = "testUser";
        User user = new User("testUser", "password", "test@example.com");
        user.setId(1L);

        GroceryList groceryList = new GroceryList();
        groceryList.setId(1L);
        groceryList.setUser(user);

        List<Long> itemIds = Arrays.asList(1L, 2L);
        List<Item> itemsToDelete = Arrays.asList(
                new Item("Item A", groceryList, false, 1),
                new Item("Item B", groceryList, false, 1)
        );
        itemsToDelete.get(0).setId(1L);
        itemsToDelete.get(1).setId(2L);

        when(authentication.getName()).thenReturn(currentUser);
        when(itemRepository.findAllByIdIn(itemIds)).thenReturn(itemsToDelete);
        doNothing().when(itemRepository).deleteAllByIdInBatch(itemIds);

        itemService.deleteItemsInBatch(itemIds);

        verify(itemRepository, times(1)).findAllByIdIn(itemIds);
        verify(itemRepository, times(1)).deleteAllByIdInBatch(itemIds);
    }

    @Test
    void testDeleteItemsInBatchResourceNotFound() {
        List<Long> itemIds = Arrays.asList(1L, 2L);
        Item partialFoundItem = new Item();
        partialFoundItem.setId(1L);
        partialFoundItem.setName("Partial Item");
        GroceryList gl = new GroceryList(); gl.setId(1L); User u = new User(); u.setUsername("test"); gl.setUser(u);
        partialFoundItem.setGroceryList(gl);

        List<Item> foundItems = Arrays.asList(partialFoundItem);

        when(itemRepository.findAllByIdIn(itemIds)).thenReturn(foundItems);

        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemsInBatch(itemIds));

        verify(itemRepository, never()).deleteAllByIdInBatch(anyList());
    }

    @Test
    void testDeleteItemsInBatchUnauthorizedAccess() {
        String currentUser = "testUser";
        String anotherUser = "anotherUser";
        User userOwned = new User(anotherUser, "pass", "another@example.com");
        userOwned.setId(2L);

        GroceryList groceryList = new GroceryList();
        groceryList.setId(1L);
        groceryList.setUser(userOwned);

        List<Long> itemIds = Arrays.asList(1L);
        List<Item> itemsToDelete = Arrays.asList(
                new Item("Item C", groceryList, false, 1)
        );
        itemsToDelete.get(0).setId(1L);

        when(authentication.getName()).thenReturn(currentUser);
        when(itemRepository.findAllByIdIn(itemIds)).thenReturn(itemsToDelete);

        assertThrows(UnauthorizedAccessException.class, () -> itemService.deleteItemsInBatch(itemIds));

        verify(itemRepository, never()).deleteAllByIdInBatch(anyList());
    }

    @Test
    void testDeleteItemsInBatchWithEmptyList() {
        List<Long> emptyItemIds = new ArrayList<>();
        String currentUser = "testUser";
        when(authentication.getName()).thenReturn(currentUser);

        itemService.deleteItemsInBatch(emptyItemIds);

        verify(itemRepository, never()).findAllByIdIn(anyList());
        verify(itemRepository, never()).deleteAllByIdInBatch(anyList());
    }

    @Test
    void testDeleteItemsInBatchWithDuplicateIds() {
        String currentUser = "testUser";
        User user = new User("testUser", "password", "test@example.com");
        user.setId(1L);

        GroceryList groceryList = new GroceryList();
        groceryList.setId(1L);
        groceryList.setUser(user);

        List<Long> itemIdsWithDuplicates = Arrays.asList(1L, 2L, 1L);
        List<Long> uniqueItemIdsForMock = Arrays.asList(1L, 2L);

        List<Item> itemsFoundByRepository = Arrays.asList(
                new Item("Item A", groceryList, false, 1),
                new Item("Item B", groceryList, false, 1)
        );
        itemsFoundByRepository.get(0).setId(1L);
        itemsFoundByRepository.get(1).setId(2L);

        when(authentication.getName()).thenReturn(currentUser);
        when(itemRepository.findAllByIdIn(eq(uniqueItemIdsForMock))).thenReturn(itemsFoundByRepository);
        doNothing().when(itemRepository).deleteAllByIdInBatch(eq(uniqueItemIdsForMock));

        assertDoesNotThrow(() -> itemService.deleteItemsInBatch(itemIdsWithDuplicates));

        verify(itemRepository, times(1)).findAllByIdIn(eq(uniqueItemIdsForMock));
        verify(itemRepository, times(1)).deleteAllByIdInBatch(eq(uniqueItemIdsForMock));
    }

    @Test
    void testCreateItemsInBatch() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);

        List<String> itemNames = Arrays.asList("Apple", "Banana", "Orange");
        when(itemRepository.saveAll(anyList())).thenReturn(Arrays.asList(
                new Item("Apple", mockGroceryList, false, 1),
                new Item("Banana", mockGroceryList, false, 1),
                new Item("Orange", mockGroceryList, false, 1)
        ));

        itemService.createItemsInBatch(mockGroceryList, itemNames);

        verify(itemRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateItemsInBatchWithEmptyNamesList() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);

        List<String> emptyItemNames = new ArrayList<>();

        itemService.createItemsInBatch(mockGroceryList, emptyItemNames);

        verify(itemRepository, never()).saveAll(anyList());
    }

    @Test
    void testCreateItemsInBatchWithNullName() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);

        List<String> itemNames = Arrays.asList("Item1", null, "Item3");

        when(itemRepository.saveAll(anyList())).thenThrow(new IllegalArgumentException("Item name cannot be null or empty in batch"));

        assertThrows(IllegalArgumentException.class, () -> itemService.createItemsInBatch(mockGroceryList, itemNames));

        verify(itemRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateItemsInBatchWithEmptyName() {
        GroceryList mockGroceryList = new GroceryList();
        mockGroceryList.setId(1L);

        List<String> itemNames = Arrays.asList("Item1", "", "Item3");

        when(itemRepository.saveAll(anyList())).thenThrow(new IllegalArgumentException("Item name cannot be null or empty in batch"));

        assertThrows(IllegalArgumentException.class, () -> itemService.createItemsInBatch(mockGroceryList, itemNames));

        verify(itemRepository, times(1)).saveAll(anyList());
    }
}