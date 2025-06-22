package com.marcus.grocerylist.service;

import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.repository.ItemRepository;
import com.marcus.grocerylist.model.Item;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    public List<Item> getItemsByListId(Long listId) {
        return itemRepository.findByGroceryListId(listId);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> getItemsByGroceryList(GroceryList groceryList){
        return itemRepository.findByGroceryList(groceryList);
    }

    public Optional<Item> findById(Long id) { return itemRepository.findById(id); }
}
