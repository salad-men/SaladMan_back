package com.kosta.saladMan.entity.inventory;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.kosta.saladMan.dto.inventory.InventoryRecordDto;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false) // 본사도 Store로 간주
    private Store store;

    private Integer quantity;
    private String changeType; // 입고 / 출고
    private String memo;
    
    @CreationTimestamp
    private LocalDateTime date;
    
    public InventoryRecordDto toDto() {
        return InventoryRecordDto.builder()
                .id(id)
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .categoryName(ingredient.getCategory().getName())
                .storeId(store.getId())
                .storeName(store.getName())
                .quantity(quantity)
                .memo(memo)
                .changeType(changeType)
                .date(date)
                .build();
    }
}
