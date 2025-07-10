package com.kosta.saladMan.dto.dashboard;


import java.time.LocalDate;
import java.util.List;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisposalSummaryDto {
    private List<Item> top3;
    private int totalCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private String ingredientName;
        private String categoryName;
        private int quantity;
        private String requestedAt;
    }
}
