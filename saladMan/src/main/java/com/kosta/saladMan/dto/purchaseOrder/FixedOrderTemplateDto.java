package com.kosta.saladMan.dto.purchaseOrder;

import com.kosta.saladMan.entity.purchaseOrder.FixedOrderTemplate;
import com.kosta.saladMan.entity.store.Store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedOrderTemplateDto {
    private Integer id;
    private Integer storeId;
    private String name;

    public FixedOrderTemplate toEntity() {
        return FixedOrderTemplate.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .name(name)
                .build();
    }
}
