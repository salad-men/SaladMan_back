package com.kosta.saladMan.dto.menu;


import com.kosta.saladMan.entity.menu.MenuCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryDto {
    private Integer id;
    private String name;

    public MenuCategory toEntity() {
        return MenuCategory.builder()
                .id(id)
                .name(name)
                .build();
    }
}
