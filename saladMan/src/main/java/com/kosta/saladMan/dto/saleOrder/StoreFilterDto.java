package com.kosta.saladMan.dto.saleOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreFilterDto {
	private Integer id;
    private String name;
    private String location;

}
