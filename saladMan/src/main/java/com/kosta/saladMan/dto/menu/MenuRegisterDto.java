package com.kosta.saladMan.dto.menu;

import java.util.List;

import lombok.Data;

@Data
public class MenuRegisterDto {
    private String name;
    private Integer salePrice;
    private Integer categoryId;
    private List<MenuIngredientDto> ingredients;
}
