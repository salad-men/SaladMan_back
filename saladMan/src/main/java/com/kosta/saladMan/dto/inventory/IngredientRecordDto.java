package com.kosta.saladMan.dto.inventory;

import java.time.LocalDate;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.IngredientRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientRecordDto {
    private Integer id;
    private Integer ingredientId;
    private Integer changeQuantity;
    private String changeType;
    private LocalDate recordedAt;
    private String memo;
		
    public IngredientRecord toEntity() {
        return IngredientRecord.builder()
                .id(id)
                .ingredient(Ingredient.builder().id(ingredientId).build())
                .changeQuantity(changeQuantity)
                .changeType(changeType)
                .recordedAt(recordedAt)
                .memo(memo)
                .build();
    }
}
