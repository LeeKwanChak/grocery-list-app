package com.marcus.grocerylist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateRequest {
    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @NotNull(message = "Grocery list ID cannot be null")
    private Long groceryListId;

    private boolean completed;

    private int quantity;
}
