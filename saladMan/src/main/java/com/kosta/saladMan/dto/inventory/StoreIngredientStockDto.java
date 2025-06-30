package com.kosta.saladMan.dto.inventory;

import java.time.LocalDate;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.StoreIngredientStock;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreIngredientStockDto {
	private Integer id;

    private Integer quantity;

    private LocalDate expiredDate;

    private Integer minimumOrderUnit;

    private LocalDate receivedDate;

    private Integer unitCost;

    private Integer storeId;

    private Integer ingredientId;

    private Integer purchaseOrderId;

    // toEntity 메서드
    public StoreIngredientStock toEntity(Store store, Ingredient ingredient, PurchaseOrder purchaseOrder) {
        return StoreIngredientStock.builder()
                .id(id)
                .quantity(quantity)
                .expiredDate(expiredDate)
                .minimumOrderUnit(minimumOrderUnit)
                .receivedDate(receivedDate)
                .unitCost(unitCost)
                .store(store)
                .ingredient(ingredient)
                .purchaseOrder(purchaseOrder)
                .build();
    }
}
