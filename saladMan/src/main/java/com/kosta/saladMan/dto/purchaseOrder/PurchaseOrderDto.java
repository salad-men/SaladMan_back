package com.kosta.saladMan.dto.purchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime orderDateTime;
    private String status;
    private String requestedBy;
    private Integer totalPrice;
    private String purType; //발주유형
    private String qrImg;
    
    private String productNameSummary; //이름 요약 ex.양상추 외 3
    private String quantitySummary; //수량 요약 5/7
    private boolean receiptAvailable;    // 입고 상태면 true


    public PurchaseOrder toEntity() {
        return PurchaseOrder.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .orderDateTime(orderDateTime)
                .status(status)
                .requestedBy(requestedBy)
                .totalPrice(totalPrice)
                .purType(purType)
                .qrImg(qrImg)
                
                .build();
    }
}
