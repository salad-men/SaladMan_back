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
	private Integer id;
    private String img;
    private String name;
    private Integer salePrice;
    private Boolean status;

}
