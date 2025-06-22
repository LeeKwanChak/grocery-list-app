package com.marcus.grocerylist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.marcus.grocerylist.model.Item;
import com.marcus.grocerylist.model.GroceryList;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByGroceryList(GroceryList groceryList);
    List<Item> findByGroceryListId(Long listId);

}
