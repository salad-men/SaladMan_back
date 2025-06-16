package com.kosta.saladMan.dto.inventory;

import java.time.LocalDate;

import javax.persistence.Column;

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
    private String categoryName;   
    private String ingredientName;
    private Integer unitCost;
    private Integer expiredQuantity;
    private Integer minimumOrderUnit;
    private Integer quantity;
    private LocalDate expiredDate;

    public HqIngredient toEntity() {
        return HqIngredient.builder()
                .id(id)
                .category(IngredientCategory.builder().id(categoryId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .unitCost(unitCost)
                .expiredQuantity(expiredQuantity)
                .minimumOrderUnit(minimumOrderUnit)
                .quantity(quantity)
                .expiredDate(expiredDate)
                .build();
    }
}