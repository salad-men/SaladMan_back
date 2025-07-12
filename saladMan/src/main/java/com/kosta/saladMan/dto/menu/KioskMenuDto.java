package com.kosta.saladMan.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KioskMenuDto {
	private Integer id; //storeMenu테이블 id
	private Integer menuId;
    private String img;
    private String name;
    private Integer salePrice;
    private Boolean status;
    private Boolean isSoldOut;
    
    private String categoryName;
    private Integer categoryId;
}
