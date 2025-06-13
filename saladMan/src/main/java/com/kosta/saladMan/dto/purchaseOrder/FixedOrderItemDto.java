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

    public FixedOrderItem toEntity() {
        return FixedOrderItem.builder()
                .id(id)
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .fixedOrderTemplate(FixedOrderTemplate.builder().id(fixedOrderTemplateId).build())
                .quantity(quantity)
                .build();
    }
}
