package com.kosta.saladMan.dto.storeInquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreInventoryResponseDto {
	private String storeName;
    private String ingredientName;
    private String unit;
    private Integer quantity;
    private Double latitude;
    private Double longitude;
}
