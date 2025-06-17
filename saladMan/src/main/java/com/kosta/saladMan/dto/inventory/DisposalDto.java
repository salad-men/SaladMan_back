package com.kosta.saladMan.dto.inventory;

import java.time.LocalDate;

import com.kosta.saladMan.entity.inventory.Disposal;
import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisposalDto {
    private Integer id;
    private Integer ingredientId;
    private String ingredientName;   
    private String categoryName;
    private String unit;             

    private Integer storeId;
    private String storeName;

    private Integer quantity;
    private String status;
    private LocalDate requestedAt;
    private LocalDate processedAt;
    private String memo;

    public Disposal toEntity() {
        return Disposal.builder()
                .id(id)
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .store(Store.builder().id(storeId).build())
                .quantity(quantity)
                .status(status)
                .requestedAt(requestedAt)
                .processedAt(processedAt)
                .memo(memo)
                .build();
    }
}
