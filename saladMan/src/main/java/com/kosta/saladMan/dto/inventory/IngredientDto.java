package com.kosta.saladMan.dto.inventory;


import java.time.LocalDate;

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
public class IngredientDto {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String unit;
    private Boolean available;


    public Ingredient toEntity() {
        return Ingredient.builder()
                .id(id)
                .category(IngredientCategory.builder().id(categoryId).build())
                .name(name)
                .unit(unit)
                .available(available)
                .build();
    }
}
