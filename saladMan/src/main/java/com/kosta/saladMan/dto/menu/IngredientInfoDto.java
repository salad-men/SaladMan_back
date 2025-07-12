package com.kosta.saladMan.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientInfoDto {
	//ingredient
	private Integer ingredientId;
	private String name;
	private String unit;
	private Integer categoryId;
	private Integer price;
	//ingredient_category
	private String category;
	
	private Integer unitPrice;
	
}
