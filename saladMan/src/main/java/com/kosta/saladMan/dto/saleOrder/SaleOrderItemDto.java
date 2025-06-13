package com.kosta.saladMan.dto.saleOrder;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.entity.saleOrder.SaleOrderItem;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrderItemDto {
    private Integer id;
    private Integer saleOrderId;
    private Integer menuId;
    private Integer quantity;
    private Integer price;

    public SaleOrderItem toEntity() {
        return SaleOrderItem.builder()
                .id(id)
                .saleOrder(SaleOrder.builder().id(saleOrderId).build())
                .menuId(menuId)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
