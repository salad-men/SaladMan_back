package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientMenuDto {
    private String name;         // 샐러드 이름
    private String description;  // 샐러드 설명
}
