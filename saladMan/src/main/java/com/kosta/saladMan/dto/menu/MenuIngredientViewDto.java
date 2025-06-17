package com.kosta.saladMan.dto.menu;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuIngredientViewDto {
    private String name;
    private Integer quantity;
}
