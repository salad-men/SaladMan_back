package com.kosta.saladMan.dto.inventory;

import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HqIngredientDto {
    private Integer id;
    private Integer categoryId;
    private Integer ingredientId;
    private Integer unitCost;
    private Integer expiredQuantity;
    private Integer minimumOrderUnit;
    private Integer quantity;

    public HqIngredient toEntity() {
        return HqIngredient.builder()
                .id(id)
                .category(IngredientCategory.builder().id(categoryId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .unitCost(unitCost)
                .expiredQuantity(expiredQuantity)
                .minimumOrderUnit(minimumOrderUnit)
                .quantity(quantity)
                .build();
    }
}

