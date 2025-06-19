package com.marcus.grocerylist.service;

import com.marcus.grocerylist.model.GroceryList;
import com.marcus.grocerylist.repository.GroceryListRepository;
import org.springframework.stereotype.Service;
import com.marcus.grocerylist.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class GroceryListService {
    private final GroceryListRepository groceryListRepository;

    public GroceryListService(GroceryListRepository groceryListRepository){
        this.groceryListRepository = groceryListRepository;
    }

    public List<GroceryList> getListById(User user){
        return groceryListRepository.findByUser(user);
    }

    public Optional<GroceryList> getListById(Long id){
        return groceryListRepository.findById(id);
    }

    public GroceryList saveList(GroceryList list){
        return groceryListRepository.save(list);
    }

    public void deleteList(Long id){
        groceryListRepository.deleteById(id);
    }
}
