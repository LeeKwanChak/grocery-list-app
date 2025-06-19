package com.marcus.grocerylist.controller;

import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.model.User;
import com.marcus.grocerylist.service.GroceryListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists")
public class GroceryListController {
    @Autowired
    private GroceryListService groceryListService;

    @PostMapping
    public GroceryList creatList(@RequestBody GroceryList list){
        return groceryListService.saveList(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GroceryList> deleteList(@PathVariable Long id){
        groceryListService.deleteList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<GroceryList> getListByUser(@PathVariable Long userId){
        User user = new User();
        user.setId(userId);
        return groceryListService.getListById(user);
    }


}
