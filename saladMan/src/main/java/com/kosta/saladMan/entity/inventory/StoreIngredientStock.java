package com.kosta.saladMan.entity.inventory;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.inventory.StoreIngredientStockDto;
import com.kosta.saladMan.entity.purchaseOrder.PurchaseOrder;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DynamicInsert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreIngredientStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private Integer quantity;

    private LocalDate expiredDate;

    private Integer minimumOrderUnit;

    @CreationTimestamp
    private LocalDate receivedDate;

    private Integer unitCost;

    public StoreIngredientStockDto toDto() {
        return StoreIngredientStockDto.builder()
                .id(id)
                .quantity(quantity)
                .expiredDate(expiredDate)
                .minimumOrderUnit(minimumOrderUnit)
                .receivedDate(receivedDate)
                .unitCost(unitCost)
                .storeId(store != null ? store.getId() : null)
                .ingredientId(ingredient != null ? ingredient.getId() : null)
                .purchaseOrderId(purchaseOrder != null ? purchaseOrder.getId() : null)
                .build();
    }
}
