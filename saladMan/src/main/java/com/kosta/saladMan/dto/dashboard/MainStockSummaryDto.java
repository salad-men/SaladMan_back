package com.kosta.saladMan.dto.dashboard;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainStockSummaryDto {
    private Integer ingredientId;
    private String ingredientName;
    private String categoryName;
    private Integer totalUsedQuantity;
    private String unit;          
}
