package com.kosta.saladMan.dto.menu;

import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.kosta.saladMan.entity.menu.TotalMenu;
import com.kosta.saladMan.entity.menu.MenuCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalMenuDto {
    private Integer id;
    private Integer categoryId;
    private String name;
    private Integer originPrice;
    private Integer salePrice;
    private String img;
    private String description;
    private LocalDate createdAt;

    public TotalMenu toEntity() {
        return TotalMenu.builder()
                .id(id)
                .category(MenuCategory.builder().id(categoryId).build())
                .name(name)
                .originPrice(originPrice)
                .salePrice(salePrice)
                .img(img)
                .description(description)
                .createdAt(createdAt)
                .build();
    }
}
