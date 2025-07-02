package com.kosta.saladMan.dto.kiosk;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareDto {
    private Integer storeId;
    private List<ItemDto> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDto {
        private Integer menuId;
        private Integer quantity;
        private Integer price;
    }
}
