package com.kosta.saladMan.dto.purchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderItemDto {
	private Integer ingredientId;
	private String name;
	private String category;
	private Integer quantity; // 현재 재고
	private Integer incoming; // 입고 중 수량
	private String unit; // 단위 (g, 개 등)
	private Integer unitCost; // 단가
	private Integer minimumOrderUnit; // 최소 발주 단위
	private Integer hqStock;
	private Integer totalPrice;
	private Boolean available;

	public StoreOrderItemDto(Integer ingredientId, String name, String category, Integer quantity, Integer incoming,
			String unit,  Integer unitCost,Integer minimumOrderUnit, Integer hqStock,Boolean available) {
		
		this.ingredientId = ingredientId;
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.incoming = incoming;
		this.unit = unit;
		this.unitCost = unitCost;
		this.minimumOrderUnit = minimumOrderUnit;
		this.hqStock = hqStock;
		this.available=available;
	}
}
