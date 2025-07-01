package com.kosta.saladMan.dto.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StoreTimeResponseDto {
    private String name;
    private String address;
    private String openTime;
    private String closeTime;
    private String breakDay;
}
