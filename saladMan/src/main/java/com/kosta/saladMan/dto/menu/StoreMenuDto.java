package com.kosta.saladMan.dto.menu;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.kosta.saladMan.entity.store.Store;
import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.entity.menu.StoreMenu;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreMenuDto {
    private Integer id;
    private Integer storeId;
    private Integer menuId;
    private Boolean status;
    private Boolean isSoldOut;

    public StoreMenu toEntity() {
        return StoreMenu.builder()
                .id(id)
                .store(Store.builder().id(storeId).build())
                .menu(TotalMenu.builder().id(menuId).build())
                .status(status)
                .isSoldOut(isSoldOut)
                .build();
    }
}
