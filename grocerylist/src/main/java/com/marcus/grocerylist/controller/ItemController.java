package com.marcus.grocerylist.controller;


import com.marcus.grocerylist.model.Item;
import com.marcus.grocerylist.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @PostMapping
    public Item creatItem(@RequestBody Item item){
        return itemService.saveItem(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);
    }

    @GetMapping("/list/{listId}")
    public List<Item> getItemsByList(@PathVariable Long listId){
        return itemService.getItemsByListId(listId);
    }


}
