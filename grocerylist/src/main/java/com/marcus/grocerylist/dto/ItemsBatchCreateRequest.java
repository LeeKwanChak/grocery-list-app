package com.marcus.grocerylist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ItemsBatchCreateRequest {

    @NotNull(message = "Grocery list ID cannot be null for batch creation")
    private Long groceryListId;

    @NotEmpty(message = "Item names list cannot be empty")
    private List<String> itemNames;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;
}