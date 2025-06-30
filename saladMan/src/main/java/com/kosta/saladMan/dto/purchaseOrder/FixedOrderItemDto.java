package com.kosta.saladMan.dto.purchaseOrder;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderItem;
import com.kosta.saladMan.entity.purchaseOrder.FixedOrderTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedOrderItemDto {
    private Integer id;
    private Integer ingredientId;
    private Integer fixedOrderTemplateId;
    private Integer quantity;
    
    private Integer autoOrderQty;    // 자동발주 시 주문 수량 (⭐ 새로 추가)
    private Boolean autoOrderEnabled; // 품목별 자동발주 여부 (⭐ 새로 추가)

    private String ingredientName;
    private String categoryName;
    private String unit;
    private Integer minimumOrderUnit;

    private Integer minQuantity;      // StoreIngredientSetting
    private Integer maxQuantity;      // StoreIngredientSetting
    
    public FixedOrderItem toEntity() {
        return FixedOrderItem.builder()
                .id(id)
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .fixedOrderTemplate(FixedOrderTemplate.builder().id(fixedOrderTemplateId).build())
                .quantity(quantity)
                .autoOrderQty(autoOrderQty)
                .autoOrderEnabled(autoOrderEnabled)
                .build();
    }
}
