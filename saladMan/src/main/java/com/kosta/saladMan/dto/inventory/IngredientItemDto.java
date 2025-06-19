package com.kosta.saladMan.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientItemDto {

	private Integer id; // 재료 번호
	private Integer categoryId; // 카테고리 번호
	private String name; // 아이템 이름
	private String unit; // 재료 단위
	private Boolean available; // 발주가능여부

	private String categoryName; // 카테고리이름
	private Integer stockQuantity; // hq_quantity
	private Integer unitCost; //

	public IngredientItemDto(Integer id, Integer categoryId, String categoryName, String unit, String name,
			Boolean available, Integer stockQuantity, Integer unitCost) {
		this.id = id;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.unit = unit;
		this.name = name;
		this.available = available;
		this.stockQuantity = stockQuantity;
		this.unitCost = unitCost;
	}

}
