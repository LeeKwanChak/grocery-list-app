package com.marcus.grocerylist.controller;

import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.service.GroceryListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.marcus.grocerylist.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/lists")
public class GroceryListController {
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
    public ResponseEntity<GroceryList> createList(@RequestBody GroceryList list) {
        User currentUser = getCurrentUser();
        list.setUser(currentUser);
        GroceryList savedList = groceryListService.saveList(list);
        return new ResponseEntity<>(savedList, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        User currentUser = getCurrentUser();

        GroceryList list = groceryListService.getListById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found"));

        if (!list.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this list.");
        }

        groceryListService.deleteList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GroceryList>> getListsByCurrentUser() {
        User currentUser = getCurrentUser();
        List<GroceryList> lists = groceryListService.findListsByUser(currentUser);
        return ResponseEntity.ok(lists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroceryList> updateList(@PathVariable Long id, @RequestBody GroceryList updatedList) {
        User currentUser = getCurrentUser();

        GroceryList existingList = groceryListService.getListById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found"));

        if (!existingList.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this list.");
        }

        existingList.setName(updatedList.getName());

        GroceryList savedList = groceryListService.saveList(existingList);
        return ResponseEntity.ok(savedList);
    }


}
