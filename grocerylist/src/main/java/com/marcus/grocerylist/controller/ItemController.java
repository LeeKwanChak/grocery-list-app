package com.marcus.grocerylist.controller;


import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.model.Item;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.service.ItemService;
import com.marcus.grocerylist.service.GroceryListService;
import com.marcus.grocerylist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private GroceryListService groceryListService;
    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        User currentUser = getCurrentUser();

        // 驗證項目所屬的清單是否存在且屬於當前使用者
        Long listId = item.getGroceryList().getId();
        GroceryList groceryList = groceryListService.getListById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroceryList not found"));

        if (!groceryList.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to add items to this list.");
        }

        item.setGroceryList(groceryList);
        Item savedItem = itemService.saveItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        Item item = itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.getGroceryList().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this item.");
        }

        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<Item>> getItemsByList(@PathVariable Long listId) {
        User currentUser = getCurrentUser();

        GroceryList groceryList = groceryListService.getListById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroceryList not found"));

        if (!groceryList.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view items in this list.");
        }

        List<Item> items = itemService.getItemsByListId(listId);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        User currentUser = getCurrentUser();

        Item existingItem = itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!existingItem.getGroceryList().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this item.");
        }

        existingItem.setName(updatedItem.getName());
        existingItem.setCompleted(updatedItem.isCompleted());

        Item savedItem = itemService.saveItem(existingItem);
        return ResponseEntity.ok(savedItem);
    }


}
