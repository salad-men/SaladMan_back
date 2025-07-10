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
    private Integer ingredientId;      // 재료ID
    private String ingredientName;     // 재료명
    private String categoryName;       // 분류명(선택)
    private Integer totalUsedQuantity; // 사용(출고) 합계수량
    private String unit;               // 단위(kg, 개 등)
}
