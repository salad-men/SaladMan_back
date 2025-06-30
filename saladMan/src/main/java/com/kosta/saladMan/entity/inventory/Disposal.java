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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.kosta.saladMan.dto.inventory.DisposalDto;
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
public class Disposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // id (폐기번호)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ingredient_id")
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "store_id")
    private Store store;

    private Integer quantity;  // 폐기량

    private String status;

    @CreationTimestamp
    private LocalDate requestedAt;

    @CreationTimestamp
    private LocalDate processedAt;

    @Column(length = 255)
    private String memo;
    
    
    private Integer storeIngredientId;

    
    
    public DisposalDto toDto() {
        return DisposalDto.builder()
            .id(id)
            .ingredientId(ingredient != null ? ingredient.getId() : null)
            .ingredientName(ingredient != null ? ingredient.getName() : null)
            .categoryName(ingredient != null && ingredient.getCategory() != null ? ingredient.getCategory().getName() : null)
            .unit(ingredient != null ? ingredient.getUnit() : null)
            .storeId(store != null ? store.getId() : null)
            .storeName(store != null ? store.getName() : null)
            .quantity(quantity)
            .status(status)
            .requestedAt(requestedAt)
            .processedAt(processedAt)
            .memo(memo)
            .build();
    }
}
