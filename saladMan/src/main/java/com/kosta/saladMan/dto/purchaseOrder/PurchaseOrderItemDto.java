package com.kosta.saladMan.dto.purchaseOrder;


import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemDto {
    private Integer id;
    private Integer purchaseOrderId;
    private Integer ingredientId;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private Integer totalPrice;
    private String inspection;
    private String inspectionNote;
    
    private String approvalStatus; // 승인, 반려, 대기중
    private String rejectionReason; // 반려 사유 (nullable)
    private String ingredientName;
    private String categoryName;
    private Integer unitCost;
    private String orderStatus;
	private String storeName;

    


    public PurchaseOrderItem toEntity() {
        return PurchaseOrderItem.builder()
                .id(id)
                .purchaseOrder(PurchaseOrder.builder().id(purchaseOrderId).build())
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .orderedQuantity(orderedQuantity)
                .receivedQuantity(receivedQuantity)
                .totalPrice(totalPrice)
                .inspection(inspection)
                .inspectionNote(inspectionNote)
                .build();
    }
}
