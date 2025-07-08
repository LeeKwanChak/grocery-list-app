package com.marcus.grocerylist.model;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name cannot be empty")
    private String name;

    private boolean isCompleted = false;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private GroceryList groceryList;

    public Item(String name, GroceryList groceryList, boolean isCompleted, int quantity) {
        this.name = name;
        this.groceryList = groceryList;
        this.quantity = quantity;
    }

    public Item(String name, GroceryList groceryList){
        this.name = name;
        this.groceryList = groceryList;
    }

    public Item(String name){
        this.name = name;
    }

    public Item(){};

}
