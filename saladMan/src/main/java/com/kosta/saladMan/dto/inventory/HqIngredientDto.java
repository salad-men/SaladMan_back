package com.kosta.saladMan.dto.inventory;

import java.time.LocalDate;

import javax.persistence.Column;

import com.kosta.saladMan.entity.inventory.HqIngredient;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientCategory;
import com.kosta.saladMan.entity.store.Store;

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
    private String storeName;
    private Integer storeId;
    private Integer unitCost;
    private String unit; 
    private Integer minimumOrderUnit;
    private Integer quantity;
    private LocalDate expiredDate;
    private Integer minquantity;  
    private LocalDate receivedDate;

    public HqIngredient toEntity() {
        return HqIngredient.builder()
                .id(id)
                .category(IngredientCategory.builder().id(categoryId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .store(Store.builder().id(storeId).build())
                .unitCost(unitCost)
                .minimumOrderUnit(minimumOrderUnit)
                .quantity(quantity)
                .expiredDate(expiredDate != null ? expiredDate : LocalDate.now())  
                .receivedDate(receivedDate != null ? receivedDate : LocalDate.now()) 
                .build();
    }

}