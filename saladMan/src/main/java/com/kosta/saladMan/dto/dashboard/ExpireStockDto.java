package com.kosta.saladMan.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExpireStockDto {
    private Integer id;
    private String categoryName;
    private String ingredientName;
    private Integer quantity;
    private String unit;
    private LocalDate expiredDate;
}
