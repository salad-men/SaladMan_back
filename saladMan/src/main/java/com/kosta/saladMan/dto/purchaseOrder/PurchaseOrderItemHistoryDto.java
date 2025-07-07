package com.kosta.saladMan.dto.purchaseOrder;

import java.util.List;

import com.kosta.saladMan.dto.inventory.StoreIngredientStockDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItemHistoryDto {
    private Integer id;
    private Integer purchaseOrderId;
    private Integer ingredientId;
    private String ingredientName;
    private String categoryName;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private Integer unitCost;
    private Integer totalPrice;
    private String approvalStatus;
    private String rejectionReason;
    private String unit;
    private List<StoreIngredientStockDto> receivedStockList;
}
