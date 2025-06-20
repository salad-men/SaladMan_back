package com.kosta.saladMan.dto.purchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreOrderItemDto {
    private Integer ingredientId;
    private String name;
    private String category;
    private Integer quantity;          // 현재 재고
    private Integer incoming;          // 입고 중 수량
    private String unit;               // 단위 (g, 개 등)
    private Integer unitCost;          // 단가
    private Integer minimumOrderUnit;  // 최소 발주 단위
}
