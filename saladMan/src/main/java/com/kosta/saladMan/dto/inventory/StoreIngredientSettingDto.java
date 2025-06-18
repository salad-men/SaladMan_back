package com.kosta.saladMan.dto.inventory;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientSetting;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreIngredientSettingDto {
    private Integer id;
    private Integer storeId;
    private Integer ingredientId;
    private Integer minQuantity;
    private Integer maxQuantity;
    private String categoryName;

    public StoreIngredientSetting toEntity() {
        return StoreIngredientSetting.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .minQuantity(minQuantity)
                .maxQuantity(maxQuantity)
                .build();
    }
}
