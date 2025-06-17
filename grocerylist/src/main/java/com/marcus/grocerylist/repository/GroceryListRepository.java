package com.marcus.grocerylist.repository;

import com.marcus.grocerylist.model.GroceryList;
import org.springframework.data.jpa.repository.JpaRepository;
import com.marcus.grocerylist.model.User;

import java.util.List;
import java.util.Optional;

public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    List<GroceryList> findByUser(User user);
}
