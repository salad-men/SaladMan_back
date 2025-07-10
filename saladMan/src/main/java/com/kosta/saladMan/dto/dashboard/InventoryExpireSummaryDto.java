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
public class InventoryExpireSummaryDto {
    private List<Item> top3;
    private int totalCount;
    private int d1Count;
    private int todayCount;

    @Data
    public static class Item {
        private String ingredientName;
        private String categoryName;
        private int remainQuantity;
        private String expiredDate;

    }
}
