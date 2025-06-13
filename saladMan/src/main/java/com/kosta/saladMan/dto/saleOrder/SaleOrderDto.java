package com.kosta.saladMan.dto.saleOrder;

import java.time.LocalDateTime;
import lombok.*;

import com.kosta.saladMan.entity.saleOrder.SaleOrder;
import com.kosta.saladMan.entity.store.Store;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrderDto {
    private Integer id;
    private Integer storeId;
    private LocalDateTime orderTime;
    private String status;
    private Integer totalPrice;

    public SaleOrder toEntity() {
        return SaleOrder.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .orderTime(orderTime)
                .status(status)
                .totalPrice(totalPrice)
                .build();
    }
}
