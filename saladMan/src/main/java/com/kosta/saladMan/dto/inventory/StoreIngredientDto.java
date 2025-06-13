package com.kosta.saladMan.dto.inventory;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.inventory.StoreIngredient;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreIngredientDto {
    private Integer id;
    private Integer categoryId;
    private Integer ingredientId;
    private Integer storeId;
    private Integer expiredQuantity;
    private Integer quantity;

    public StoreIngredient toEntity() {
        return StoreIngredient.builder()
                .id(id)
                .category(categoryId != null ? IngredientCategory.builder().id(categoryId).build() : null)
                .ingredient(ingredientId != null ? Ingredient.builder().id(ingredientId).build() : null)
                .store(storeId != null ? Store.builder().id(storeId).build() : null)
                .expiredQuantity(expiredQuantity)
                .quantity(quantity)
                .build();
    }
}
