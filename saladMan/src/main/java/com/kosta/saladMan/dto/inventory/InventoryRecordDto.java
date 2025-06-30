package com.kosta.saladMan.dto.inventory;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kosta.saladMan.entity.inventory.Ingredient;
import com.kosta.saladMan.entity.inventory.InventoryRecord;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRecordDto {

	 private Integer id;
	    private Integer ingredientId;
	    private String ingredientName;
	    private String categoryName;
	    private Integer storeId;
	    private String storeName;
	    private Integer quantity;
	    private String memo;
	    private String changeType;
	    private LocalDateTime date;
    
    public InventoryRecord toEntity(Ingredient ingredient, Store store) {
        return InventoryRecord.builder()
                .ingredient(ingredient)
                .store(store)
                .quantity(quantity)
                .memo(memo)
                .changeType(changeType)
                .date(date)
                .build();
    }
}
