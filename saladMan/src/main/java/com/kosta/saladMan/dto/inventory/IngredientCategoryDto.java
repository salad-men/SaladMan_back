package com.kosta.saladMan.dto.inventory;


import com.kosta.saladMan.entity.inventory.IngredientCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientCategoryDto {
    private Integer id;
    private String name;

    public IngredientCategory toEntity() {
        return IngredientCategory.builder()
                .id(id)
                .build();
    }
}
