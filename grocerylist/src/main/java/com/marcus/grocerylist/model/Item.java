package com.marcus.grocerylist.model;

import jakarta.persistence.*;

@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private GroceryList groceryList;
}
