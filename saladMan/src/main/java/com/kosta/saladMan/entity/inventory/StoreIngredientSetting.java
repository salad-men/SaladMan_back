package com.kosta.saladMan.entity.inventory;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.inventory.StoreIngredientSettingDto;
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
public class StoreIngredientSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // id (매장별재료설정번호)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    private Integer minQuantity;

    private Integer maxQuantity;
    
    public StoreIngredientSettingDto toDto() {
        return StoreIngredientSettingDto.builder()
            .id(id)
            .storeId(store.getId())
            .ingredientId(ingredient.getId())
            .minQuantity(minQuantity)
            .maxQuantity(maxQuantity)
            .categoryName(ingredient.getCategory() != null ? ingredient.getCategory().getName() : null)
            .build();
    }

}
