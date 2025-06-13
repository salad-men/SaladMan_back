package com.kosta.saladMan.entity.inventory;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.inventory.StoreIngredientDto;
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
public class StoreIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // si_id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private IngredientCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    private Integer expiredQuantity;

    private Integer quantity;  // s_quantity

    public StoreIngredientDto toDto() {
        return StoreIngredientDto.builder()
                .id(id)
                .categoryId(category != null ? category.getId() : null)
                .ingredientId(ingredient != null ? ingredient.getId() : null)
                .storeId(store != null ? store.getId() : null)
                .expiredQuantity(expiredQuantity)
                .quantity(quantity)
                .build();
    }
}
