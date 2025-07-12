package com.kosta.saladMan.dto.storeInquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NearbyInventoryDto {
	 private String storeName;
	 private String ingredientName;
	 private String unit;
	 private Integer storeId;
	 private Integer ingredientId;
	 private Integer totalQuantity;
	 private Double lat;
	 private Double lng;

}
