package com.kosta.saladMan.entity.inventory;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.inventory.HqIngredientDto;
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
public class HqIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // hq_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private IngredientCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;
    
    @Column(nullable = false)
    private Integer unitCost;

    private Integer minimumOrderUnit;

    private Integer quantity;  // hq_quantity
    
    @Column(nullable = false)
    private LocalDate expiredDate;
    
    @Column(nullable = false)
    private LocalDate receivedDate;
    
    private Integer reservedQuantity;

    public HqIngredientDto toDto() {
        return HqIngredientDto.builder()
                .id(id)
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)         
                .ingredientId(ingredient != null ? ingredient.getId() : null)
                .ingredientName(ingredient != null ? ingredient.getName() : null)
                .storeName(store != null ? store.getName() : null)  
                .unit(ingredient != null ? ingredient.getUnit() : null)
                .unitCost(unitCost)
                .minimumOrderUnit(minimumOrderUnit)
                .quantity(quantity)
                .expiredDate(expiredDate)
                .receivedDate(receivedDate)
                .reservedQuantity(reservedQuantity)
                .build();
    }

}
