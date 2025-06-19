package com.kosta.saladMan.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreMenuStatusDto {
	
	private Integer menuId;
    private String menuName;
    private Integer salePrice;
    private String img;
    private Boolean status; 

}
