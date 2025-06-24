package com.marcus.grocerylist.service;

import com.marcus.grocerylist.exception.ResourceNotFoundException;
import com.marcus.grocerylist.exception.UnauthorizedAccessException;
import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.repository.GroceryListRepository;
import com.marcus.grocerylist.repository.ItemRepository;
import com.marcus.grocerylist.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final GroceryListRepository groceryListRepository;

    public ItemService(ItemRepository itemRepository, GroceryListRepository groceryListRepository){
        this.itemRepository = itemRepository;
        this.groceryListRepository = groceryListRepository;
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

    @Transactional // All or nothing
    public void deleteItemsInBatch(List<Long> itemIds){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Item> deleteItems = itemRepository.findAllByIdIn(itemIds);

        if(itemIds.size() != deleteItems.size()){
            throw new ResourceNotFoundException("Some items not found");
        }
        for(Item item: deleteItems){
            GroceryList groceryList = item.getGroceryList();

            if (!groceryList.getUser().getUsername().equals(currentUsername)) {
                throw new UnauthorizedAccessException("You are not authorized to delete item with ID: " + item.getId() + " as it does not belong to your list.");
            }
        }
        itemRepository.deleteAllByIdInBatch(itemIds);
    }

    @Transactional
    public void createItemsInBatch(GroceryList groceryList, List<String> itemNames){
        List<Item> SaveItem = new ArrayList<>();
        for(String name:itemNames){
            SaveItem.add(new Item(name, groceryList));
        }
        itemRepository.saveAll(SaveItem);
    }

}
