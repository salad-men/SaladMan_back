package com.kosta.saladMan.dto.purchaseOrder;

import java.time.LocalDate;

import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PurchaseOrderDto {
    private Integer id;
    private Integer storeId;
    private LocalDate orderDate;
    private String status;
    private String requestedBy;
    private Integer totalPrice;
    private String category;
    private String qrImg;

    public PurchaseOrder toEntity() {
        return PurchaseOrder.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .orderDate(orderDate)
                .status(status)
                .requestedBy(requestedBy)
                .totalPrice(totalPrice)
                .category(category)
                .qrImg(qrImg)
                .build();
    }
}
