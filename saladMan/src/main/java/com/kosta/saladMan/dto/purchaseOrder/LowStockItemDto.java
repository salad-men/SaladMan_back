package com.kosta.saladMan.dto.purchaseOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LowStockItemDto {
    private String name;
    private String category;
    private Integer quantity;
    private String unit;
}
