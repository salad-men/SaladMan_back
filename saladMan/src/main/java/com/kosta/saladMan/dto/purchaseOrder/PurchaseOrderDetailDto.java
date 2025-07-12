package com.kosta.saladMan.dto.purchaseOrder;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDetailDto {
    private Integer purchaseOrderId;
    private String storeName;
    private String status;
    private String orderStatus;
    private LocalDateTime orderDateTime;
    private String purType;
    private Integer totalPrice;
    private String requestedBy;
    private String qrImg;
    private List<PurchaseOrderItemHistoryDto> items;
}
