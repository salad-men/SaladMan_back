package com.kosta.saladMan.dto.saleOrder;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentListDto {
	private Integer id;
	private LocalDateTime orderTime;
	private String status;
	private Integer totalPrice;
	private Integer storeId;
	//sale_order_item
	private Integer menuId;
	private Integer quantity;
	//total_menu
	private String name;
	
	
}
