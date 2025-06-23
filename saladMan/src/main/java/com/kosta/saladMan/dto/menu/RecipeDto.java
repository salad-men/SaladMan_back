package com.kosta.saladMan.dto.menu;

import java.util.List;

import com.kosta.saladMan.entity.menu.TotalMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {
    private Integer id;
    private String name;
    private String img;
    private List<MenuIngredientDto> ingredients;

}
