 package com.kosta.saladMan.dto.menu;

import java.time.LocalDate;
import java.util.List;

import com.kosta.saladMan.entity.menu.MenuCategory;
import com.kosta.saladMan.entity.menu.TotalMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    //이미지조회용
    private String categoryName;

    //성분 조회
    private List<MenuIngredientViewDto> ingredients;
    
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
    
    public static TotalMenuDto fromEntity(TotalMenu menu) {
        return TotalMenuDto.builder()
                .id(menu.getId())
                .categoryId(menu.getCategory().getId())
                .name(menu.getName())
                .originPrice(menu.getOriginPrice())
                .salePrice(menu.getSalePrice())
                .img(menu.getImg())
                .description(menu.getDescription())
                .createdAt(menu.getCreatedAt())
                .build();
    }
}
