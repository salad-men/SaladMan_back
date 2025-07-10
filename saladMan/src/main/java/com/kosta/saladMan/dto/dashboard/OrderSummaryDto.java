package com.kosta.saladMan.dto.dashboard;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryDto {
    private int totalCount;
    private List<OrderItemDto> top3;

    @Data
    public static class OrderItemDto {
        private String ingredientName;
        private String categoryName;
        private int orderCount;
        private String lastOrderDate;
    }
}
