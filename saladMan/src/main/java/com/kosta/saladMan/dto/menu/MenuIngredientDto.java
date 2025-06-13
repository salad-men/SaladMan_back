package com.kosta.saladMan.dto.menu;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.menu.MenuIngredient;
import com.kosta.saladMan.entity.menu.TotalMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuIngredientDto {
    private Integer id;
    private Integer menuId;
    private Integer ingredientId;
    private Integer quantity;

    public MenuIngredient toEntity() {
        return MenuIngredient.builder()
                .id(id)
                .menu(TotalMenu.builder().id(menuId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .quantity(quantity)
                .build();
    }
}
