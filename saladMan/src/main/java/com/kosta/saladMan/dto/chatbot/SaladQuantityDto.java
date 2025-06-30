package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaladQuantityDto {
    private Integer menuId;
    private String menuName;
    private Double totalQuantity;
}