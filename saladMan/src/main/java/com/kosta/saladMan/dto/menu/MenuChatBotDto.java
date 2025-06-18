package com.kosta.saladMan.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MenuChatBotDto {
    private String name;
    private Integer salePrice;
}